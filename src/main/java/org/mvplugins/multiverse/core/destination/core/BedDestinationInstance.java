package org.mvplugins.multiverse.core.destination.core;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.api.DestinationInstance;

/**
 * Destination instance implementation for the {@link BedDestination}.
 */
public class BedDestinationInstance implements DestinationInstance {
    private final Player player;

    /**
     * Constructor.
     *
     * @param player The player whose bed to use.
     */
    BedDestinationInstance(Player player) {
        this.player = player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        if (player != null) {
            return player.getBedSpawnLocation();
        }
        if (teleportee instanceof Player) {
            return ((Player) teleportee).getBedSpawnLocation();
        }
        return null;
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
        return player != null ? player.getName() : BedDestination.OWN_BED_STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        return player != null ? player.getName() : BedDestination.OWN_BED_STRING;
    }
}
