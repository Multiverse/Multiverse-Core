package com.onarandombox.MultiverseCore;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.onarandombox.MultiverseCore.event.MVRespawnEvent;

public class MVPlayerListener extends PlayerListener {
    MultiverseCore plugin;
    MVTeleport mvteleporter;

    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer(); // Grab Player
        Location loc = p.getLocation(); // Grab Location
        /**
         * Check the Player has actually moved a block to prevent unneeded calculations... This is to prevent huge performance drops on high player count servers.
         */
        MVPlayerSession ps = this.plugin.getPlayerSession(p);
        if (ps.loc.getBlockX() == loc.getBlockX() && ps.loc.getBlockY() == loc.getBlockY() && ps.loc.getBlockZ() == loc.getBlockZ()) {
            return;
        } else {
            ps.loc = loc; // Update the Players Session to the new Location.
        }
    }

    @Override
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Location bedLoc = event.getBed().getLocation();
        bedLoc = this.plugin.getTeleporter().getSafeBedDestination(bedLoc);
        this.plugin.getPlayerSession(event.getPlayer()).setRespawnLocation(bedLoc);
        event.getPlayer().sendMessage("You should come back here when you type '/mv sleep'!");
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        // Not sure if this should be a separate plugin... in here for now!!!
        // FernFerret

        if (event.isCancelled()) {
            return;
        }
        /**
         * Check whether the Server is set to prefix the chat with the World name. If not we do nothing, if so we need to check if the World has an Alias.
         */
        if (this.plugin.configMV.getBoolean("worldnameprefix", true)) {

            String world = event.getPlayer().getWorld().getName();

            String prefix = "";

            // If we're not a MV world, don't do anything
            if (!this.plugin.isMVWorld(world)) {
                return;
            }
            MVWorld mvworld = this.plugin.getMVWorld(world);
            prefix = mvworld.getColoredWorldString();

            String format = event.getFormat();

            event.setFormat("[" + prefix + "]" + format);
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // TODO: Reimplement bed respawning, needs to be a way to persist the bed location or something otherwise it's not very effective.

        World world = event.getPlayer().getWorld();

        // If it's not a World MV manages we stop.
        if (!this.plugin.isMVWorld(world.getName())) {
            return;
        }

        // Get the MVWorld
        MVWorld mvWorld = this.plugin.getMVWorld(world.getName());
        // Get the instance of the World the player should respawn at.
        MVWorld respawnWorld = null;
        if (this.plugin.isMVWorld(mvWorld.getRespawnToWorld())) {
            respawnWorld = this.plugin.getMVWorld(mvWorld.getRespawnToWorld());
        }

        // If it's null then it either means the World doesn't exist or the value is blank, so we don't handle it.
        if (respawnWorld == null) {
            return;
        }

        Location respawnLocation = respawnWorld.getCBWorld().getSpawnLocation();

        MVRespawnEvent respawnEvent = new MVRespawnEvent(respawnLocation, event.getPlayer(), "compatability");
        this.plugin.getServer().getPluginManager().callEvent(respawnEvent);
        event.setRespawnLocation(respawnEvent.getPlayersRespawnLocation());
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.plugin.getMVWorlds().size() == 0 && this.plugin.ph.hasPermission(event.getPlayer(), "multiverse.world.import", true)) {
            event.getPlayer().sendMessage("You don't have any worlds imported into Multiverse!");
            event.getPlayer().sendMessage("You can import your current worlds with " + ChatColor.AQUA + "/mvimport");
            event.getPlayer().sendMessage("or you can create new ones with " + ChatColor.GOLD + "/mvcreate");
            event.getPlayer().sendMessage("If you just wanna see all of the Multiverse Help, type: " + ChatColor.GREEN + "/mv");
        }
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {

    }
}
