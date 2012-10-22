package com.onarandombox.MultiverseCore.api;

import org.bukkit.Location;

/**
 * A special {@link MVDestination} that can determine the target {@link Location} without a reference to the teleportee.
 */
public interface MVStaticDestination extends MVDestination {
    /**
     * Returns {@link MVDestination}'s target {@link Location}.
     * <p>
     * Although that shouldn't be necessary, it is recommended to implement
     * {@link #getLocation(org.bukkit.entity.Entity)} with a simple call to this method.
     * @return The target location.
     * @see #getLocation(org.bukkit.entity.Entity)
     */
    Location getLocation();
}
