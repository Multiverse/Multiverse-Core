package com.onarandombox.MultiverseCore;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MVPlayerListener extends PlayerListener {

    MultiverseCore plugin;

    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    public void onPlayerTeleport(PlayerMoveEvent event) {
        // MultiVerseCore.debugMsg(event.getPlayer().getName() + " just tried to Teleport");
        // event.setCancelled(true);
        // Entity entity = event.getPlayer().;
        // MultiVerseCore.log.info("1 - " + event.getTo().toString());
        // MultiVerseCore.log.info("2 - " + event.getPlayer().getLocation().toString());
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

    }

    public void onPlayerJoin(PlayerEvent event) {

    }

    public void onPlayerQuit(PlayerEvent event) {

    }
}
