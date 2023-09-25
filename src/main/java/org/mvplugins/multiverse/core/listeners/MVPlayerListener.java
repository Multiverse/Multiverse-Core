/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import java.util.Objects;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.event.MVRespawnEvent;
import org.mvplugins.multiverse.core.inject.InjectableListener;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.TeleportQueue;
import org.mvplugins.multiverse.core.utils.result.ResultChain;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.EntryFeeResult;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;
import org.mvplugins.multiverse.core.world.helpers.EnforcementHandler;

/**
 * Multiverse's Listener for players.
 */
@Service
public class MVPlayerListener implements InjectableListener {
    private final Plugin plugin;
    private final MVCoreConfig config;
    private final Provider<WorldManager> worldManagerProvider;
    private final BlockSafety blockSafety;
    private final AsyncSafetyTeleporter safetyTeleporter;
    private final TeleportQueue teleportQueue;
    private final MVEconomist economist;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;
    private final Provider<MVCommandManager> commandManagerProvider;
    private final DestinationsProvider destinationsProvider;
    private final EnforcementHandler enforcementHandler;

    @Inject
    MVPlayerListener(
            MultiverseCore plugin,
            MVCoreConfig config,
            Provider<WorldManager> worldManagerProvider,
            BlockSafety blockSafety,
            AsyncSafetyTeleporter safetyTeleporter,
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
     * This method is called when a player respawns.
     *
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void playerRespawn(PlayerRespawnEvent event) {
        getWorldManager()
                .getLoadedWorld(event.getPlayer().getWorld())
                .filter(mvWorld -> isBedOrAnchorRespawn(event, mvWorld))
                .flatMap(MultiverseWorld::getRespawnWorld)
                .map(this::getMostAccurateRespawnLocation)
                .peek(respawnLocation -> callMVRepawnEvent(event, respawnLocation));
    }

    private static boolean isBedOrAnchorRespawn(PlayerRespawnEvent event, LoadedMultiverseWorld mvWorld) {
        if (mvWorld.getBedRespawn() && (event.isBedSpawn() || event.isAnchorSpawn())) {
            Logging.fine("Spawning %s at their %s.",
                    event.getPlayer().getName(),
                    event.isBedSpawn() ? "BED" : "ANCHOR");
            return false;
        }
        return true;
    }

    private Location getMostAccurateRespawnLocation(World world) {
        return getWorldManager().getLoadedWorld(world.getName())
                .map(LoadedMultiverseWorld::getSpawnLocation)
                .getOrElse(world.getSpawnLocation());
    }

    private void callMVRepawnEvent(PlayerRespawnEvent event, Location respawnLocation) {
        MVRespawnEvent respawnEvent = new MVRespawnEvent(
                respawnLocation, event.getPlayer(), "compatability");
        Bukkit.getPluginManager().callEvent(respawnEvent);
        event.setRespawnLocation(respawnEvent.getPlayersRespawnLocation());
    }

    /**
     * This method is called when a player joins the server.
     *
     * @param event The Event that was fired.
     */
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            playerJoinAgain(event);
        } else {
            playerJoinFirstTime(event);
        }

        // Handle the Players GameMode setting for the new world.
        this.handleGameModeAndFlight(event.getPlayer(), event.getPlayer().getWorld());
    }

    private void playerJoinAgain(PlayerJoinEvent event) {
        Logging.finer("Player joined AGAIN!");
        if (!config.getEnableJoinDestination()) {
            return;
        }

        Option.of(destinationsProvider.parseDestination(config.getJoinDestination()))
                .peek(joinDestination -> {
                    Logging.fine("Moving player to: %s", joinDestination);
                    safetyTeleporter.teleportSafely(event.getPlayer(), joinDestination);
                })
                .onEmpty(() -> {
                    Logging.warning("JoinDestination '%s' is invalid! Not teleporting player!",
                            config.getJoinDestination());
                });
    }

    private void playerJoinFirstTime(PlayerJoinEvent event) {
        Logging.finer("Player joined for the FIRST time!");
        if (!config.getFirstSpawnOverride()) {
            return;
        }

        Option.of(destinationsProvider.parseDestination(config.getFirstSpawnLocation()))
                .peek(firstSpawnDestination -> {
                    Logging.fine("Moving NEW player to(firstspawnoverride): %s", firstSpawnDestination);
                    safetyTeleporter.teleportSafely(event.getPlayer(), firstSpawnDestination);
                })
                .onEmpty(() -> {
                    Logging.warning("FirstSpawnLocation '%s' is invalid! Not teleporting player!",
                            config.getFirstSpawnLocation());
                });
    }

    /**
     * This method is called when a player changes worlds.
     *
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerChangedWorld(PlayerChangedWorldEvent event) {
        // Permissions now determine whether or not to handle a gamemode.
        handleGameModeAndFlight(event.getPlayer(), event.getPlayer().getWorld());
    }

    /**
     * This method is called when a player teleports anywhere.
     *
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) {
            return;
        }

        Logging.finer("Got teleport event for player '%s' with cause '%s'",
                event.getPlayer().getName(), event.getCause());

        Player teleportee = event.getPlayer();
        CommandSender teleporter = getTeleporter(teleportee);

        World fromWorld = event.getFrom().getWorld();
        World toWorld = event.getTo().getWorld();
        if (toWorld == null) {
            Logging.warning("Player '%s' is teleporting to a world that does not exist. "
                    + "No further actions will be taken by Multiverse-Core.", teleportee.getName());
            return;
        }

        if (Objects.equals(fromWorld, toWorld)) {
            // The player is Teleporting to the same world.
            Logging.finer("Player '%s' is teleporting to the same world '%s'.",
                    teleportee.getName(), toWorld.getName());
            return;
        }

        checkTeleportEntry(event, toWorld, fromWorld, teleporter, teleportee);
    }

    private @NotNull CommandSender getTeleporter(Player teleportee) {
        return teleportQueue.popFromQueue(teleportee.getName())
                .map(teleporterName -> {
                    Logging.finer("Player '%s' is being teleported by '%s'.",
                            teleportee.getName(), teleporterName);
                    return teleporterName.equals("CONSOLE")
                            ? Bukkit.getConsoleSender()
                            : Bukkit.getPlayerExact(teleporterName);
                })
                .getOrElse(teleportee);
    }

    private void checkTeleportEntry(
            PlayerTeleportEvent event, World toWorld, World fromWorld, CommandSender teleporter, Player teleportee) {
        getWorldManager().getLoadedWorld(toWorld).peek(mvToWorld -> {
            LoadedMultiverseWorld mvFromWorld = getWorldManager().getLoadedWorld(fromWorld).getOrNull();
            ResultChain entryResult = worldEntryCheckerProvider.forSender(teleporter)
                    .canEnterWorld(mvFromWorld, mvToWorld)
                    .onSuccessReason(EntryFeeResult.Success.class, reason -> {
                        if (teleporter instanceof Player player && reason == EntryFeeResult.Success.ENOUGH_MONEY) {
                            economist.payEntryFee(player, mvToWorld);
                            // Send payment receipt
                        }
                    })
                    .onFailure(results -> {
                        event.setCancelled(true);
                        getCommandManager().getCommandIssuer(teleporter).sendError(results.getLastResultMessage());
                    });
            Logging.fine("Teleport result: %s", entryResult);
        }).onEmpty(() -> {
            Logging.fine("Player '%s' is teleporting to world '%s' which is not managed by Multiverse-Core. "
                    + "No further actions will be taken by Multiverse-Core.",
                    teleportee.getName(),
                    toWorld.getName());
        });
    }

    /**
     * This method is called to adjust the portal location to the actual portal location (and not
     * right outside of it.
     *
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerPortalCheck(PlayerPortalEvent event) {
        World fromWorld = event.getFrom().getWorld();
        if (fromWorld != null && fromWorld.getBlockAt(event.getFrom()).getType() != Material.NETHER_PORTAL) {
            Location newloc = blockSafety.findPortalBlockNextTo(event.getFrom());
            // TODO: Fix this. Currently, we only check for PORTAL blocks. I'll have to figure out what
            // TODO: we want to do here.
            if (newloc != null) {
                event.setFrom(newloc);
            }
        }
    }

    /**
     * This method is called when a player actually portals via a vanilla style portal.
     *
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerPortal(PlayerPortalEvent event) {
        if (event.getTo() == null) {
            return;
        }

        if (config.isUsingCustomPortalSearch()) {
            event.setSearchRadius(config.getCustomPortalSearchRadius());
        }

        if (Objects.equals(event.getFrom().getWorld(), event.getTo().getWorld())) {
            Logging.finer("Player '" + event.getPlayer().getName() + "' is portaling to the same world.");
            return;
        }

        checkPortalTeleportEntry(event);
    }

    private void checkPortalTeleportEntry(PlayerPortalEvent event) {
        getWorldManager().getLoadedWorld(event.getTo().getWorld()).peek(toWorld -> {
            LoadedMultiverseWorld fromWorld = getWorldManager().getLoadedWorld(event.getFrom().getWorld()).getOrNull();
            ResultChain entryResult = worldEntryCheckerProvider.forSender(event.getPlayer())
                    .canEnterWorld(fromWorld, toWorld)
                    .onFailure(results -> {
                        event.setCancelled(true);
                        getCommandManager().getCommandIssuer(event.getPlayer())
                                .sendError(results.getLastResultMessage());
                    });
            Logging.fine("Teleport result: %s", entryResult);
        });
    }

    /**
     * Handles the gamemode for the specified {@link Player} with a 1 tick delay.
     *
     * @param player The {@link Player}.
     * @param world  The {@link World} the player is supposed to be in.
     */
    private void handleGameModeAndFlight(final Player player, World world) {
        if (!player.isOnline() || !player.getWorld().equals(world)) {
            return;
        }
        enforcementHandler.handleFlightEnforcement(player);
        enforcementHandler.handleGameModeEnforcement(player);
    }
}
