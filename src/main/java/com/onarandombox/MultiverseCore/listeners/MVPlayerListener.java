/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.fernferret.allpay.GenericBank;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.event.MVRespawnEvent;
import com.onarandombox.MultiverseCore.utils.SafeTTeleporter;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import java.util.logging.Level;

public class MVPlayerListener extends PlayerListener {
    MultiverseCore plugin;
    SafeTTeleporter mvteleporter;
    WorldManager worldManager;

    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
        worldManager = plugin.getMVWorldManager();
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Check whether the Server is set to prefix the chat with the World name.
        // If not we do nothing, if so we need to check if the World has an Alias.
        if (MultiverseCore.PrefixChat) {
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

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        World world = event.getPlayer().getWorld();
        MultiverseWorld mvWorld = this.worldManager.getMVWorld(world.getName());
        // If it's not a World MV manages we stop.
        if (mvWorld == null) {
            return;
        }

        if (mvWorld.getBedRespawn()) {
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

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.worldManager.getMVWorlds().size() == 0 && this.plugin.getMVPerms().hasPermission(event.getPlayer(), "multiverse.core.import", true)) {
            event.getPlayer().sendMessage("You don't have any worlds imported into Multiverse!");
            event.getPlayer().sendMessage("You can import your current worlds with " + ChatColor.AQUA + "/mvimport");
            event.getPlayer().sendMessage("or you can create new ones with " + ChatColor.GOLD + "/mvcreate");
            event.getPlayer().sendMessage("If you just wanna see all of the Multiverse Help, type: " + ChatColor.GREEN + "/mv");
        }
        // Handle the Players GameMode setting for the new world.
        if (MultiverseCore.EnforceGameModes) {
            this.handleGameMode(event.getPlayer(), event.getPlayer().getWorld());
        }
    }

    @Override
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        // Handle the Players GameMode setting for the new world.
        if (MultiverseCore.EnforceGameModes) {
            this.handleGameMode(event.getPlayer(), event.getPlayer().getWorld());
        }
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.removePlayerSession(event.getPlayer());
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        MultiverseWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld().getName());
        MultiverseWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld().getName());
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            // The player is Teleporting to the same world.
            this.plugin.log(Level.FINER, "Player '" + event.getPlayer().getName() + "' is teleporting to the same world.");
            return;
        }
        if (MultiverseCore.EnforceAccess) {
            event.setCancelled(!playerCanGoFromTo(fromWorld, toWorld, event.getPlayer()));
            if (event.isCancelled()) {
                this.plugin.log(Level.FINE, "Player '" + event.getPlayer().getName() + "' was DENIED ACCESS to '" + event.getTo().getWorld().getName() +
                        "' because they don't have: multiverse.access." + event.getTo().getWorld().getName());
            }
        } else {
            this.plugin.log(Level.FINE, "Player '" + event.getPlayer().getName() + "' was allowed to go to '" + event.getTo().getWorld().getName() + "' because enforceaccess is off.");
        }
    }

    @Override
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.isCancelled() || event.getTo() == null || event.getFrom() == null) {
            return;
        }

        // If the player was actually outside of the portal, adjust the from location
        if (event.getFrom().getWorld().getBlockAt(event.getFrom()).getType() != Material.PORTAL) {
            Location newloc = SafeTTeleporter.findPortalBlockNextTo(event.getFrom());
            // TODO: Fix this. Currently, we only check for PORTAL blocks. I'll have to figure out what
            // TODO: we want to do here.
            if (newloc != null) {
                event.setFrom(newloc);
            }
        }
        MultiverseWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld().getName());
        MultiverseWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld().getName());
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            // The player is Portaling to the same world.
            this.plugin.log(Level.FINER, "Player '" + event.getPlayer().getName() + "' is portaling to the same world.");
            return;
        }
        if (MultiverseCore.EnforceAccess) {
            event.setCancelled(!playerCanGoFromTo(fromWorld, toWorld, event.getPlayer()));
            if (event.isCancelled()) {
                this.plugin.log(Level.FINE, "Player '" + event.getPlayer().getName() + "' was DENIED ACCESS to '" + event.getTo().getWorld().getName() +
                        "' because they don't have: multiverse.access." + event.getTo().getWorld().getName());
            }
        } else {
            this.plugin.log(Level.FINE, "Player '" + event.getPlayer().getName() + "' was allowed to go to '" + event.getTo().getWorld().getName() + "' because enforceaccess is off.");
        }
    }

    /**
     * Checks to see if player can go to a world given their current status.
     * <p/>
     * The return is a little backwards, and will return a value safe for event.setCancelled.
     *
     * @param fromWorld The MultiverseWorld they are in.
     * @param toWorld   The MultiverseWorld they want to go to.
     * @param player    The player that wants to travel.
     *
     * @return True if they can't go to the world, False if they can.
     */
    private boolean playerCanGoFromTo(MultiverseWorld fromWorld, MultiverseWorld toWorld, Player player) {

        if (toWorld != null) {
            if (!this.plugin.getMVPerms().canEnterWorld(player, toWorld)) {
                player.sendMessage("You don't have access to go here...");
                return false;
            }
        } else {
            //TODO: Determine if this value is false because a world didn't exist
            // or if it was because a world wasn't imported.
            return true;
        }
        if (fromWorld != null) {
            if (fromWorld.getWorldBlacklist().contains(toWorld.getName())) {
                player.sendMessage("You don't have access to go to " + toWorld.getColoredWorldString() + " from " + fromWorld.getColoredWorldString());
                return false;
            }
        }

        // Only check payments if it's a different world:
        if (!toWorld.equals(fromWorld)) {
            // If the player does not have to pay, return now.
            if (this.plugin.getMVPerms().hasPermission(player, toWorld.getExemptPermission().getName(), true)) {
                return true;
            }
            GenericBank bank = plugin.getBank();
            if (!bank.hasEnough(player, toWorld.getPrice(), toWorld.getCurrency(), "You need " + bank.getFormattedAmount(player, toWorld.getPrice(), toWorld.getCurrency()) + " to enter " + toWorld.getColoredWorldString())) {
                return false;
            } else {
                bank.pay(player, toWorld.getPrice(), toWorld.getCurrency());
            }
        }
        return true;
    }

    // FOLLOWING 2 Methods and Private class handle Per Player GameModes.
    private void handleGameMode(Player player, World world) {
        this.plugin.log(Level.FINE, "Handeling gamemode for player: " + player.getName());
        MultiverseWorld mvWorld = this.worldManager.getMVWorld(world.getName());
        if (mvWorld != null) {
            this.handleGameMode(player, mvWorld);
        }
    }

    public void handleGameMode(Player player, MultiverseWorld world) {
        // We perform this task one tick later to MAKE SURE that the player actually reaches the
        // destination world, otherwise we'd be changing the player mode if they havent moved anywhere.
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new HandleGameMode(player, world), 1L);
    }

    /** The following private class is used to handle player game mode changes within a scheduler. */
    private class HandleGameMode implements Runnable {

        private Player player;
        private MultiverseWorld world;

        private HandleGameMode(Player player, MultiverseWorld world) {
            this.player = player;
            this.world = world;
        }

        @Override
        public void run() {
            // Check that the player is in the new world and they haven't been teleported elsewhere or the event cancelled.
            if (player.getWorld().getName().equals(world.getCBWorld().getName())) {
                MultiverseCore.staticLog(Level.FINE, "Handeling gamemode for player: " + player.getName() + ", " + world.getGameMode().toString());
                MultiverseCore.staticLog(Level.FINE, "PWorld: " + player.getWorld());
                MultiverseCore.staticLog(Level.FINE, "AWorld: " + world);
                player.setGameMode(world.getGameMode());
            }
        }
    }
}
