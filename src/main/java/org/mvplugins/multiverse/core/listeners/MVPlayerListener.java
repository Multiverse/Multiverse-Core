/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import java.util.Map;
import java.util.Objects;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.dynamiclistener.EventRunnable;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.DefaultEventPriority;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventClass;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventMethod;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventPriorityKey;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.event.MVRespawnEvent;
import org.mvplugins.multiverse.core.locale.PluginLocales;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.teleportation.TeleportQueue;
import org.mvplugins.multiverse.core.utils.result.ResultChain;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.EntryFeeResult;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;
import org.mvplugins.multiverse.core.world.helpers.DimensionFinder;
import org.mvplugins.multiverse.core.world.helpers.EnforcementHandler;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Multiverse's Listener for players.
 */
@Service
final class MVPlayerListener implements CoreListener {
    private final Plugin plugin;
    private final CoreConfig config;
    private final Provider<WorldManager> worldManagerProvider;
    private final BlockSafety blockSafety;
    private final Server server;
    private final TeleportQueue teleportQueue;
    private final MVEconomist economist;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;
    private final Provider<MVCommandManager> commandManagerProvider;
    private final DestinationsProvider destinationsProvider;
    private final EnforcementHandler enforcementHandler;
    private final DimensionFinder dimensionFinder;
    private final CorePermissionsChecker corePermissionsChecker;

    @Inject
    MVPlayerListener(
            MultiverseCore plugin,
            CoreConfig config,
            Provider<WorldManager> worldManagerProvider,
            BlockSafety blockSafety,
            Server server,
            TeleportQueue teleportQueue,
            MVEconomist economist,
            WorldEntryCheckerProvider worldEntryCheckerProvider,
            Provider<MVCommandManager> commandManagerProvider,
            DestinationsProvider destinationsProvider,
            EnforcementHandler enforcementHandler,
            DimensionFinder dimensionFinder,
            CorePermissionsChecker corePermissionsChecker) {
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
        this.dimensionFinder = dimensionFinder;
        this.corePermissionsChecker = corePermissionsChecker;
    }

    private WorldManager getWorldManager() {
        return worldManagerProvider.get();
    }

    private MVCommandManager getCommandManager() {
        return commandManagerProvider.get();
    }

    private PluginLocales getLocales() {
        return getCommandManager().getLocales();
    }

    /**
     * This method is called when a player respawns.
     *
     * @param event The Event that was fired.
     */
    @EventMethod
    @EventPriorityKey("mvcore-player-respawn")
    @DefaultEventPriority(EventPriority.LOW)
    void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LoadedMultiverseWorld mvWorld = getWorldManager().getLoadedWorld(player.getWorld()).getOrNull();
        if (mvWorld == null) {
            Logging.finer("Player '%s' died in a world that is not managed by Multiverse.", player.getName());
            return;
        }

        if (mvWorld.getBedRespawn() && event.isBedSpawn()) {
            Logging.fine("Spawning %s at their bed.", player.getName());
            return;
        }
        if (mvWorld.getAnchorRespawn() && event.isAnchorSpawn()) {
            Logging.fine("Spawning %s at their anchor.", player.getName());
            return;
        }

        getRespawnWorld(mvWorld)
                .onEmpty(() -> Logging.fine("No respawn-world determined for world '%s'.", mvWorld.getName()))
                .flatMap(respawnWorld -> {
                    Logging.finer("Using respawn-world '%s' for world '%s'.", respawnWorld.getName(), mvWorld.getName());
                    return getMostAccurateRespawnLocation(respawnWorld, event.getRespawnLocation())
                            .onEmpty(() -> Logging.finer("No accurate respawn-location determined for world '%s'.",
                                    mvWorld.getName()));
                })
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

    private Option<LoadedMultiverseWorld> getRespawnWorld(LoadedMultiverseWorld mvWorld) {
        if (!mvWorld.getRespawnWorldName().isEmpty()) {
            Logging.finer("Using configured respawn-world for world '%s'.", mvWorld.getName());
            return getWorldManager().getLoadedWorld(mvWorld.getRespawnWorldName())
                    .onEmpty(() -> {
                        Logging.warning("World '%s' has respawn-world property of '%s' that does not exist!",
                                mvWorld.getName(), mvWorld.getRespawnWorldName());
                    });
        } else if (!dimensionFinder.isOverworld(mvWorld) && config.getDefaultRespawnInOverworld()) {
            Logging.finer("Defaulting to overworld for world '%s'.", mvWorld.getName());
            return dimensionFinder.getOverworldWorld(mvWorld).flatMap(getWorldManager()::getLoadedWorld)
                    .onEmpty(() -> {
                        Logging.warning("World '%s' has no overworld to teleport to!",
                                mvWorld.getName());
                    });
        } else if (config.getDefaultRespawnWithinSameWorld()) {
            Logging.finer("Defaulting to same world for world '%s'.", mvWorld.getName());
            return Option.of(mvWorld);
        }
        return Option.none();
    }

    private Option<Location> getMostAccurateRespawnLocation(LoadedMultiverseWorld mvWorld, Location defaultRespawnLocation) {
        if (!config.getEnforceRespawnAtWorldSpawn() && Objects.equals(defaultRespawnLocation.getWorld(), mvWorld.getBukkitWorld().getOrNull())) {
            Logging.fine("Respawn location is within same world as respawn-world, not overriding.");
            return Option.none();
        }
        return Option.of(mvWorld.getSpawnLocation());
    }

    @EventClass("org.spigotmc.event.player.PlayerSpawnLocationEvent")
    @EventPriorityKey("mvcore-player-spawn-location")
    EventRunnable playerSpawnLocation() {
        return new EventRunnable<PlayerSpawnLocationEvent>() {
            @Override
            public void onEvent(PlayerSpawnLocationEvent event) {
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
                handleGameModeAndFlight(player, event.getSpawnLocation().getWorld());
            }

            private void handleFirstSpawn(PlayerSpawnLocationEvent event) {
                if (!config.getFirstSpawnOverride()) {
                    Logging.finer("FirstSpawnOverride is disabled");
                    // User has disabled the feature in config
                    return;
                }
                Logging.fine("Moving NEW player to(firstspawnoverride): %s", config.getFirstSpawnLocation());
                destinationsProvider.parseDestination(config.getFirstSpawnLocation())
                        .map(destination -> destination.getLocation(event.getPlayer())
                                .peek(event::setSpawnLocation)
                                .onEmpty(() -> Logging.warning("The destination in FirstSpawnLocation in config is invalid")))
                        .onFailure(failure -> {
                            Logging.warning("Invalid destination in FirstSpawnLocation in config: %s");
                            Logging.warning(failure.getFailureMessage().formatted(getLocales()));
                        });
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
                if (corePermissionsChecker.hasJoinLocationBypassPermission(event.getPlayer())) {
                    Logging.finer("Player %s has bypass permission for JoinDestination", event.getPlayer().getName());
                    return;
                }
                Logging.finer("JoinDestination is " + config.getJoinDestination());
                destinationsProvider.parseDestination(config.getJoinDestination())
                        .map(destination -> destination.getLocation(event.getPlayer())
                                .peek(event::setSpawnLocation)
                                .onEmpty(() -> Logging.warning("The destination in JoinDestination in config is invalid")))
                        .onFailure(failure -> {
                            Logging.warning("Invalid destination in JoinDestination in config: %s");
                            Logging.warning(failure.getFailureMessage().formatted(getLocales()));
                        });
            }
        };
    }

    /**
     * This method is called when a player changes worlds.
     * @param event The Event that was fired.
     */
    @EventMethod
    @DefaultEventPriority(EventPriority.MONITOR)
    void playerChangedWorld(PlayerChangedWorldEvent event) {
        // Permissions now determine whether or not to handle a gamemode.
        this.handleGameModeAndFlight(event.getPlayer(), event.getPlayer().getWorld());
    }

    /**
     * This method is called when a player teleports anywhere.
     * @param event The Event that was fired.
     */
    @EventMethod
    @EventPriorityKey("mvcore-player-teleport")
    @DefaultEventPriority(EventPriority.HIGHEST)
    void playerTeleport(PlayerTeleportEvent event) {
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

        MultiverseWorld fromWorld = getWorldManager().getLoadedWorld(event.getFrom().getWorld()).getOrNull();
        LoadedMultiverseWorld toWorld = getWorldManager().getLoadedWorld(event.getTo().getWorld()).getOrNull();
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
    @EventMethod
    @DefaultEventPriority(EventPriority.LOWEST)
    void playerPortalCheck(PlayerPortalEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getFrom().getWorld() == null) {
            Logging.warning("PlayerPortalEvent's from world is null!");
            return;
        }
        if (event.getFrom().getWorld().getBlockAt(event.getFrom()).getType() == Material.NETHER_PORTAL) {
            return;
        }

        // Player was actually outside of the portal, adjust the from location
        Location newLocation = blockSafety.findPortalBlockNextTo(event.getFrom());
        if (newLocation != null) {
            event.setFrom(newLocation);
        }
    }

    /**
     * This method is called when a player actually portals via a vanilla style portal.
     * @param event The Event that was fired.
     */
    @EventMethod
    @EventPriorityKey("mvcore-player-portal")
    @DefaultEventPriority(EventPriority.HIGH)
    void playerPortal(PlayerPortalEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getTo() == null || event.getTo().getWorld() == null) {
            Logging.finer("PlayerPortalEvent's to world is null!");
            return;
        }
        if (config.isUsingCustomPortalSearch()) {
            event.setSearchRadius(config.getCustomPortalSearchRadius());
        }
        if (Objects.equals(event.getFrom().getWorld(), event.getTo().getWorld())) {
            // The player is Portaling to the same world.
            Logging.finer("Player '" + event.getPlayer().getName() + "' is portaling to the same world.");
            return;
        }

        MultiverseWorld fromWorld = getWorldManager().getLoadedWorld(event.getFrom().getWorld()).getOrNull();
        LoadedMultiverseWorld toWorld = getWorldManager().getLoadedWorld(event.getTo().getWorld()).getOrNull();
        if (toWorld == null) {
            Logging.fine("Player '" + event.getPlayer().getName() + "' is portaling to world '"
                    + event.getTo().getWorld().getName() + "' which is not managed by Multiverse-Core.  No further "
                    + "actions will be taken by Multiverse-Core.");
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
        this.server.getScheduler().runTaskLater(this.plugin, () -> {
            if (!player.isOnline() || !player.getWorld().equals(world)) {
                return;
            }
            Logging.finer("Handling gamemode and flight for player %s in world '%s'", player.getName(), world.getName());
            enforcementHandler.handleFlightEnforcement(player);
            enforcementHandler.handleGameModeEnforcement(player);
        }, 1L);
    }
}
