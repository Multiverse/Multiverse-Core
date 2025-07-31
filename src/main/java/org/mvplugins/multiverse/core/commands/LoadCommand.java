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
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.RemovePlayerFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.options.LoadWorldOptions;

@Service
class LoadCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final LoadCommand.Flags flags;

    @Inject
    LoadCommand(@NotNull WorldManager worldManager, @NotNull Flags flags) {
        this.worldManager = worldManager;
        this.flags = flags;
    }

    @Subcommand("load")
    @CommandPermission("multiverse.core.load")
    @CommandCompletion("@mvworlds:scope=unloaded")
    @Syntax("<world>")
    @Description("{@@mv-core.load.description}")
    void onLoadCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.load.world.description}")
            MultiverseWorld world,

            @Optional
            @Syntax("[--remove-players]")
            @Description("")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        issuer.sendInfo(MVCorei18n.LOAD_LOADING, Replace.WORLD.with(world.getName()));
        worldManager.loadWorld(LoadWorldOptions.world(world)
                        .doFolderCheck(!parsedFlags.hasFlag(flags.skipFolderCheck)))
                .onSuccess(newWorld -> {
                    Logging.fine("World load success: " + newWorld);
                    issuer.sendInfo(MVCorei18n.LOAD_SUCCESS, Replace.WORLD.with(newWorld.getName()));
                }).onFailure(failure -> {
                    Logging.fine("World load failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }

    @Service
    private static final class Flags extends RemovePlayerFlags {

        private static final String NAME = "mvunload";

        @Inject
        private Flags(@NotNull CommandFlagsManager flagsManager) {
            super(NAME, flagsManager);
        }

        private final CommandFlag skipFolderCheck = flag(CommandFlag.builder("--skip-folder-check")
                .addAlias("-f")
                .build());
    }

    @Service
    private static final class LegacyAlias extends LoadCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull WorldManager worldManager, @NotNull Flags flags) {
            super(worldManager, flags);
        }

        @Override
        @CommandAlias("mvload")
        void onLoadCommand(MVCommandIssuer issuer, MultiverseWorld world, String[] flagArray) {
            super.onLoadCommand(issuer, world, flagArray);
        }
    }
}
