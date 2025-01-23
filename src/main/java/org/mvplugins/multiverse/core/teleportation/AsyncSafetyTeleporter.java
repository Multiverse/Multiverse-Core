package org.mvplugins.multiverse.core.teleportation;

import io.vavr.control.Either;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.destination.DestinationInstance;

/**
 * Teleports entities safely and asynchronously. Provider for the {@link AsyncSafetyTeleporter}.
 */
@Service
public final class AsyncSafetyTeleporter {
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

    /**
         * Sets the location to teleport to.
         *
         * @param location The location
         * @return A new {@link AsyncSafetyTeleporterAction} to be chained
         */
    public AsyncSafetyTeleporterAction to(@Nullable Location location) {
        return new AsyncSafetyTeleporterAction(
                blockSafety,
                teleportQueue,
                pluginManager,
                Either.left(location)
        );
    }

    /**
         * Sets the destination to teleport to.
         *
         * @param destination The destination
         * @return A new {@link AsyncSafetyTeleporterAction} to be chained
         */
    public AsyncSafetyTeleporterAction to(@Nullable DestinationInstance<?, ?> destination) {
        return new AsyncSafetyTeleporterAction(
                blockSafety,
                teleportQueue,
                pluginManager,
                Either.right(destination)
        );
    }
}
