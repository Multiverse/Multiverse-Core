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
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.RemovePlayerFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.result.AsyncAttemptsAggregate;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.PlayerWorldTeleporter;
import org.mvplugins.multiverse.core.world.options.UnloadWorldOptions;

@Service
class UnloadCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final PlayerWorldTeleporter playerWorldTeleporter;
    private final UnloadCommand.Flags flags;

    @Inject
    UnloadCommand(
            @NotNull WorldManager worldManager,
            @NotNull PlayerWorldTeleporter playerWorldTeleporter,
            @NotNull Flags flags
    ) {
        this.worldManager = worldManager;
        this.playerWorldTeleporter = playerWorldTeleporter;
        this.flags = flags;
    }

    @Subcommand("unload")
    @CommandPermission("multiverse.core.unload")
    @CommandCompletion("@mvworlds @flags:groupName=" + Flags.NAME)
    @Syntax("<world>")
    @Description("{@@mv-core.unload.description}")
    void onUnloadCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.unload.world.description}")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--remove-players] [--no-save]")
            @Description("{@@mv-core.gamerules.description.page}")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        issuer.sendInfo(MVCorei18n.UNLOAD_UNLOADING, Replace.WORLD.with(world.getAliasOrName()));

        var future = parsedFlags.hasFlag(flags.removePlayers)
                ? playerWorldTeleporter.removeFromWorld(world)
                : AsyncAttemptsAggregate.emptySuccess();

        future.onSuccess(() -> doWorldUnloading(issuer, world, parsedFlags))
                .onFailure(() -> issuer.sendError("Failed to teleport one or more players out of the world!"));
    }

    private void doWorldUnloading(MVCommandIssuer issuer, LoadedMultiverseWorld world, ParsedCommandFlags parsedFlags) {
        UnloadWorldOptions unloadWorldOptions = UnloadWorldOptions.world(world)
                .saveBukkitWorld(!parsedFlags.hasFlag(flags.noSave));
        worldManager.unloadWorld(unloadWorldOptions)
                .onSuccess(loadedWorld -> {
                    Logging.fine("World unload success: " + loadedWorld);
                    issuer.sendInfo(MVCorei18n.UNLOAD_SUCCESS, Replace.WORLD.with(loadedWorld.getName()));
                }).onFailure(failure -> {
                    Logging.fine("World unload failure: " + failure);
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

        private final CommandFlag noSave = flag(CommandFlag.builder("--no-save")
                .addAlias("-n")
                .build());
    }

    @Service
    private static final class LegacyAlias extends UnloadCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(
                @NotNull WorldManager worldManager,
                @NotNull PlayerWorldTeleporter playerWorldTeleporter,
                @NotNull Flags flags
        ) {
            super(worldManager, playerWorldTeleporter, flags);
        }

        @Override
        @CommandAlias("mvunload")
        void onUnloadCommand(MVCommandIssuer issuer, LoadedMultiverseWorld world, String[] flags) {
            super.onUnloadCommand(issuer, world, flags);
        }
    }
}
