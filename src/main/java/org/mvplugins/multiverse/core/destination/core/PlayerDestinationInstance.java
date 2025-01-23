package org.mvplugins.multiverse.core.destination.core;

import io.vavr.control.Option;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.destination.DestinationInstance;

/**
 * Destination instance implementation for the {@link PlayerDestination}.
 */
public class PlayerDestinationInstance extends DestinationInstance<PlayerDestinationInstance, PlayerDestination> {
    private final Player player;

    /**
     * Constructor.
     *
     * @param player The player whose location to go to.
     */
    PlayerDestinationInstance(@NotNull PlayerDestination destination, @NotNull Player player) {
        super(destination);
        this.player = player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Location> getLocation(@NotNull Entity teleportee) {
        return Option.of(player.getLocation());
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
        return Option.of(player.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        return player.getName();
    }
}
