package org.mvplugins.multiverse.core.worldnew.helpers;

import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.SafeTTeleporter;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.WorldManager;

/**
 * Handles all player actions that need to be done when a change in world related activity occurs.
 */
@Service
public class PlayerWorldActions {

    private final CorePermissionsChecker permissionsChecker;
    private final SafeTTeleporter safetyTeleporter;
    private final Provider<WorldManager> worldManagerProvider;

    @Inject
    PlayerWorldActions(
            @NotNull CorePermissionsChecker permissionsChecker,
            @NotNull SafeTTeleporter safetyTeleporter,
            @NotNull Provider<WorldManager> worldManagerProvider) {
        this.permissionsChecker = permissionsChecker;
        this.safetyTeleporter = safetyTeleporter;
        this.worldManagerProvider = worldManagerProvider;
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

    /**
     * Teleports all players to the given location.
     *
     * @param world The world to teleport players to.
     */
    public void handleAllGameModeEnforcement(@NotNull LoadedMultiverseWorld world) {
        world.getPlayers().peek(players -> players.forEach(this::handleGameModeEnforcement));
    }

    /**
     * Handles game mode enforcement for the given player in the world they are currently in.
     *
     * @param player    The player to enforce game mode for.
     */
    public void handleGameModeEnforcement(@NotNull Player player) {
        worldManagerProvider.get().getLoadedWorld(player.getWorld()).peek(world -> {
            if (permissionsChecker.hasGameModeBypassPermission(player, world)) {
                Logging.finer("Player is immune to gamemode enforcement: %s", player.getName());
                return;
            }
            Logging.finer("Handling gamemode for player in world '%s': %s, Changing to %s",
                    world.getName(), player.getName(), world.getGameMode());
            player.setGameMode(world.getGameMode());
        }).onEmpty(() -> {
            Logging.fine("Player %s is not in a Multiverse world, gamemode enforcement will not apply",
                    player.getName());
        });
    }

    /**
     * Handles flight enforcement for all players in the given world.
     *
     * @param world The world to enforce flight in.
     */
    public void handleAllFlightEnforcement(@NotNull LoadedMultiverseWorld world) {
        world.getPlayers().peek(players -> players.forEach(this::handleFlightEnforcement));
    }

    /**
     * Handles flight enforcement for the given player in the world they are currently in.
     *
     * @param player    The player to enforce flight for.
     */
    public void handleFlightEnforcement(@NotNull Player player) {
        worldManagerProvider.get().getLoadedWorld(player.getWorld()).peek(world -> {
            if (player.getAllowFlight() && !world.getAllowFlight() && player.getGameMode() != GameMode.CREATIVE) {
                player.setAllowFlight(false);
                if (player.isFlying()) {
                    player.setFlying(false);
                }
            } else if (world.getAllowFlight()) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.setAllowFlight(true);
                }
            }
        }).onEmpty(() -> {
            Logging.fine("Player %s is not in a Multiverse world, flight enforcement will not apply",
                    player.getName());
        });
    }
}
