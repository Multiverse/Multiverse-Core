package org.mvplugins.multiverse.core.destination.core;

import io.vavr.control.Option;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.destination.DestinationInstance;

/**
 * Destination instance implementation for the {@link CannonDestination}.
 */
public class CannonDestinationInstance extends DestinationInstance<CannonDestinationInstance, CannonDestination> {
    private final Location location;
    private final double speed;

    /**
     * Constructor.
     *
     * @param location The location to teleport to.
     * @param speed The speed to fire the player at.
     */
    CannonDestinationInstance(@NotNull CannonDestination destination, @NotNull Location location, double speed) {
        super(destination);
        this.location = location;
        this.speed = speed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Location> getLocation(@NotNull Entity teleportee) {
        return Option.of(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Vector> getVelocity(@NotNull Entity teleportee) {
        double pitchRadians = Math.toRadians(location.getPitch());
        double yawRadians = Math.toRadians(location.getYaw());
        double x = Math.sin(yawRadians) * speed * -1;
        double y = Math.sin(pitchRadians) * speed * -1;
        double z = Math.cos(yawRadians) * speed;
        // Account for the angle they were pointed, and take away velocity
        x = Math.cos(pitchRadians) * x;
        z = Math.cos(pitchRadians) * z;
        return Option.of(new Vector(x, y, z));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkTeleportSafety() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<String> getFinerPermissionSuffix() {
        return Option.of(location.getWorld()).map(World::getName);
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
