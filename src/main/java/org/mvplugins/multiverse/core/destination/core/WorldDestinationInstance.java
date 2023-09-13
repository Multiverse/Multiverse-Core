package org.mvplugins.multiverse.core.destination.core;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.api.DestinationInstance;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Destination instance implementation for the {@link WorldDestination}.
 */
public class WorldDestinationInstance implements DestinationInstance {
    private final LoadedMultiverseWorld world;
    private final String direction;
    private final float yaw;

    /**
     * Constructor.
     *
     * @param world     The world to teleport to.
     * @param direction The direction to face.
     * @param yaw       The yaw to face.
     */
    WorldDestinationInstance(@NotNull LoadedMultiverseWorld world, @Nullable String direction, float yaw) {
        this.world = world;
        this.direction = direction;
        this.yaw = yaw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        Location worldLoc = world.getSpawnLocation();
        if (this.yaw >= 0) {
            // Only modify the yaw if its set.
            worldLoc.setYaw(this.yaw);
        }
        return worldLoc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Vector getVelocity(@NotNull Entity teleportee) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String getFinerPermissionSuffix() {
        return world.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        if (this.direction != null) {
            return this.world.getName() + ":" + this.direction;
        }
        return this.world.getName();
    }
}
