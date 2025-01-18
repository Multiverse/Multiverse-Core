package org.mvplugins.multiverse.core.api.teleportation;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.api.destination.DestinationInstance;

/**
 * Teleports entities safely and asynchronously. Provider for the {@link SafetyTeleporterAction}.
 *
 * @since 5.0
 */
@Contract
public interface SafetyTeleporter {
    /**
     * Sets the location to teleport to.
     *
     * @param location The location
     * @return A new {@link SafetyTeleporterAction} to be chained
     * @since 5.0
     */
    SafetyTeleporterAction to(@Nullable Location location);

    /**
     * Sets the destination to teleport to.
     *
     * @param destination The destination
     * @return A new {@link SafetyTeleporterAction} to be chained
     * @since 5.0
     */
    SafetyTeleporterAction to(@Nullable DestinationInstance<?, ?> destination);
}
