package org.mvplugins.multiverse.core.destination.core;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.api.DestinationInstance;

/**
 * Destination instance implementation for the {@link PlayerDestination}.
 */
public class PlayerDestinationInstance implements DestinationInstance {
    private final Player player;

    /**
     * Constructor.
     *
     * @param player The player whose location to go to.
     */
    PlayerDestinationInstance(Player player) {
        this.player = player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        return player.getLocation();
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
        return player.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        return player.getName();
    }
}
