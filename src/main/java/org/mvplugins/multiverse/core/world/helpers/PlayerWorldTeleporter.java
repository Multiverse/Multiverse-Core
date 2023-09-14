package org.mvplugins.multiverse.core.world.helpers;

import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.SafeTTeleporter;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Handles all player actions that need to be done when a change in world related activity occurs.
 */
@Service
public class PlayerWorldTeleporter {
    private final SafeTTeleporter safetyTeleporter;

    @Inject
    PlayerWorldTeleporter(@NotNull SafeTTeleporter safetyTeleporter) {
        this.safetyTeleporter = safetyTeleporter;
    }

    /**
     * Removes all players from the given world.
     *
     * @param world The world to remove all players from.
     */
    public void removeFromWorld(@NotNull LoadedMultiverseWorld world) {
        // TODO: Better handling of fallback world
        World toWorld = Bukkit.getWorlds().get(0);
        transferFromWorldTo(world, toWorld);
    }

    /**
     * Transfers all players from the given world to another world's spawn location.
     *
     * @param from  The world to transfer players from.
     * @param to    The location to transfer players to.
     */
    public void transferFromWorldTo(@NotNull LoadedMultiverseWorld from, @NotNull LoadedMultiverseWorld to) {
        transferAllFromWorldToLocation(from, to.getSpawnLocation());
    }

    /**
     * Transfers all players from the given world to another world's spawn location.
     *
     * @param from  The world to transfer players from.
     * @param to    The world to transfer players to.
     */
    public void transferFromWorldTo(@NotNull LoadedMultiverseWorld from, @NotNull World to) {
        transferAllFromWorldToLocation(from, to.getSpawnLocation());
    }

    /**
     * Transfers all players from the given world to the given location.
     *
     * @param world     The world to transfer players from.
     * @param location  The location to transfer players to.
     */
    public void transferAllFromWorldToLocation(@NotNull LoadedMultiverseWorld world, @NotNull Location location) {
        world.getPlayers().peek(players -> players.forEach(player -> {
            if (player.isOnline()) {
                Logging.fine("Teleporting player '%s' to world spawn: %s", player.getName(), location);
                safetyTeleporter.safelyTeleport(null, player, location, true);
            }
        }));
    }

    /**
     * Teleports all players to the given world's spawn location.
     *
     * @param players   The players to teleport.
     * @param world     The world to teleport players to.
     */
    public void teleportPlayersToWorld(@NotNull List<Player> players, @NotNull LoadedMultiverseWorld world) {
        players.forEach(player -> {
            Location spawnLocation = world.getSpawnLocation();
            if (player.isOnline()) {
                safetyTeleporter.safelyTeleport(null, player, spawnLocation, true);
            }
        });
    }
}
