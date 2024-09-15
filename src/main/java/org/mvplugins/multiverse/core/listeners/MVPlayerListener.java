/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import java.util.Map;
import java.util.Optional;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.api.BlockSafety;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.destination.ParsedDestination;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.event.MVRespawnEvent;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.TeleportQueue;
import org.mvplugins.multiverse.core.utils.result.ResultChain;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.EntryFeeResult;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;
import org.mvplugins.multiverse.core.world.helpers.EnforcementHandler;

/**
 * Multiverse's Listener for players.
 */
@Service
public class MVPlayerListener implements CoreListener {
    private final Plugin plugin;
    private final MVCoreConfig config;
    private final Provider<WorldManager> worldManagerProvider;
    private final BlockSafety blockSafety;
    private final AsyncSafetyTeleporter safetyTeleporter;
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
            AsyncSafetyTeleporter safetyTeleporter,
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
        this.safetyTeleporter = safetyTeleporter;
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
        World world = event.getPlayer().getWorld();
        LoadedMultiverseWorld mvWorld = getWorldManager().getLoadedWorld(world.getName()).getOrNull();
        // If it's not a World MV manages we stop.
        if (mvWorld == null) {
            return;
        }

        if (mvWorld.getBedRespawn() && (event.isBedSpawn() || event.isAnchorSpawn())) {
            Logging.fine("Spawning %s at their %s.", event.getPlayer().getName(), event.isBedSpawn() ? "BED" : "ANCHOR");
            return;
        }

        // Get the instance of the World the player should respawn at.
        LoadedMultiverseWorld respawnWorld = null;
        if (getWorldManager().isLoadedWorld(mvWorld.getRespawnWorld())) {
            respawnWorld = getWorldManager().getLoadedWorld(mvWorld.getRespawnWorld()).getOrNull();
        }

        // If it's null then it either means the World doesn't exist or the value is blank, so we don't handle it.
        // NOW: We'll always handle it to get more accurate spawns
        if (respawnWorld != null) {
            world = respawnWorld.getBukkitWorld().getOrNull();
        }
        // World has been set to the appropriate world
        Location respawnLocation = getMostAccurateRespawnLocation(world);

        MVRespawnEvent respawnEvent = new MVRespawnEvent(respawnLocation, event.getPlayer(), "compatability");
        this.server.getPluginManager().callEvent(respawnEvent);
        event.setRespawnLocation(respawnEvent.getPlayersRespawnLocation());
    }

    private Location getMostAccurateRespawnLocation(World w) {
        LoadedMultiverseWorld mvw = getWorldManager().getLoadedWorld(w.getName()).getOrNull();
        if (mvw != null) {
            return mvw.getSpawnLocation();
        }
        return w.getSpawnLocation();
    }

    /**
     * This method is called when a player joins the server.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LoadedMultiverseWorld world = getWorldManager().getLoadedWorld(player.getWorld()).getOrNull();
        if (world == null) {
            Logging.finer("Player joined in a world that is not managed by Multiverse.");
            return;
        }

        Option.of(destinationsProvider.parseDestination(config.getFirstSpawnLocation()))
                .peek(parsedDestination -> {
                    if (!player.hasPlayedBefore()) {
                        Logging.finer("Player joined for the FIRST time!");
                        if (config.getFirstSpawnOverride()) {
                            Logging.fine("Moving NEW player to(firstspawnoverride): %s", config.getFirstSpawnLocation());
                            this.sendPlayerToDefaultWorld(player, parsedDestination);
                        }
                    }
                    handleJoinDestination(player);
                });

        // Handle the Players GameMode setting for the new world.
        this.handleGameModeAndFlight(event.getPlayer(), event.getPlayer().getWorld());
        playerWorld.put(player.getName(), player.getWorld().getName());
    }

    /**
     * Will teleport the player to the destination specified in config
     * @param player The {@link Player} to teleport
     */
    private void handleJoinDestination(@NotNull Player player) {
        if (!config.getEnableJoinDestination()) {
            Logging.finer("JoinDestination is disabled");
            // User has disabled the feature in config
            return;
        }

        if (config.getJoinDestination() == null) {
            Logging.warning("Joindestination is enabled but no destination has been specified in config!");
            return;
        }

        Logging.finer("JoinDestination is " + config.getJoinDestination());
        ParsedDestination<?> joinDestination = destinationsProvider.parseDestination(config.getJoinDestination());

        if (joinDestination == null) {
            Logging.warning("The destination in JoinDestination in config is invalid");
            return;
        }

        // Finally, teleport the player
        safetyTeleporter.teleportSafely(player, player, joinDestination);
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
        CommandSender teleporter;
        Optional<String> teleporterName = teleportQueue.popFromQueue(teleportee.getName());
        if (teleporterName.isPresent()) {
            if (teleporterName.equals("CONSOLE")) {
                Logging.finer("We know the teleporter is the console! Magical!");
                teleporter = this.server.getConsoleSender();
            } else {
                teleporter = this.server.getPlayerExact(teleporterName.get());
            }
        } else {
            teleporter = teleportee;
        }
        Logging.finer("Inferred sender '" + teleporter + "' from name '"
                + teleporterName + "', fetched from name '" + teleportee.getName() + "'");
        LoadedMultiverseWorld fromWorld = getWorldManager().getLoadedWorld(event.getFrom().getWorld().getName()).getOrNull();
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
            this.stateSuccess(teleportee.getName(), toWorld.getAlias());
            return;
        }

        ResultChain entryResult = worldEntryCheckerProvider.forSender(teleporter).canEnterWorld(fromWorld, toWorld)
                .onSuccessReason(EntryFeeResult.Success.class, reason -> {
                    if (reason == EntryFeeResult.Success.ENOUGH_MONEY) {
                        economist.payEntryFee((Player) teleporter, toWorld);
                        // Send payment receipt
                    }
                })
                .onFailure(results -> {
                    event.setCancelled(true);
                    getCommandManager().getCommandIssuer(teleporter).sendError(results.getLastResultMessage());
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
            // TODO: Fix this. Currently, we only check for PORTAL blocks. I'll have to figure out what
            // TODO: we want to do here.
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

        LoadedMultiverseWorld fromWorld = getWorldManager().getLoadedWorld(event.getFrom().getWorld().getName()).getOrNull();
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

    private void sendPlayerToDefaultWorld(final Player player, ParsedDestination parsedDestination) {
        // Remove the player 1 tick after the login. I'm sure there's GOT to be a better way to do this...
        this.server.getScheduler().scheduleSyncDelayedTask(this.plugin,
            new Runnable() {
                @Override
                public void run() {
                    safetyTeleporter.teleportSafely(player, player, parsedDestination);
                }
            }, 1L);
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
            enforcementHandler.handleFlightEnforcement(player);
            enforcementHandler.handleGameModeEnforcement(player);
        }, 1L);
    }
}
