package org.mvplugins.multiverse.core.teleportation;

import io.vavr.control.Either;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.api.teleportation.SafetyTeleporter;
import org.mvplugins.multiverse.core.api.teleportation.SafetyTeleporterAction;
import org.mvplugins.multiverse.core.api.destination.DestinationInstance;

@Service
public class AsyncSafetyTeleporter implements SafetyTeleporter {
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
     * {@inheritDoc}
     */
    @Override
    public SafetyTeleporterAction to(@Nullable Location location) {
        return new AsyncSafetyTeleporterAction(
                blockSafety,
                teleportQueue,
                pluginManager,
                Either.left(location)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SafetyTeleporterAction to(@Nullable DestinationInstance<?, ?> destination) {
        return new AsyncSafetyTeleporterAction(
                blockSafety,
                teleportQueue,
                pluginManager,
                Either.right(destination)
        );
    }
}
