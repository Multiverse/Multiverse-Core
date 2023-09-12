package org.mvplugins.multiverse.core.teleportation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import org.mvplugins.multiverse.core.utils.result.Result;

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

    public CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> teleportSafely(
            @NotNull Entity teleportee,
            @Nullable ParsedDestination<?> destination) {
        return teleportSafely(null, teleportee, destination);
    }

    public CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> teleportSafely(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable ParsedDestination<?> destination) {
        if (destination == null) {
            return CompletableFuture.completedFuture(Result.failure(TeleportResult.Failure.NULL_DESTINATION));
        }
        return destination.getDestination().checkTeleportSafety()
                ? teleportSafely(teleporter, teleportee, destination.getLocation(teleportee))
                : teleport(teleporter, teleportee, destination.getLocation(teleportee));
    }

    public CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> teleportSafely(
            @NotNull Entity teleportee,
            @Nullable Location location) {
        return teleportSafely(null, teleportee, location);
    }

    public CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> teleportSafely(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable Location location) {
        if (location == null) {
            return CompletableFuture.completedFuture(Result.failure(TeleportResult.Failure.NULL_LOCATION));
        }
        Location safeLocation = blockSafety.getSafeLocation(location);
        if (safeLocation == null) {
            return CompletableFuture.completedFuture(Result.failure(TeleportResult.Failure.UNSAFE_LOCATION));
        }
        return teleport(teleporter, teleportee, safeLocation);
    }

    public <T extends Entity> List<CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>>> teleport(
            @NotNull List<T> teleportees,
            @Nullable ParsedDestination<?> destination) {
        return teleportees.stream()
                .map(teleportee -> teleport(teleportee, destination))
                .toList();
    }

    public CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> teleport(
            @NotNull Entity teleportee,
            @Nullable ParsedDestination<?> destination) {
        return teleport(null, teleportee, destination);
    }

    public CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> teleport(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable ParsedDestination<?> destination) {
        if (destination == null) {
            return CompletableFuture.completedFuture(Result.failure(TeleportResult.Failure.NULL_DESTINATION));
        }
        return teleport(teleporter, teleportee, destination.getLocation(teleportee));
    }

    public <T extends Entity> List<CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>>> teleport(
            @NotNull List<T> teleportees,
            @Nullable Location location) {
        return teleportees.stream()
                .map(teleportee -> teleport(teleportee, location))
                .toList();
    }

    public CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> teleport(
            @NotNull Entity teleportee,
            @Nullable Location location) {
        return teleport(null, teleportee, location);
    }

    public CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> teleport(
            @Nullable CommandSender teleporter,
            @NotNull Entity teleportee,
            @Nullable Location location) {
        if (location == null) {
            return CompletableFuture.completedFuture(Result.failure(TeleportResult.Failure.NULL_LOCATION));
        }

        boolean shouldAddToQueue = teleporter != null && teleportee instanceof Player;
        if (shouldAddToQueue) {
            teleportQueue.addToQueue(teleporter.getName(), teleportee.getName());
        }

        CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> future = new CompletableFuture<>();
        doAsyncTeleport(teleportee, location, future, shouldAddToQueue);
        return future;
    }

    private void doAsyncTeleport(
            @NotNull Entity teleportee,
            @NotNull Location location,
            CompletableFuture<Result<TeleportResult.Success, TeleportResult.Failure>> future,
            boolean shouldAddToQueue) {
        Try.run(() -> PaperLib.teleportAsync(teleportee, location).thenAccept(result -> {
            Logging.finer("Teleported async %s to %s", teleportee.getName(), location);
            future.complete(result
                    ? Result.success(TeleportResult.Success.SUCCESS)
                    : Result.failure(TeleportResult.Failure.TELEPORT_FAILED));
        }).exceptionally(exception -> {
            Logging.warning("Failed to teleport %s to %s: %s",
                    teleportee.getName(), location, exception.getMessage());
            future.completeExceptionally(exception);
            return null;
        })).onFailure(exception -> {
            Logging.warning("Failed to teleport %s to %s: %s",
                    teleportee.getName(), location, exception.getMessage());
            future.complete(Result.failure(TeleportResult.Failure.TELEPORT_FAILED_EXCEPTION));
        }).andFinally(() -> {
            if (shouldAddToQueue) {
                teleportQueue.popFromQueue(teleportee.getName());
            }
        });
    }
}
