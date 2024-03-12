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

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.options.CloneWorldOptions;

@Service
@CommandAlias("mv")
class CloneCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    private final CommandFlag RESET_WORLD_CONFIG_FLAG = flag(CommandFlag.builder("--reset-world-config")
            .addAlias("-wc")
            .build());

    private final CommandFlag RESET_GAMERULES_FLAG = flag(CommandFlag.builder("--reset-gamerules")
            .addAlias("-gm")
            .build());

    private final CommandFlag RESET_WORLD_BORDER_FLAG = flag(CommandFlag.builder("--reset-world-border")
            .addAlias("-wb")
            .build());

    @Inject
    CloneCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("clone")
    @CommandPermission("multiverse.core.clone")
    @CommandCompletion("@mvworlds:scope=loaded @empty @flags:groupName=mvclonecommand")
    @Syntax("<world> <new world name>")
    @Description("{@@mv-core.clone.description}")
    void onCloneCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.clone.world.description}")
            LoadedMultiverseWorld world,

            @Syntax("<new world name>")
            @Description("{@@mv-core.clone.newWorld.description}")
            String newWorldName,

            @Optional
            @Syntax(/* TODO */ "")
            @Description("{@@mv-core.regen.other.description}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.CLONE_CLONING, "{world}", world.getName(), "{newworld}", newWorldName);
        CloneWorldOptions cloneWorldOptions = CloneWorldOptions.fromTo(world, newWorldName)
                .keepWorldConfig(!parsedFlags.hasFlag(RESET_WORLD_CONFIG_FLAG))
                .keepGameRule(!parsedFlags.hasFlag(RESET_GAMERULES_FLAG))
                .keepWorldBorder(!parsedFlags.hasFlag(RESET_WORLD_BORDER_FLAG));
        worldManager.cloneWorld(cloneWorldOptions)
                .onSuccess(newWorld -> {
                    Logging.fine("World clone success: " + newWorld);
                    issuer.sendInfo(MVCorei18n.CLONE_SUCCESS, "{world}", newWorld.getName());
                }).onFailure(failure -> {
                    Logging.fine("World clone failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }
}
