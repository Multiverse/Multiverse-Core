package org.mvplugins.multiverse.core.teleportation;

import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import io.papermc.lib.PaperLib;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.BlockSafety;
import org.mvplugins.multiverse.core.destination.ParsedDestination;
import org.mvplugins.multiverse.core.utils.result.AsyncAttempt;
import org.mvplugins.multiverse.core.utils.result.AsyncResult;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.Result;

@SuppressWarnings("unchecked")
@Service
public class AsyncSafetyTeleporter {
    private final BlockSafety blockSafety;
    private final TeleportQueue teleportQueue;

    @Inject
    AsyncSafetyTeleporter(
            BlockSafety blockSafety,
            TeleportQueue teleportQueue) {
        this.blockSafety = blockSafety;
        this.teleportQueue = teleportQueue;
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleportSafely(
            @NotNull Entity teleportee,
            @Nullable ParsedDestination<?> destination) {
        return teleportSafely(null, teleportee, destination);
    }

    public <T extends Entity> AsyncResult<List<Result<TeleportResult.Success, TeleportResult.Failure>>> teleportSafely(
            @Nullable CommandSender teleporter,
            @NotNull List<T> teleportees,
            @Nullable ParsedDestination<?> destination) {
        return AsyncResult.allOf(teleportees.stream()
                .map(teleportee -> teleportSafely(teleporter, teleportee, destination))
                .toList());
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleportSafely(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable ParsedDestination<?> destination) {
        if (destination == null) {
            return AsyncAttempt.failure(TeleportResult.Failure.NULL_DESTINATION);
        }
        return destination.getDestination().checkTeleportSafety()
                ? teleportSafely(teleporter, teleportee, destination.getLocation(teleportee))
                : teleport(teleporter, teleportee, destination.getLocation(teleportee));
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

    public <T extends Entity> AsyncResult<List<Result<TeleportResult.Success, TeleportResult.Failure>>> teleport(
            @NotNull List<T> teleportees,
            @Nullable ParsedDestination<?> destination) {
        return AsyncResult.allOf(teleportees.stream()
                .map(teleportee -> teleport(teleportee, destination))
                .toList());
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleport(
            @NotNull Entity teleportee,
            @Nullable ParsedDestination<?> destination) {
        return teleport(null, teleportee, destination);
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleport(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable ParsedDestination<?> destination) {
        if (destination == null) {
            return AsyncAttempt.failure(TeleportResult.Failure.NULL_DESTINATION);
        }
        return teleport(teleporter, teleportee, destination.getLocation(teleportee));
    }

    public <T extends Entity> AsyncResult<List<Result<TeleportResult.Success, TeleportResult.Failure>>> teleport(
            @NotNull List<T> teleportees,
            @Nullable Location location) {
        return AsyncResult.allOf(teleportees.stream()
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
            boolean shouldAddToQueue) {
        return AsyncAttempt.of(PaperLib.teleportAsync(teleportee, location), exception -> {
            Logging.warning("Failed to teleport %s to %s: %s",
                    teleportee.getName(), location, exception.getMessage());
            return Attempt.failure(TeleportResult.Failure.TELEPORT_FAILED_EXCEPTION);
        }).mapAsyncAttempt(result -> {
            Logging.finer("Teleported async %s to %s", teleportee.getName(), location);
            if (result) {
                if (shouldAddToQueue) {
                    teleportQueue.popFromQueue(teleportee.getName());
                }
                return AsyncAttempt.success();
            } else {
                return AsyncAttempt.failure(TeleportResult.Failure.TELEPORT_FAILED);
            }
        });
    }
}
