package org.mvplugins.multiverse.core.world.helpers;

import java.util.List;

import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.TeleportFailureReason;
import org.mvplugins.multiverse.core.utils.result.Async;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

/**
 * Handles all player actions that need to be done when a change in world related activity occurs.
 */
@Service
public class PlayerWorldTeleporter {
    private final AsyncSafetyTeleporter safetyTeleporter;

    @Inject
    PlayerWorldTeleporter(@NotNull AsyncSafetyTeleporter safetyTeleporter) {
        this.safetyTeleporter = safetyTeleporter;
    }

    /**
     * Removes all players from the given world.
     *
     * @param world The world to remove all players from.
     * @return A list of async futures that represent the teleportation result of each player.
     */
    public Async<List<Attempt<Void, TeleportFailureReason>>> removeFromWorld(@NotNull LoadedMultiverseWorld world) {
        // TODO: Better handling of fallback world
        World toWorld = Bukkit.getWorlds().get(0);
        return transferFromWorldTo(world, toWorld);
    }

    /**
     * Transfers all players from the given world to another world's spawn location.
     *
     * @param from  The world to transfer players from.
     * @param to    The location to transfer players to.
     * @return A list of async futures that represent the teleportation result of each player.
     */
    public Async<List<Attempt<Void, TeleportFailureReason>>> transferFromWorldTo(
            @NotNull LoadedMultiverseWorld from,
            @NotNull LoadedMultiverseWorld to) {
        return transferAllFromWorldToLocation(from, to.getSpawnLocation());
    }

    /**
     * Transfers all players from the given world to another world's spawn location.
     *
     * @param from  The world to transfer players from.
     * @param to    The world to transfer players to.
     * @return A list of async futures that represent the teleportation result of each player.
     */
    public Async<List<Attempt<Void, TeleportFailureReason>>> transferFromWorldTo(
            @NotNull LoadedMultiverseWorld from,
            @NotNull World to) {
        return transferAllFromWorldToLocation(from, to.getSpawnLocation());
    }

    /**
     * Transfers all players from the given world to the given location.
     *
     * @param world    The world to transfer players from.
     * @param location The location to transfer players to.
     * @return A list of async futures that represent the teleportation result of each player.
     */
    public Async<List<Attempt<Void, TeleportFailureReason>>> transferAllFromWorldToLocation(
            @NotNull LoadedMultiverseWorld world,
            @NotNull Location location) {
        return world.getPlayers()
                .map(players -> safetyTeleporter.to(location).teleport(players))
                .getOrElse(() -> Async.failedFuture(
                        new IllegalStateException("Unable to get players from world" + world.getName())));
    }

    /**
     * Teleports all players to the given world's spawn location.
     *
     * @param players   The players to teleport.
     * @param world     The world to teleport players to.
     * @return A list of async futures that represent the teleportation result of each player.
     */
    public Async<List<Attempt<Void, TeleportFailureReason>>> teleportPlayersToWorld(
            @NotNull List<Player> players,
            @NotNull LoadedMultiverseWorld world) {
        Location spawnLocation = world.getSpawnLocation();
        return safetyTeleporter.to(spawnLocation).teleport(players);
    }
}
