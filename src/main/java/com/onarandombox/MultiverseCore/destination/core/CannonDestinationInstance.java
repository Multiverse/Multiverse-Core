package com.onarandombox.MultiverseCore.destination.core;

import com.onarandombox.MultiverseCore.api.DestinationInstance;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Destination instance implementation for the {@link CannonDestination}.
 */
public class CannonDestinationInstance implements DestinationInstance {
    private final Location location;
    private final double speed;

    /**
     * Constructor.
     *
     * @param location The location to teleport to.
     * @param speed The speed to fire the player at.
     */
    CannonDestinationInstance(@NotNull Location location, double speed) {
        this.location = location;
        this.speed = speed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Vector getVelocity(@NotNull Entity teleportee) {
        double pitchRadians = Math.toRadians(location.getPitch());
        double yawRadians = Math.toRadians(location.getYaw());
        double x = Math.sin(yawRadians) * speed * -1;
        double y = Math.sin(pitchRadians) * speed * -1;
        double z = Math.cos(yawRadians) * speed;
        // Account for the angle they were pointed, and take away velocity
        x = Math.cos(pitchRadians) * x;
        z = Math.cos(pitchRadians) * z;
        return new Vector(x, y, z);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String getFinerPermissionSuffix() {
        return location.getWorld().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        return location.getWorld().getName() + ":" + location.getX() + "," + location.getY()
                + "," + location.getZ() + ":" + location.getPitch() + ":" + location.getYaw() + ":" + this.speed;
    }
}
