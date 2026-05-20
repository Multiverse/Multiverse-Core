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
import org.mvplugins.multiverse.core.command.flags.RemovePlayerDestinationFlags;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.destination.core.WorldDestination;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.result.AsyncAttemptsAggregate;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.PlayerWorldTeleporter;
import org.mvplugins.multiverse.core.world.options.RemoveWorldOptions;

import java.util.Objects;

@Service
class RemoveCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final PlayerWorldTeleporter playerWorldTeleporter;
    private final Flags flags;

    @Inject
    RemoveCommand(
            @NotNull WorldManager worldManager,
            @NotNull PlayerWorldTeleporter playerWorldTeleporter,
            @NotNull Flags flags
    ) {
        this.worldManager = worldManager;
        this.playerWorldTeleporter = playerWorldTeleporter;
        this.flags = flags;
    }

    @Subcommand("remove")
    @CommandPermission("multiverse.core.remove")
    @CommandCompletion("@mvworlds:scope=both @flags:groupName=" + Flags.NAME)
    @Syntax("<world> [--remove-players [destination]] [--no-unload-bukkit-world] [--no-save]")
    @Description("{@@mv-core.remove.description}")
    void onRemoveCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.remove.world.description}")
            MultiverseWorld world,

            @Optional
            @Syntax("[--remove-players [destination]] [--no-unload-bukkit-world] [--no-save]")
            @Description("")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        DestinationInstance<?, ?> removeToDestination = parsedFlags.flagValue(flags.removePlayers);
        var future = Objects.nonNull(removeToDestination)
                ? world.asLoadedWorld()
                  .map(loadedWorld -> playerWorldTeleporter.transferAllFromWorldToDestination(loadedWorld, removeToDestination))
                  .getOrElse(AsyncAttemptsAggregate::emptySuccess)
                : AsyncAttemptsAggregate.emptySuccess();

        future.onSuccess(() -> doWorldRemoving(issuer, world, parsedFlags))
                .onFailure(() -> issuer.sendError("Failed to teleport one or more players out of the world!"));
    }

    private void doWorldRemoving(MVCommandIssuer issuer, MultiverseWorld world, ParsedCommandFlags parsedFlags) {
        worldManager.removeWorld(RemoveWorldOptions.world(world)
                        .saveBukkitWorld(!parsedFlags.hasFlag(flags.noSave))
                        .unloadBukkitWorld(!parsedFlags.hasFlag(flags.noUnloadBukkitWorld)))
                .onSuccess(removedWorldName -> {
                    Logging.fine("World remove success: " + removedWorldName);
                    issuer.sendInfo(MVCorei18n.REMOVE_SUCCESS, Replace.WORLD.with(removedWorldName));
                }).onFailure(failure -> {
                    Logging.fine("World remove failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }

    @Service
    private static final class Flags extends RemovePlayerDestinationFlags {

        private static final String NAME = "mvremove";

        @Inject
        private Flags(
                @NotNull CommandFlagsManager flagsManager,
                @NotNull WorldManager worldManager,
                @NotNull DestinationsProvider destinationsProvider,
                @NotNull WorldDestination worldDestination
        ) {
            super(NAME, flagsManager, worldManager, destinationsProvider, worldDestination);
        }

        private final CommandFlag noUnloadBukkitWorld = flag(CommandFlag.builder("--no-unload-bukkit-world")
                .addAlias("-b")
                .build());

        private final CommandFlag noSave = flag(CommandFlag.builder("--no-save")
                .addAlias("-n")
                .build());
    }

    @Service
    private static final class LegacyAlias extends RemoveCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(
                @NotNull WorldManager worldManager,
                @NotNull PlayerWorldTeleporter playerWorldTeleporter,
                @NotNull Flags flags
        ) {
            super(worldManager, playerWorldTeleporter, flags);
        }

        @Override
        @CommandAlias("mvremove")
        void onRemoveCommand(MVCommandIssuer issuer, MultiverseWorld world, String[] flags) {
            super.onRemoveCommand(issuer, world, flags);
        }
    }
}
