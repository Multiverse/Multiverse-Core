package com.onarandombox.MultiverseCore.destination.core;

import com.onarandombox.MultiverseCore.api.DestinationInstance;
import com.onarandombox.MultiverseCore.api.MVWorld;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldDestinationInstance implements DestinationInstance {
    private final MVWorld world;
    private final String direction;
    private final float yaw;

    /**
     * Constructor.
     *
     * @param world The world to teleport to.
     */
    public WorldDestinationInstance(@NotNull MVWorld world, @Nullable String direction, float yaw) {
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
            return this.world.getCBWorld().getName() + ":" + this.direction;
        }
        return this.world.getCBWorld().getName();
    }
}
