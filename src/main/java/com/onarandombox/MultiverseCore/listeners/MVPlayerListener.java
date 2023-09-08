/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.RespawnType;
import com.onarandombox.MultiverseCore.event.MVRespawnEvent;
import com.onarandombox.MultiverseCore.utils.CompatibilityLayer;
import com.onarandombox.MultiverseCore.utils.PermissionTools;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Multiverse's {@link Listener} for players.
 */
public class MVPlayerListener implements Listener {
    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private final PermissionTools pt;

    private final Map<String, String> playerWorld = new ConcurrentHashMap<String, String>();

    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
        worldManager = plugin.getMVWorldManager();
        pt = new PermissionTools(plugin);
    }

    /**
     * @return the playerWorld-map
     */
    public Map<String, String> getPlayerWorld() {
        return playerWorld;
    }

    /**
     * This method is called when a player respawns.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void playerRespawn(PlayerRespawnEvent event) {
        World world = event.getPlayer().getWorld();
        MultiverseWorld mvWorld = this.worldManager.getMVWorld(world.getName());
        // If it's not a World MV manages we stop.
        if (mvWorld == null) {
            return;
        }

        RespawnType respawnType = RespawnType.OTHER;
        if (event.isBedSpawn()) {
            respawnType = RespawnType.BED;
        }
        if (CompatibilityLayer.isAnchorSpawn(event)) {
            respawnType = RespawnType.ANCHOR;
        }

        if (mvWorld.getBedRespawn() && (respawnType == RespawnType.BED || respawnType == RespawnType.ANCHOR)) {
            Logging.fine("Spawning %s at their %s", event.getPlayer().getName(), respawnType);
            return;
        }

        // Get the instance of the World the player should respawn at.
        MultiverseWorld respawnWorld = null;
        if (this.worldManager.isMVWorld(mvWorld.getRespawnToWorld())) {
            respawnWorld = this.worldManager.getMVWorld(mvWorld.getRespawnToWorld());
        }

        // If it's null then it either means the World doesn't exist or the value is blank, so we don't handle it.
        // NOW: We'll always handle it to get more accurate spawns
        if (respawnWorld != null) {
            world = respawnWorld.getCBWorld();
        }
        // World has been set to the appropriate world
        Location respawnLocation = getMostAccurateRespawnLocation(world);

        MVRespawnEvent respawnEvent = new MVRespawnEvent(respawnLocation, event.getPlayer(), "compatability");
        this.plugin.getServer().getPluginManager().callEvent(respawnEvent);
        event.setRespawnLocation(respawnEvent.getPlayersRespawnLocation());
    }

    private Location getMostAccurateRespawnLocation(World w) {
        MultiverseWorld mvw = this.worldManager.getMVWorld(w.getName());
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
        Player p = event.getPlayer();
        if (!p.hasPlayedBefore()) {
            Logging.finer("Player joined for the FIRST time!");
            if (plugin.getMVConfig().getFirstSpawnOverride()) {
                Logging.fine("Moving NEW player to(firstspawnoverride): "
                        + worldManager.getFirstSpawnWorld().getSpawnLocation());
                this.sendPlayerToDefaultWorld(p);
            }
            return;
        } else {
            Logging.finer("Player joined AGAIN!");
            if (this.plugin.getMVConfig().getEnforceAccess() // check this only if we're enforcing access!
                    && !this.plugin.getMVPerms().hasPermission(p, "multiverse.access." + p.getWorld().getName(), false)) {
                p.sendMessage("[MV] - Sorry you can't be in this world anymore!");
                this.sendPlayerToDefaultWorld(p);
            }
        }
        // Handle the Players GameMode setting for the new world.
        this.handleGameModeAndFlight(event.getPlayer(), event.getPlayer().getWorld());
        playerWorld.put(p.getName(), p.getWorld().getName());
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
     * This method is called when a player quits the game.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        this.plugin.removePlayerSession(event.getPlayer());
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
        CommandSender teleporter = teleportee;
        String teleporterName = MultiverseCore.getPlayerTeleporter(teleportee.getName());
        if (teleporterName != null) {
            if (teleporterName.equals("CONSOLE")) {
                Logging.finer("We know the teleporter is the console! Magical!");
                teleporter = this.plugin.getServer().getConsoleSender();
            } else {
                teleporter = this.plugin.getServer().getPlayerExact(teleporterName);
            }
        }
        Logging.finer("Inferred sender '" + teleporter + "' from name '"
                + teleporterName + "', fetched from name '" + teleportee.getName() + "'");
        MultiverseWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld().getName());
        MultiverseWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld().getName());
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
        // TODO: Refactor these lines.
        // Charge the teleporter
        event.setCancelled(!pt.playerHasMoneyToEnter(fromWorld, toWorld, teleporter, teleportee, true));
        if (event.isCancelled() && teleporter != null) {
            Logging.fine("Player '" + teleportee.getName()
                    + "' was DENIED ACCESS to '" + toWorld.getAlias()
                    + "' because '" + teleporter.getName()
                    + "' don't have the FUNDS required to enter it.");
            return;
        }

        // Check if player is allowed to enter the world if we're enforcing permissions
        if (plugin.getMVConfig().getEnforceAccess()) {
            event.setCancelled(!pt.playerCanGoFromTo(fromWorld, toWorld, teleporter, teleportee));
            if (event.isCancelled() && teleporter != null) {
                Logging.fine("Player '" + teleportee.getName()
                        + "' was DENIED ACCESS to '" + toWorld.getAlias()
                        + "' because '" + teleporter.getName()
                        + "' don't have: multiverse.access." + event.getTo().getWorld().getName());
                return;
            }
        } else {
            Logging.fine("Player '" + teleportee.getName()
                    + "' was allowed to go to '" + toWorld.getAlias() + "' because enforceaccess is off.");
        }

        // Does a limit actually exist?
        if (toWorld.getPlayerLimit() > -1) {
            // Are there equal or more people on the world than the limit?
            if (toWorld.getCBWorld().getPlayers().size() >= toWorld.getPlayerLimit()) {
                // Ouch the world is full, lets see if the player can bypass that limitation
                if (!pt.playerCanBypassPlayerLimit(toWorld, teleporter, teleportee)) {
                    Logging.fine("Player '" + teleportee.getName()
                            + "' was DENIED ACCESS to '" + toWorld.getAlias()
                            + "' because the world is full and '" + teleporter.getName()
                            + "' doesn't have: mv.bypass.playerlimit." + event.getTo().getWorld().getName());
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // By this point anything cancelling the event has returned on the method, meaning the teleport is a success \o/
        this.stateSuccess(teleportee.getName(), toWorld.getAlias());
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
            Location newloc = this.plugin.getSafeTTeleporter().findPortalBlockNextTo(event.getFrom());
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
        MultiverseWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld().getName());
        MultiverseWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld().getName());
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            // The player is Portaling to the same world.
            Logging.finer("Player '" + event.getPlayer().getName() + "' is portaling to the same world.");
            return;
        }
        event.setCancelled(!pt.playerHasMoneyToEnter(fromWorld, toWorld, event.getPlayer(), event.getPlayer(), true));
        if (event.isCancelled()) {
            Logging.fine("Player '" + event.getPlayer().getName()
                    + "' was DENIED ACCESS to '" + event.getTo().getWorld().getName()
                    + "' because they don't have the FUNDS required to enter.");
            return;
        }
        if (plugin.getMVConfig().getEnforceAccess()) {
            event.setCancelled(!pt.playerCanGoFromTo(fromWorld, toWorld, event.getPlayer(), event.getPlayer()));
            if (event.isCancelled()) {
                Logging.fine("Player '" + event.getPlayer().getName()
                        + "' was DENIED ACCESS to '" + event.getTo().getWorld().getName()
                        + "' because they don't have: multiverse.access." + event.getTo().getWorld().getName());
            }
        } else {
            Logging.fine("Player '" + event.getPlayer().getName()
                    + "' was allowed to go to '" + event.getTo().getWorld().getName()
                    + "' because enforceaccess is off.");
        }
        if (!this.plugin.getMVConfig().isUsingDefaultPortalSearch()) {
            CompatibilityLayer.setPortalSearchRadius(event, this.plugin.getMVConfig().getPortalSearchRadius());
        }
    }

    private void sendPlayerToDefaultWorld(final Player player) {
        // Remove the player 1 tick after the login. I'm sure there's GOT to be a better way to do this...
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            new Runnable() {
                @Override
                public void run() {
                    player.teleport(plugin.getMVWorldManager().getFirstSpawnWorld().getSpawnLocation());
                }
            }, 1L);
    }

    // FOLLOWING 2 Methods and Private class handle Per Player GameModes.
    private void handleGameModeAndFlight(Player player, World world) {

        MultiverseWorld mvWorld = this.worldManager.getMVWorld(world.getName());
        if (mvWorld != null) {
            this.handleGameModeAndFlight(player, mvWorld);
        } else {
            Logging.finer("Not handling gamemode and flight for world '" + world.getName()
                    + "' not managed by Multiverse.");
        }
    }

    /**
     * Handles the gamemode for the specified {@link Player}.
     * @param player The {@link Player}.
     * @param world The world the player is in.
     */
    public void handleGameModeAndFlight(final Player player, final MultiverseWorld world) {
        // We perform this task one tick later to MAKE SURE that the player actually reaches the
        // destination world, otherwise we'd be changing the player mode if they havent moved anywhere.
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            new Runnable() {
                @Override
                public void run() {
                    if (player.getWorld() != world.getCBWorld()) {
                        Logging.fine("The gamemode was NOT changed for player '%s' because he is now in world '%s' instead of world '%s'",
                                player.getName(), player.getWorld().getName(), world.getName());
                        return;
                    }

                    boolean isAllowFlight = player.getAllowFlight();
                    boolean isFlying = player.isFlying();

                    if (!MVPlayerListener.this.pt.playerCanIgnoreGameModeRestriction(world, player)) {
                        // Check that the player is in the new world and they haven't been teleported elsewhere or the event cancelled.
                        Logging.fine("Handling gamemode for player: %s, Changing to %s", player.getName(), world.getGameMode().toString());
                        Logging.finest("From World: %s", player.getWorld());
                        Logging.finest("To World: %s", world);
                        player.setGameMode(world.getGameMode());
                    } else {
                        Logging.fine("Player: " + player.getName() + " is IMMUNE to gamemode changes!");
                    }

                    if (!MVPlayerListener.this.pt.playerCanIgnoreFlyRestriction(world, player)) {
                        if (player.getGameMode() == GameMode.CREATIVE && world.getAllowFlight()) {
                            player.setAllowFlight(true);
                        } else if (isAllowFlight && player.getGameMode() != GameMode.SPECTATOR) {
                            player.setAllowFlight(false);
                            player.setFlying(false);
                        }
                    } else {
                        // Set back to previous state before gamemode change
                        player.setAllowFlight(isAllowFlight);
                        player.setFlying(isFlying);
                        Logging.fine("Player: " + player.getName() + " is IMMUNE to fly changes!");
                    }
                }
            }, 1L);
    }
}
