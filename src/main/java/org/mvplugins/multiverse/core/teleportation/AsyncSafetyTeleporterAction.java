package org.mvplugins.multiverse.core.teleportation;

import co.aikar.commands.BukkitCommandIssuer;
import com.dumptruckman.minecraft.util.Logging;
import io.papermc.lib.PaperLib;
import io.vavr.control.Either;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.api.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.api.teleportation.SafetyTeleporterAction;
import org.mvplugins.multiverse.core.api.teleportation.TeleportFailureReason;
import org.mvplugins.multiverse.core.api.destination.DestinationInstance;
import org.mvplugins.multiverse.core.api.event.MVTeleportDestinationEvent;
import org.mvplugins.multiverse.core.api.result.Async;
import org.mvplugins.multiverse.core.api.result.AsyncAttempt;
import org.mvplugins.multiverse.core.api.result.Attempt;

import java.util.List;

public class AsyncSafetyTeleporterAction implements SafetyTeleporterAction {

    private final BlockSafety blockSafety;
    private final TeleportQueue teleportQueue;
    private final PluginManager pluginManager;

    private final @NotNull Either<Location, DestinationInstance<?, ?>> locationOrDestination;
    private boolean checkSafety;
    private @Nullable CommandSender teleporter = null;

    AsyncSafetyTeleporterAction(
            @NotNull BlockSafety blockSafety,
            @NotNull TeleportQueue teleportQueue,
            @NotNull PluginManager pluginManager,
            @NotNull Either<Location, DestinationInstance<?, ?>> locationOrDestination) {
        this.blockSafety = blockSafety;
        this.teleportQueue = teleportQueue;
        this.pluginManager = pluginManager;
        this.locationOrDestination = locationOrDestination;
        this.checkSafety = locationOrDestination.fold(
                location -> true,
                destination -> destination != null && destination.checkTeleportSafety()
        );
    }

    @Override
    public SafetyTeleporterAction checkSafety(boolean checkSafety) {
        this.checkSafety = checkSafety;
        return this;
    }

    @Override
    public SafetyTeleporterAction by(@NotNull BukkitCommandIssuer issuer) {
        return by(issuer.getIssuer());
    }

    @Override
    public SafetyTeleporterAction by(@NotNull CommandSender teleporter) {
        this.teleporter = teleporter;
        return this;
    }

    @Override
    public <T extends Entity> Async<List<Attempt<Void, TeleportFailureReason>>> teleport(@NotNull List<T> teleportees) {
        return AsyncAttempt.allOf(teleportees.stream().map(this::teleport).toList());
    }

    @Override
    public AsyncAttempt<Void, TeleportFailureReason> teleport(@NotNull Entity teleportee) {
        var localTeleporter = this.teleporter == null ? teleportee : this.teleporter;
        return AsyncAttempt.fromAttempt(getLocation(teleportee).mapAttempt(this::doSafetyCheck))
                .onSuccess(() -> {
                    if (teleportee instanceof Player player) {
                        this.teleportQueue.addToQueue(localTeleporter, player);
                    }
                })
                .mapAsyncAttempt(location -> doAsyncTeleport(teleportee, location))
                .thenRun(() -> {
                    if (teleportee instanceof Player player) {
                        this.teleportQueue.popFromQueue(player.getName());
                    }
                });
    }

    private Attempt<Location, TeleportFailureReason> getLocation(@NotNull Entity teleportee) {
        return this.locationOrDestination.fold(
                location -> parseLocation(teleportee, location),
                destination -> parseDestination(teleportee, destination)
        );
    }

    private Attempt<Location, TeleportFailureReason> parseLocation(
            @NotNull Entity teleportee, @Nullable Location location) {
        if (location == null) {
            return Attempt.failure(TeleportFailureReason.NULL_LOCATION);
        }
        return Attempt.success(location);
    }

    private Attempt<Location, TeleportFailureReason> parseDestination(
            @NotNull Entity teleportee, @Nullable DestinationInstance<?, ?> destination) {
        if (destination == null) {
            return Attempt.failure(TeleportFailureReason.NULL_LOCATION);
        }
        MVTeleportDestinationEvent event = new MVTeleportDestinationEvent(destination, teleportee, teleporter);
        this.pluginManager.callEvent(event);
        if (event.isCancelled()) {
            return Attempt.failure(TeleportFailureReason.EVENT_CANCELLED);
        }
        return destination.getLocation(teleportee)
                .map(Attempt::<Location, TeleportFailureReason>success)
                .getOrElse(Attempt.failure(TeleportFailureReason.NULL_LOCATION));
    }

    private Attempt<Location, TeleportFailureReason> doSafetyCheck(@NotNull Location location) {
        if (!this.checkSafety) {
            return Attempt.success(location);
        }
        Location safeLocation = blockSafety.findSafeSpawnLocation(location);
        if (safeLocation == null) {
            return Attempt.failure(TeleportFailureReason.UNSAFE_LOCATION);
        }
        return Attempt.success(safeLocation);
    }

    private AsyncAttempt<Void, TeleportFailureReason> doAsyncTeleport(
            @NotNull Entity teleportee,
            @NotNull Location location) {
        return AsyncAttempt.of(PaperLib.teleportAsync(teleportee, location), exception -> {
            Logging.warning("Failed to teleport %s to %s: %s",
                    teleportee.getName(), location, exception.getMessage());
            return Attempt.failure(TeleportFailureReason.TELEPORT_FAILED_EXCEPTION);
        }).mapAttempt(success -> {
            if (success) {
                Logging.finer("Teleported async %s to %s", teleportee.getName(), location);
                return Attempt.success(null);
            }
            Logging.warning("Failed to async teleport %s to %s", teleportee.getName(), location);
            return Attempt.failure(TeleportFailureReason.TELEPORT_FAILED);
        });
    }
}
