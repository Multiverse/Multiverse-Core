/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.event.MVRespawnEvent;
import com.onarandombox.MultiverseCore.utils.PermissionTools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.logging.Level;

/**
 * Multiverse's {@link Listener} for players.
 */
public class MVPlayerListener implements Listener {
    private MultiverseCore plugin;
    private MVWorldManager worldManager;
    private PermissionTools pt;


    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
        worldManager = plugin.getMVWorldManager();
        pt = new PermissionTools(plugin);
    }
    /**
     * This method is called when a player wants to chat.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void playerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Check whether the Server is set to prefix the chat with the World name.
        // If not we do nothing, if so we need to check if the World has an Alias.
        if (plugin.getMVConfig().getPrefixChat()) {
            String world = event.getPlayer().getWorld().getName();
            String prefix = "";
            // If we're not a MV world, don't do anything
            if (!this.worldManager.isMVWorld(world)) {
                return;
            }
            MultiverseWorld mvworld = this.worldManager.getMVWorld(world);
            if (mvworld.isHidden()) {
                return;
            }
            prefix = mvworld.getColoredWorldString();
            String format = event.getFormat();
            event.setFormat("[" + prefix + "]" + format);
        }
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


        if (mvWorld.getBedRespawn() && event.isBedSpawn()) {
            this.plugin.log(Level.FINE, "Spawning " + event.getPlayer().getName() + " at their bed");
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
            this.plugin.log(Level.FINER, "Player joined for the FIRST time!");
            if (plugin.getMVConfig().getFirstSpawnOverride()) {
                this.plugin.log(Level.FINE, String.format("Moving NEW player to(firstspawnoverride): %s",
                        worldManager.getFirstSpawnWorld().getSpawnLocation()));
                this.sendPlayerToDefaultWorld(p);
            }
            return;
        } else {
            this.plugin.log(Level.FINER, "Player joined AGAIN!");
            if (!this.plugin.getMVPerms().hasPermission(p, "multiverse.access." + p.getWorld().getName(), false)) {
                p.sendMessage("[MV] - Sorry you can't be in this world anymore!");
                this.sendPlayerToDefaultWorld(p);
            }
        }
        // Handle the Players GameMode setting for the new world.
        this.handleGameMode(event.getPlayer(), event.getPlayer().getWorld());
    }

    /**
     * This method is called when a player changes worlds.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerChangedWorld(PlayerChangedWorldEvent event) {
        // Permissions now determine whether or not to handle a gamemode.
        this.handleGameMode(event.getPlayer(), event.getPlayer().getWorld());
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
        this.plugin.log(Level.FINER, String.format(
                "Got teleport event for player '%s' with cause '%s'",
                event.getPlayer().getName(), event.getCause()));
        if (event.isCancelled()) {
            return;
        }
        Player teleportee = event.getPlayer();
        CommandSender teleporter = null;
        String teleporterName = MultiverseCore.getPlayerTeleporter(teleportee.getName());
        if (teleporterName != null) {
            if (teleporterName.equals("CONSOLE")) {
                this.plugin.log(Level.FINER, "We know the teleporter is the console! Magical!");
                teleporter = this.plugin.getServer().getConsoleSender();
            } else {
                teleporter = this.plugin.getServer().getPlayer(teleporterName);
            }
        }
        this.plugin.log(Level.FINER, String.format("Inferred sender '%s' from name '%s', fetched from name '%s'",
                teleporter, teleporterName, teleportee.getName()));
        MultiverseWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld().getName());
        MultiverseWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld().getName());
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            // The player is Teleporting to the same world.
            this.plugin.log(Level.FINER, String.format("Player '%s' is teleporting to the same world.",
                    teleportee.getName()));
            this.stateSuccess(teleportee.getName(), toWorld.getAlias());
            return;
        }
        // TODO: Refactor these lines.
        // Charge the teleporter
        event.setCancelled(!pt.playerHasMoneyToEnter(fromWorld, toWorld, teleporter, teleportee, true));
        if (event.isCancelled() && teleporter != null) {
            this.plugin.log(Level.FINE, String.format(
                    "Player '%s' was DENIED ACCESS to '%s' because '%s' don't have the FUNDS required to enter it.",
                    teleportee.getName(), toWorld.getAlias(), teleporter.getName()));

            return;
        }
        if (plugin.getMVConfig().getEnforceAccess()) {
            event.setCancelled(!pt.playerCanGoFromTo(fromWorld, toWorld, teleporter, teleportee));
            if (event.isCancelled() && teleporter != null) {
                this.plugin.log(Level.FINE, String.format(
                        "Player '%s' was DENIED ACCESS to '%s' because '%s' don't have: multiverse.access.%s",
                        teleportee.getName(), toWorld.getAlias(), teleporter.getName(),
                        event.getTo().getWorld().getName()));
            } else {
                this.stateSuccess(teleportee.getName(), toWorld.getAlias());
            }
        } else {
            this.plugin.log(Level.FINE, String.format(
                    "Player '%s' was allowed to go to '%s' because enforceaccess is off.",
                    teleportee.getName(), toWorld.getAlias()));
        }
    }

    private void stateSuccess(String playerName, String worldName) {
        this.plugin.log(Level.FINE, String.format(
                                        "MV-Core is allowing Player '%s' to go to '%s'.",
                                        playerName, worldName));
    }

    /**
     * This method is called to adjust the portal location to the actual portal location (and not
     * right outside of it.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerPortalCheck(PlayerPortalEvent event) {
        this.plugin.log(Level.FINE, "CALLING CORE-ADJUST!!!");
        if (event.isCancelled() || event.getFrom() == null) {
            return;
        }

        // REMEMBER! getTo MAY be NULL HERE!!!
        // If the player was actually outside of the portal, adjust the from location
        if (event.getFrom().getWorld().getBlockAt(event.getFrom()).getType() != Material.PORTAL) {
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
            this.plugin.log(Level.FINER, "Player '" + event.getPlayer().getName() + "' is portaling to the same world.");
            return;
        }
        event.setCancelled(!pt.playerHasMoneyToEnter(fromWorld, toWorld, event.getPlayer(), event.getPlayer(), true));
        if (event.isCancelled()) {
            this.plugin.log(Level.FINE, String.format("Player '%s' was DENIED ACCESS to '%s' because they don't have the FUNDS required to enter.",
                    event.getPlayer().getName(), event.getTo().getWorld().getName()));
            return;
        }
        if (plugin.getMVConfig().getEnforceAccess()) {
            event.setCancelled(!pt.playerCanGoFromTo(fromWorld, toWorld, event.getPlayer(), event.getPlayer()));
            if (event.isCancelled()) {
                this.plugin.log(Level.FINE, String.format("Player '%s' was DENIED ACCESS to '%s' because they don't have: multiverse.access.%s",
                        event.getPlayer().getName(), event.getTo().getWorld().getName(), event.getTo().getWorld().getName()));
            }
        } else {
            this.plugin.log(Level.FINE, String.format("Player '%s' was allowed to go to '%s' because enforceaccess is off.",
                    event.getPlayer().getName(), event.getTo().getWorld().getName()));
        }
    }

    private void sendPlayerToDefaultWorld(final Player player) {
        // Remove the player 1 tick after the login. I'm sure there's GOT to be a better way to do this...
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
            new Runnable() {
                public void run() {
                    player.teleport(plugin.getMVWorldManager().getFirstSpawnWorld().getSpawnLocation());
                }
            }, 1L);
    }

    // FOLLOWING 2 Methods and Private class handle Per Player GameModes.
    private void handleGameMode(Player player, World world) {

        MultiverseWorld mvWorld = this.worldManager.getMVWorld(world.getName());
        if (mvWorld != null) {
            this.handleGameMode(player, mvWorld);
        }
    }

    /**
     * Handles the gamemode for the specified {@link Player}.
     * @param player The {@link Player}.
     * @param world The world the player is in.
     */
    public void handleGameMode(final Player player, final MultiverseWorld world) {
        // We perform this task one tick later to MAKE SURE that the player actually reaches the
        // destination world, otherwise we'd be changing the player mode if they havent moved anywhere.
        if (!this.pt.playerCanIgnoreGameModeRestriction(world, player)) {
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
                new Runnable() {
                    public void run() {
                        // Check that the player is in the new world and they haven't been teleported elsewhere or the event cancelled.
                        if (player.getWorld() == world.getCBWorld()) {
                            MultiverseCore.staticLog(Level.FINE, String.format(
                                    "Handling gamemode for player: %s, Changing to %s",
                                    player.getName(), world.getGameMode().toString()));
                            MultiverseCore.staticLog(Level.FINEST, "From World: " + player.getWorld());
                            MultiverseCore.staticLog(Level.FINEST, "To World: " + world);
                            player.setGameMode(world.getGameMode());
                        } else {
                            MultiverseCore.staticLog(Level.FINE,
                                    String.format("The gamemode was NOT changed for player '%s' because he is now in world '%s' instead of world '%s'",
                                    player.getName(), player.getWorld().getName(), world.getName()));
                        }
                    }
                }, 1L);
        } else {
            this.plugin.log(Level.FINE, String.format("Player: %s is IMMUNE to gamemode changes!", player.getName()));
        }
    }
}
