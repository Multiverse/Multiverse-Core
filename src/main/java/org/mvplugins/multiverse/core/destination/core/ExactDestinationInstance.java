package org.mvplugins.multiverse.core.destination.core;

import io.vavr.control.Option;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.destination.DestinationInstance;

/**
 * Destination instance implementation for the {@link ExactDestination}.
 */
public class ExactDestinationInstance extends DestinationInstance<ExactDestinationInstance, ExactDestination> {
    private final Location location;

    /**
     * Constructor.
     *
     * @param location The location to teleport to.
     */
    ExactDestinationInstance(@NotNull ExactDestination destination, @NotNull Location location) {
        super(destination);
        this.location = location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Location> getLocation(@NotNull Entity teleportee) {
        if (location.getWorld() == null) {
            return Option.none();
        }
        return Option.of(location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Vector> getVelocity(@NotNull Entity teleportee) {
        return Option.none();
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
                + "," + location.getZ() + ":" + location.getPitch() + ":" + location.getYaw();
    }
}
