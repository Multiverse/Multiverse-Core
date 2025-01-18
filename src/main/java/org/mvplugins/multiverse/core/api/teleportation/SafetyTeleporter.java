package org.mvplugins.multiverse.core.api.teleportation;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.api.destination.DestinationInstance;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporterAction;

/**
 * Teleports entities safely and asynchronously. Provider for the {@link AsyncSafetyTeleporterAction}.
 *
 * @since 5.0
 */
@Contract
public interface SafetyTeleporter {
    /**
     * Sets the location to teleport to.
     *
     * @param location The location
     * @return A new {@link AsyncSafetyTeleporterAction} to be chained
     * @since 5.0
     */
    SafetyTeleporterAction to(@Nullable Location location);

    /**
     * Sets the destination to teleport to.
     *
     * @param destination The destination
     * @return A new {@link AsyncSafetyTeleporterAction} to be chained
     * @since 5.0
     */
    SafetyTeleporterAction to(@Nullable DestinationInstance<?, ?> destination);
}
