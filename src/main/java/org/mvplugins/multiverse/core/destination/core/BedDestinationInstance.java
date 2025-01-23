package org.mvplugins.multiverse.core.destination.core;

import io.vavr.control.Option;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.destination.DestinationInstance;

/**
 * Destination instance implementation for the {@link BedDestination}.
 */
public class BedDestinationInstance extends DestinationInstance<BedDestinationInstance, BedDestination> {
    private final Player player;

    /**
     * Constructor.
     *
     * @param player The player whose bed to use.
     */
    BedDestinationInstance(@NotNull BedDestination destination, @Nullable Player player) {
        super(destination);
        this.player = player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Location> getLocation(@NotNull Entity teleportee) {
        if (player != null) {
            return Option.of(player.getBedSpawnLocation());
        }
        if (teleportee instanceof Player) {
            return Option.of(((Player) teleportee).getBedSpawnLocation());
        }
        return Option.none();
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
        return Option.of(player != null ? player.getName() : BedDestination.OWN_BED_STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        return player != null ? player.getName() : BedDestination.OWN_BED_STRING;
    }
}
