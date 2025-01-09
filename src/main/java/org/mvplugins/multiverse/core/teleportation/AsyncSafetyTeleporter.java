package org.mvplugins.multiverse.core.teleportation;

import io.vavr.control.Either;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.BlockSafety;
import org.mvplugins.multiverse.core.destination.DestinationInstance;

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

    public AsyncSafetyTeleporterAction to(@Nullable Location location) {
        return new AsyncSafetyTeleporterAction(
                blockSafety,
                teleportQueue,
                pluginManager,
                Either.left(location)
        );
    }

    public AsyncSafetyTeleporterAction to(@Nullable DestinationInstance<?, ?> destination) {
        return new AsyncSafetyTeleporterAction(
                blockSafety,
                teleportQueue,
                pluginManager,
                Either.right(destination)
        );
    }
}
