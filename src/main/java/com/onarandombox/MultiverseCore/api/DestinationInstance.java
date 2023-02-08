package com.onarandombox.MultiverseCore.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DestinationInstance {
    /**
     * Gets the exact location to teleport an entity to.
     *
     * @param teleportee The entity to teleport.
     * @return The location to teleport to.
     */
    @Nullable Location getLocation(@NotNull Entity teleportee);

    /**
     * Gets the velocity to apply to an entity after teleporting.
     *
     * @param teleportee The entity to teleport.
     * @return A vector representing the speed/direction the player should travel when arriving at the destination.
     */
    @Nullable Vector getVelocity(@NotNull Entity teleportee);

    /**
     * Gets the permission suffix to check for when teleporting to this destination.
     * This is used for finer per world/player permissions, such as "multiverse.teleport.self.worldname".
     *
     * <p>For example, if the destination is "w:world", the permission suffix is "world".</p>
     *
     * @return The permission suffix.
     */
    @Nullable String getFinerPermissionSuffix();

    /**
     * Serialises the destination instance to a savable string.
     *
     * <p>This is used when plugins save destinations to configuration,
     * and when the destination is displayed to the user.</p>
     *
     * @return The serialised destination instance.
     */
    @NotNull String serialise();
}
