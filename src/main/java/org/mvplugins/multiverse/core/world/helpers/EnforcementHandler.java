package org.mvplugins.multiverse.core.world.helpers;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
public final class EnforcementHandler {

    private final CorePermissionsChecker permissionsChecker;
    private final Provider<WorldManager> worldManagerProvider;

    @Inject
    EnforcementHandler(CorePermissionsChecker permissionsChecker, Provider<WorldManager> worldManagerProvider) {
        this.permissionsChecker = permissionsChecker;
        this.worldManagerProvider = worldManagerProvider;
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
