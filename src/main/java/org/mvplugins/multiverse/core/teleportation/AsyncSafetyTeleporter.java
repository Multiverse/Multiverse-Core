package org.mvplugins.multiverse.core.teleportation;

import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import io.papermc.lib.PaperLib;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.BlockSafety;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.event.MVTeleportDestinationEvent;
import org.mvplugins.multiverse.core.utils.result.Async;
import org.mvplugins.multiverse.core.utils.result.AsyncAttempt;
import org.mvplugins.multiverse.core.utils.result.Attempt;

/**
 * Teleports entities safely and asynchronously.
 */
@Service
public class AsyncSafetyTeleporter {
    private final BlockSafety blockSafety;
    private final TeleportQueue teleportQueue;
    private final PluginManager pluginManager;

    @Inject
    AsyncSafetyTeleporter(
            @NotNull BlockSafety blockSafety,
            @NotNull TeleportQueue teleportQueue,
            @NotNull PluginManager pluginManager) {
        this.blockSafety = blockSafety;
        this.teleportQueue = teleportQueue;
        this.pluginManager = pluginManager;
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleportSafely(
            @NotNull Entity teleportee,
            @Nullable DestinationInstance<?, ?> destination) {
        return teleportSafely(null, teleportee, destination);
    }

    public <T extends Entity> Async<List<Attempt<Void, TeleportResult.Failure>>> teleportSafely(
            @Nullable CommandSender teleporter,
            @NotNull List<T> teleportees,
            @Nullable DestinationInstance<?, ?> destination) {
        return AsyncAttempt.allOf(teleportees.stream()
                .map(teleportee -> teleportSafely(teleporter, teleportee, destination))
                .toList());
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleportSafely(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable DestinationInstance<?, ?> destination) {
        if (destination == null) {
            return AsyncAttempt.failure(TeleportResult.Failure.NULL_DESTINATION);
        }
        MVTeleportDestinationEvent event = new MVTeleportDestinationEvent(destination, teleportee, teleporter);
        this.pluginManager.callEvent(event);
        if (event.isCancelled()) {
            return AsyncAttempt.failure(TeleportResult.Failure.EVENT_CANCELLED);
        }
        return destination.getLocation(teleportee)
                .map(location -> destination.checkTeleportSafety()
                        ? teleportSafely(teleporter, teleportee, location)
                        : teleport(teleporter, teleportee, location))
                .getOrElse(AsyncAttempt.failure(TeleportResult.Failure.NULL_LOCATION));
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleportSafely(
            @NotNull Entity teleportee,
            @Nullable Location location) {
        return teleportSafely(null, teleportee, location);
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleportSafely(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable Location location) {
        if (location == null) {
            return AsyncAttempt.failure(TeleportResult.Failure.NULL_LOCATION);
        }
        Location safeLocation = blockSafety.getSafeLocation(location);
        if (safeLocation == null) {
            return AsyncAttempt.failure(TeleportResult.Failure.UNSAFE_LOCATION);
        }
        return teleport(teleporter, teleportee, safeLocation);
    }

    public <T extends Entity> Async<List<Attempt<Void, TeleportResult.Failure>>> teleport(
            @NotNull List<T> teleportees,
            @Nullable DestinationInstance<?, ?> destination) {
        return AsyncAttempt.allOf(teleportees.stream()
                .map(teleportee -> teleport(teleportee, destination))
                .toList());
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleport(
            @NotNull Entity teleportee,
            @Nullable DestinationInstance<?, ?> destination) {
        return teleport(null, teleportee, destination);
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleport(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable DestinationInstance<?, ?> destination) {
       if (destination == null) {
           return AsyncAttempt.failure(TeleportResult.Failure.NULL_DESTINATION);
       }
        MVTeleportDestinationEvent event = new MVTeleportDestinationEvent(destination, teleportee, teleporter);
        this.pluginManager.callEvent(event);
        if (event.isCancelled()) {
            return AsyncAttempt.failure(TeleportResult.Failure.EVENT_CANCELLED);
        }
       return destination.getLocation(teleportee)
               .map(location -> teleport(teleporter, teleportee, location))
               .getOrElse(AsyncAttempt.failure(TeleportResult.Failure.NULL_LOCATION));
    }

    public <T extends Entity> Async<List<Attempt<Void, TeleportResult.Failure>>> teleport(
            @NotNull List<T> teleportees,
            @Nullable Location location) {
        return AsyncAttempt.allOf(teleportees.stream()
                .map(teleportee -> teleport(teleportee, location))
                .toList());
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleport(
            @NotNull Entity teleportee,
            @Nullable Location location) {
        return teleport(null, teleportee, location);
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleport(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable Location location) {
        if (location == null) {
            return AsyncAttempt.failure(TeleportResult.Failure.NULL_LOCATION);
        }

        boolean shouldAddToQueue = teleporter != null && teleportee instanceof Player;
        if (shouldAddToQueue) {
            teleportQueue.addToQueue(teleporter.getName(), teleportee.getName());
        }

        return doAsyncTeleport(teleportee, location, shouldAddToQueue);
    }

    private AsyncAttempt<Void, TeleportResult.Failure> doAsyncTeleport(
            @NotNull Entity teleportee,
            @NotNull Location location,
            boolean shouldRemoveFromQueue) {
        return AsyncAttempt.of(PaperLib.teleportAsync(teleportee, location), exception -> {
            Logging.warning("Failed to teleport %s to %s: %s",
                    teleportee.getName(), location, exception.getMessage());
            return Attempt.failure(TeleportResult.Failure.TELEPORT_FAILED_EXCEPTION);
        }).mapAttempt(success -> {
            Logging.finer("Teleported async %s to %s", teleportee.getName(), location);
            if (success) {
                if (shouldRemoveFromQueue) {
                    teleportQueue.popFromQueue(teleportee.getName());
                }
                return Attempt.success(null);
            }
            return Attempt.failure(TeleportResult.Failure.TELEPORT_FAILED);
        });
    }
}
