package org.mvplugins.multiverse.core.destination.core;

import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.destination.DestinationInstance;

import java.util.UUID;

/**
 * Destination instance implementation for the {@link PlayerDestination}.
 */
public final class PlayerDestinationInstance extends DestinationInstance<PlayerDestinationInstance, PlayerDestination> {
    private final UUID playerUUID;
    private final String playerName;

    /**
     * Constructor.
     *
     * @param player The player whose location to go to.
     */
    PlayerDestinationInstance(@NotNull PlayerDestination destination, @NotNull Player player) {
        super(destination);
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Location> getLocation(@NotNull Entity teleportee) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) {
            return Option.none();
        }
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
        return Option.of(playerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        return playerName;
    }
}
