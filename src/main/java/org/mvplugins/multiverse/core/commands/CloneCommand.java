package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.FlagBuilder;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.options.CloneWorldOptions;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
class CloneCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final CloneCommand.Flags flags;

    @Inject
    CloneCommand(@NotNull WorldManager worldManager, @NotNull Flags flags) {
        this.worldManager = worldManager;
        this.flags = flags;
    }

    @Subcommand("clone")
    @CommandPermission("multiverse.core.clone")
    @CommandCompletion("@mvworlds:scope=loaded @empty @flags:groupName=" + Flags.NAME)
    @Syntax("<world> <new-world-name> [--reset-world-config --reset-gamerules --reset-world-border]")
    @Description("{@@mv-core.clone.description}")
    void onCloneCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.clone.world.description}")
            LoadedMultiverseWorld world,

            @Syntax("<new-world-name>")
            @Description("{@@mv-core.clone.newWorld.description}")
            String newWorldName,

            @Optional
            @Syntax("[--reset-world-config --reset-gamerules --reset-world-border]")
            @Description("{@@mv-core.regen.other.description}")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        issuer.sendInfo(MVCorei18n.CLONE_CLONING,
                Replace.WORLD.with(world.getName()),
                replace("{newworld}").with(newWorldName));
        CloneWorldOptions cloneWorldOptions = CloneWorldOptions.fromTo(world, newWorldName)
                .keepWorldConfig(!parsedFlags.hasFlag(flags.resetWorldConfig))
                .keepGameRule(!parsedFlags.hasFlag(flags.resetGamerules))
                .keepWorldBorder(!parsedFlags.hasFlag(flags.resetWorldBorder));
        worldManager.cloneWorld(cloneWorldOptions)
                .onSuccess(newWorld -> {
                    Logging.fine("World clone success: " + newWorld);
                    issuer.sendInfo(MVCorei18n.CLONE_SUCCESS, Replace.WORLD.with(newWorld.getName()));
                }).onFailure(failure -> {
                    Logging.fine("World clone failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }

    @Service
    private static final class Flags extends FlagBuilder {

        private static final String NAME = "mvclone";

        @Inject
        private Flags(@NotNull CommandFlagsManager flagsManager) {
            super(NAME, flagsManager);
        }

        private final CommandFlag resetWorldConfig = flag(CommandFlag.builder("--reset-world-config")
                .addAlias("-wc")
                .build());

        private final CommandFlag resetGamerules = flag(CommandFlag.builder("--reset-gamerules")
                .addAlias("-gm")
                .build());

        private final CommandFlag resetWorldBorder = flag(CommandFlag.builder("--reset-world-border")
                .addAlias("-wb")
                .build());
    }

    @Service
    private final static class LegacyAlias extends CloneCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull WorldManager worldManager, @NotNull Flags flags) {
            super(worldManager, flags);
        }

        @Override
        @CommandAlias("mvcl|mvclone")
        void onCloneCommand(MVCommandIssuer issuer, LoadedMultiverseWorld world, String newWorldName, String[] flags) {
            super.onCloneCommand(issuer, world, newWorldName, flags);
        }
    }
}
