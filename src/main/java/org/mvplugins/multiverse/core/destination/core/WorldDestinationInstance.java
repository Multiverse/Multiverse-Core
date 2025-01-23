package org.mvplugins.multiverse.core.destination.core;

import io.vavr.control.Option;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Destination instance implementation for the {@link WorldDestination}.
 */
public class WorldDestinationInstance extends DestinationInstance<WorldDestinationInstance, WorldDestination> {
    private final MultiverseWorld world;
    private final String direction;
    private final float yaw;

    /**
     * Constructor.
     *
     * @param destination   The destination.
     * @param world         The world to teleport to.
     * @param direction     The direction to face.
     * @param yaw           The yaw to face.
     */
    WorldDestinationInstance(
            @NotNull WorldDestination destination,
            @NotNull MultiverseWorld world,
            @Nullable String direction,
            float yaw
    ) {
        super(destination);
        this.world = world;
        this.direction = direction;
        this.yaw = yaw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Location> getLocation(@NotNull Entity teleportee) {
        Location worldLoc = world.getSpawnLocation();
        if (this.yaw >= 0) {
            // Only modify the yaw if its set.
            worldLoc.setYaw(this.yaw);
        }
        return Option.of(worldLoc);
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
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<String> getFinerPermissionSuffix() {
        return Option.of(world.getName());
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
