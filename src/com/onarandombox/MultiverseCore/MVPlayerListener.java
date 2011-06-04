package com.onarandombox.MultiverseCore;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MVPlayerListener extends PlayerListener {
    private final Logger log = Logger.getLogger("Minecraft");
    MultiverseCore plugin;

    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // MultiVerseCore.debugMsg(event.getPlayer().getName() + " just tried to Teleport");
        // event.setCancelled(true);
        // Entity entity = event.getPlayer().;
        // MultiVerseCore.log.info("1 - " + event.getTo().toString());
        // MultiVerseCore.log.info("2 - " + event.getPlayer().getLocation().toString());
        MVPlayerSession ps = this.plugin.getPlayerSession(event.getPlayer());
        ps.setRespawnWorld(event.getTo().getWorld());
    }

    public void onPlayerKick(PlayerKickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer(); // Grab Player
        Location loc = p.getLocation(); // Grab Location
        /**
         * Check the Player has actually moved a block to prevent unneeded calculations...
         * This is to prevent huge performance drops on high player count servers.
         */
        MVPlayerSession ps = this.plugin.getPlayerSession(p);
        if (ps.loc.getBlockX() == loc.getBlockX() && ps.loc.getBlockY() == loc.getBlockY() && ps.loc.getBlockZ() == loc.getBlockZ()) {
            return;
        } else {
            ps.loc = loc; // Update the Players Session to the new Location.
        }
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {

    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // TODO: Handle Global Respawn from config
        
        // TODO: Handle Alternate Respawn from config
        
        MVPlayerSession ps = this.plugin.getPlayerSession(event.getPlayer());
        event.setRespawnLocation(ps.getRespawnWorld().getSpawnLocation());
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {

    }
}
