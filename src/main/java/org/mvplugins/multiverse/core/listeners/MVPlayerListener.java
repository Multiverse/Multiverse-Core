/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.event.MVRespawnEvent;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.teleportation.TeleportQueue;
import org.mvplugins.multiverse.core.utils.result.ResultChain;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.EntryFeeResult;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;
import org.mvplugins.multiverse.core.world.helpers.EnforcementHandler;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Multiverse's Listener for players.
 */
@Service
final class MVPlayerListener implements CoreListener {
    private final Plugin plugin;
    private final MVCoreConfig config;
    private final Provider<WorldManager> worldManagerProvider;
    private final BlockSafety blockSafety;
    private final Server server;
    private final TeleportQueue teleportQueue;
    private final MVEconomist economist;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;
    private final Provider<MVCommandManager> commandManagerProvider;
    private final DestinationsProvider destinationsProvider;
    private final EnforcementHandler enforcementHandler;

    private final Map<String, String> playerWorld = new ConcurrentHashMap<String, String>();

    @Inject
    MVPlayerListener(
            MultiverseCore plugin,
            MVCoreConfig config,
            Provider<WorldManager> worldManagerProvider,
            BlockSafety blockSafety,
            Server server,
            TeleportQueue teleportQueue,
            MVEconomist economist,
            WorldEntryCheckerProvider worldEntryCheckerProvider,
            Provider<MVCommandManager> commandManagerProvider,
            DestinationsProvider destinationsProvider,
            EnforcementHandler enforcementHandler) {
        this.plugin = plugin;
        this.config = config;
        this.worldManagerProvider = worldManagerProvider;
        this.blockSafety = blockSafety;
        this.server = server;
        this.teleportQueue = teleportQueue;
        this.economist = economist;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
        this.commandManagerProvider = commandManagerProvider;
        this.destinationsProvider = destinationsProvider;
        this.enforcementHandler = enforcementHandler;
    }

    private WorldManager getWorldManager() {
        return worldManagerProvider.get();
    }

    private MVCommandManager getCommandManager() {
        return commandManagerProvider.get();
    }

    /**
     * Gets the map of player and the world name they are in.
     *
     * @return the playerWorld-map
     */
    public Map<String, String> getPlayerWorld() {
        return playerWorld;
    }

    /**
     * This method is called when a player respawns.
     *
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        getWorldManager().getLoadedWorld(player.getWorld())
                .onEmpty(() -> Logging.fine("Player '%s' is in a world that is not managed by Multiverse.", player.getName()))
                .filter(mvWorld -> {
                    if (mvWorld.getBedRespawn() && event.isBedSpawn()) {
                        Logging.fine("Spawning %s at their bed.", player.getName());
                        return false;
                    }
                    if (mvWorld.getAnchorRespawn() && event.isAnchorSpawn()) {
                        Logging.fine("Spawning %s at their anchor.", player.getName());
                        return false;
                    }
                    if (!config.getDefaultRespawnWithinSameWorld() && mvWorld.getRespawnWorldName().isEmpty()) {
                        Logging.fine("Not overriding respawn location for player '%s' as " +
                                "default-respawn-within-same-world is disabled and no respawn-world is set.", player.getName());
                        return false;
                    }
                    return true;
                })
                .flatMap(mvWorld -> getMostAccurateRespawnLocation(player, mvWorld, event.getRespawnLocation()))
                .peek(newRespawnLocation -> {
                    MVRespawnEvent respawnEvent = new MVRespawnEvent(newRespawnLocation, event.getPlayer());
                    this.server.getPluginManager().callEvent(respawnEvent);
                    if (respawnEvent.isCancelled()) {
                        Logging.fine("Player '%s' cancelled their respawn event.", player.getName());
                        return;
                    }
                    Logging.fine("Overriding respawn location for player '%s' to '%s'.", player.getName(), respawnEvent.getRespawnLocation());
                    event.setRespawnLocation(respawnEvent.getRespawnLocation());
                });
    }

    private Option<Location> getMostAccurateRespawnLocation(Player player, MultiverseWorld mvWorld, Location defaultRespawnLocation) {
        return Option.of(mvWorld.getRespawnWorldName().isEmpty()
                        ? player.getWorld()
                        : server.getWorld(mvWorld.getRespawnWorldName()))
                .onEmpty(() -> Logging.warning("World '%s' has respawn-world property of '%s' that does not exist!",
                        player.getWorld().getName(), mvWorld.getRespawnWorldName()))
                .flatMap(newRespawnWorld -> {
                    if (!config.getEnforceRespawnAtWorldSpawn() && newRespawnWorld.equals(defaultRespawnLocation.getWorld())) {
                        Logging.fine("Respawn location is within same world as respawn-world, not overriding.");
                        return Option.none();
                    }
                    return getWorldManager()
                            .getLoadedWorld(newRespawnWorld)
                            .map(newMVRespawnWorld -> (Location) newMVRespawnWorld.getSpawnLocation())
                            .orElse(() -> Option.of(newRespawnWorld.getSpawnLocation()));
                });
    }

    @EventHandler
    void playerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        MultiverseWorld world = getWorldManager().getLoadedWorld(player.getWorld()).getOrNull();
        if (world == null) {
            Logging.finer("Player joined in a world that is not managed by Multiverse.");
            return;
        }
        if (!player.hasPlayedBefore()) {
            handleFirstSpawn(event);
        } else {
            handleJoinLocation(event);
        }
        this.handleGameModeAndFlight(player, event.getSpawnLocation().getWorld());
    }

    private void handleFirstSpawn(PlayerSpawnLocationEvent event) {
        if (!config.getFirstSpawnOverride()) {
            Logging.finer("FirstSpawnOverride is disabled");
            // User has disabled the feature in config
            return;
        }
        Logging.fine("Moving NEW player to(firstspawnoverride): %s", config.getFirstSpawnLocation());
        destinationsProvider.parseDestination(config.getFirstSpawnLocation())
                .flatMap(destination -> destination.getLocation(event.getPlayer()))
                .peek(event::setSpawnLocation)
                .onEmpty(() -> Logging.warning("The destination in FirstSpawnLocation in config is invalid"));
    }

    private void handleJoinLocation(PlayerSpawnLocationEvent event) {
        if (!config.getEnableJoinDestination()) {
            Logging.finer("JoinDestination is disabled");
            // User has disabled the feature in config
            return;
        }
        if (config.getJoinDestination().isBlank()) {
            Logging.warning("Joindestination is enabled but no destination has been specified in config!");
            return;
        }
        Logging.finer("JoinDestination is " + config.getJoinDestination());
        destinationsProvider.parseDestination(config.getJoinDestination())
                .flatMap(destination -> destination.getLocation(event.getPlayer()))
                .peek(event::setSpawnLocation)
                .onEmpty(() -> Logging.warning("The destination in JoinDestination in config is invalid"));
    }

    /**
     * This method is called when a player changes worlds.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerChangedWorld(PlayerChangedWorldEvent event) {
        // Permissions now determine whether or not to handle a gamemode.
        this.handleGameModeAndFlight(event.getPlayer(), event.getPlayer().getWorld());
        playerWorld.put(event.getPlayer().getName(), event.getPlayer().getWorld().getName());
    }

    /**
     * This method is called when a player teleports anywhere.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent event) {
        Logging.finer("Got teleport event for player '"
                + event.getPlayer().getName() + "' with cause '" + event.getCause() + "'");
        if (event.isCancelled()) {
            return;
        }
        Player teleportee = event.getPlayer();
        Option<String> teleporterName = teleportQueue.popFromQueue(teleportee.getName());
        CommandSender teleporter = teleporterName.map(name -> {
            if (name.equalsIgnoreCase("CONSOLE")) {
                Logging.finer("We know the teleporter is the console! Magical!");
                return this.server.getConsoleSender();
            }
            return this.server.getPlayerExact(teleporterName.get());
        }).getOrNull();

        if (teleporter == null) {
            if (!config.getTeleportIntercept()) {
                Logging.finer("Teleport for %s was not initiated by multiverse and " +
                        "teleport intercept is disabled. Ignoring...", teleportee.getName());
                return;
            }
            Logging.finer("Unknown teleporter for teleport for %s. Using player as teleporter.", teleportee.getName());
            teleporter = teleportee;
        }

        Logging.finer("Teleporter %s is teleporting %s from %s to %s", teleporter.getName(), teleportee.getName(),
                event.getFrom(), event.getTo());

        MultiverseWorld fromWorld = getWorldManager().getLoadedWorld(event.getFrom().getWorld().getName()).getOrNull();
        LoadedMultiverseWorld toWorld = getWorldManager().getLoadedWorld(event.getTo().getWorld().getName()).getOrNull();
        if (toWorld == null) {
            Logging.fine("Player '" + teleportee.getName() + "' is teleporting to world '"
                    + event.getTo().getWorld().getName() + "' which is not managed by Multiverse-Core.  No further "
                    + "actions will be taken by Multiverse-Core.");
            return;
        }
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            // The player is Teleporting to the same world.
            Logging.finer("Player '" + teleportee.getName() + "' is teleporting to the same world.");
            this.stateSuccess(teleportee.getName(), toWorld.getName());
            return;
        }

        CommandSender finalTeleporter = teleporter;
        ResultChain entryResult = worldEntryCheckerProvider.forSender(finalTeleporter).canEnterWorld(fromWorld, toWorld)
                .onSuccessReason(EntryFeeResult.Success.class, reason -> {
                    if (reason == EntryFeeResult.Success.ENOUGH_MONEY) {
                        economist.payEntryFee((Player) finalTeleporter, toWorld);
                        // Send payment receipt
                    }
                })
                .onFailure(results -> {
                    event.setCancelled(true);
                    getCommandManager().getCommandIssuer(finalTeleporter).sendError(results.getLastResultMessage());
                });

        Logging.fine("Teleport result: %s", entryResult);
    }

    private void stateSuccess(String playerName, String worldName) {
        Logging.fine("MV-Core is allowing Player '" + playerName
                + "' to go to '" + worldName + "'.");
    }

    /**
     * This method is called to adjust the portal location to the actual portal location (and not
     * right outside of it.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerPortalCheck(PlayerPortalEvent event) {
        if (event.isCancelled() || event.getFrom() == null) {
            return;
        }

        // REMEMBER! getTo MAY be NULL HERE!!!
        // If the player was actually outside of the portal, adjust the from location
        if (event.getFrom().getWorld().getBlockAt(event.getFrom()).getType() != Material.NETHER_PORTAL) {
            Location newloc = blockSafety.findPortalBlockNextTo(event.getFrom());
            if (newloc != null) {
                event.setFrom(newloc);
            }
        }
        // Wait for the adjust, then return!
        if (event.getTo() == null) {
            return;
        }
    }

    /**
     * This method is called when a player actually portals via a vanilla style portal.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void playerPortal(PlayerPortalEvent event) {
        if (event.isCancelled() || (event.getFrom() == null)) {
            return;
        }
        // The adjust should have happened much earlier.
        if (event.getTo() == null) {
            return;
        }
        if (config.isUsingCustomPortalSearch()) {
            event.setSearchRadius(config.getCustomPortalSearchRadius());
        }

        MultiverseWorld fromWorld = getWorldManager().getLoadedWorld(event.getFrom().getWorld().getName()).getOrNull();
        LoadedMultiverseWorld toWorld = getWorldManager().getLoadedWorld(event.getTo().getWorld().getName()).getOrNull();
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            // The player is Portaling to the same world.
            Logging.finer("Player '" + event.getPlayer().getName() + "' is portaling to the same world.");
            return;
        }

        ResultChain entryResult = worldEntryCheckerProvider.forSender(event.getPlayer()).canEnterWorld(fromWorld, toWorld)
                .onFailure(results -> {
                    event.setCancelled(true);
                    getCommandManager().getCommandIssuer(event.getPlayer()).sendError(results.getLastResultMessage());
                });

        Logging.fine("Teleport result: %s", entryResult);
    }

    /**
     * Handles the gamemode for the specified {@link Player}.
     *
     * @param player The {@link Player}.
     * @param world  The {@link World} the player is supposed to be in.
     */
    private void handleGameModeAndFlight(final Player player, World world) {
        // We perform this task one tick later to MAKE SURE that the player actually reaches the
        // destination world, otherwise we'd be changing the player mode if they havent moved anywhere.
        this.server.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            if (!player.isOnline() || !player.getWorld().equals(world)) {
                return;
            }
            Logging.finer("Handling gamemode and flight for player %s in world '%s'", player.getName(), world.getName());
            enforcementHandler.handleFlightEnforcement(player);
            enforcementHandler.handleGameModeEnforcement(player);
        }, 1L);
    }
}
