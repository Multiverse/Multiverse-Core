package com.onarandombox.MultiverseCore;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.onarandombox.MultiverseCore.event.MVRespawnEvent;

public class MVPlayerListener extends PlayerListener {
    MultiverseCore plugin;
    MVTeleport mvteleporter;

    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    // Taken out until we do persistance.
    // @Override
    // public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
    // Location bedLoc = event.getBed().getLocation();
    // bedLoc = this.plugin.getTeleporter().getSafeBedDestination(bedLoc);
    // this.plugin.getPlayerSession(event.getPlayer()).setRespawnLocation(bedLoc);
    // event.getPlayer().sendMessage("You should come back here when you type '/mv sleep'!");
    // }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Check whether the Server is set to prefix the chat with the World name. If not we do nothing, if so we need to check if the World has an Alias.
        if (this.plugin.getConfig().getBoolean("worldnameprefix", true)) {
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
        this.plugin.removePlayerSession(event.getPlayer());
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        MVWorld fromWorld = this.plugin.getMVWorld(event.getTo().getWorld().getName());
        MVWorld toWorld = this.plugin.getMVWorld(event.getTo().getWorld().getName());
        if (toWorld != null) {
            if (!this.plugin.getPermissions().canEnterWorld(event.getPlayer(), toWorld)) {
                event.getPlayer().sendMessage("You don't have access to go here...");
                event.setCancelled(true);
                return;
            }
        }
        if (fromWorld != null) {
            if (fromWorld.getWorldBlacklist().contains(toWorld.getName())) {
                event.getPlayer().sendMessage("You don't have access to go to " + toWorld.getColoredWorldString() + " from " + fromWorld.getColoredWorldString());
                event.setCancelled(true);
                return;
            }
        }
    }
}
