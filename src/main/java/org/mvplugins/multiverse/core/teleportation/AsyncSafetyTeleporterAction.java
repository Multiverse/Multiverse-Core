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
import org.mvplugins.multiverse.core.api.BlockSafety;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.event.MVTeleportDestinationEvent;
import org.mvplugins.multiverse.core.utils.result.Async;
import org.mvplugins.multiverse.core.utils.result.AsyncAttempt;
import org.mvplugins.multiverse.core.utils.result.Attempt;

import java.util.List;

public class AsyncSafetyTeleporterAction {

    private final BlockSafety blockSafety;
    private final TeleportQueue teleportQueue;
    private final PluginManager pluginManager;

    private final @NotNull Either<Location, DestinationInstance<?, ?>> locationOrDestination;
    private boolean checkSafety;
    private @Nullable CommandSender teleporter = null;

    public AsyncSafetyTeleporterAction(
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

    public AsyncSafetyTeleporterAction checkSafety(boolean checkSafety) {
        this.checkSafety = checkSafety;
        return this;
    }

    public AsyncSafetyTeleporterAction by(@NotNull BukkitCommandIssuer issuer) {
        return by(issuer.getIssuer());
    }

    public AsyncSafetyTeleporterAction by(@NotNull CommandSender teleporter) {
        this.teleporter = teleporter;
        return this;
    }

    public  <T extends Entity> Async<List<Attempt<Void, TeleportResult.Failure>>> teleport(@NotNull List<T> teleportees) {
        return AsyncAttempt.allOf(teleportees.stream().map(this::teleport).toList());
    }

    public AsyncAttempt<Void, TeleportResult.Failure> teleport(@NotNull Entity teleportee) {
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

    private Attempt<Location, TeleportResult.Failure> getLocation(@NotNull Entity teleportee) {
        return this.locationOrDestination.fold(
                location -> parseLocation(teleportee, location),
                destination -> parseDestination(teleportee, destination)
        );
    }

    private Attempt<Location, TeleportResult.Failure> parseLocation(
            @NotNull Entity teleportee, @Nullable Location location) {
        if (location == null) {
            return Attempt.failure(TeleportResult.Failure.NULL_LOCATION);
        }
        return Attempt.success(location);
    }

    private Attempt<Location, TeleportResult.Failure> parseDestination(
            @NotNull Entity teleportee, @Nullable DestinationInstance<?, ?> destination) {
        if (destination == null) {
            return Attempt.failure(TeleportResult.Failure.NULL_LOCATION);
        }
        MVTeleportDestinationEvent event = new MVTeleportDestinationEvent(destination, teleportee, teleporter);
        this.pluginManager.callEvent(event);
        if (event.isCancelled()) {
            return Attempt.failure(TeleportResult.Failure.EVENT_CANCELLED);
        }
        return destination.getLocation(teleportee)
                .map(Attempt::<Location, TeleportResult.Failure>success)
                .getOrElse(Attempt.failure(TeleportResult.Failure.NULL_LOCATION));
    }

    private Attempt<Location, TeleportResult.Failure> doSafetyCheck(@NotNull Location location) {
        if (!this.checkSafety) {
            return Attempt.success(location);
        }
        Location safeLocation = blockSafety.getSafeLocation(location);
        if (safeLocation == null) {
            return Attempt.failure(TeleportResult.Failure.UNSAFE_LOCATION);
        }
        return Attempt.success(safeLocation);
    }

    private AsyncAttempt<Void, TeleportResult.Failure> doAsyncTeleport(
            @NotNull Entity teleportee,
            @NotNull Location location) {
        return AsyncAttempt.of(PaperLib.teleportAsync(teleportee, location), exception -> {
            Logging.warning("Failed to teleport %s to %s: %s",
                    teleportee.getName(), location, exception.getMessage());
            return Attempt.failure(TeleportResult.Failure.TELEPORT_FAILED_EXCEPTION);
        }).mapAttempt(success -> {
            if (success) {
                Logging.finer("Teleported async %s to %s", teleportee.getName(), location);
                return Attempt.success(null);
            }
            Logging.warning("Failed to async teleport %s to %s", teleportee.getName(), location);
            return Attempt.failure(TeleportResult.Failure.TELEPORT_FAILED);
        });
    }
}
