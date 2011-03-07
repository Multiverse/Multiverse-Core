package com.onarandombox.MultiVerseCore;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MVPlayerListener extends PlayerListener {

    MultiVerseCore plugin;
    
    public MVPlayerListener(MultiVerseCore plugin) {
        this.plugin = plugin;
    }

    public void onPlayerTeleport(PlayerMoveEvent event){
        //MultiVerseCore.debugMsg(event.getPlayer().getName() + " just tried to Teleport");
        //event.setCancelled(true);
    }
    
    public void onPlayerMove(PlayerMoveEvent event){
        
    }
    
    public void onPlayerChat(PlayerChatEvent event){
        
    }
    
    public void onPlayerRespawn(PlayerRespawnEvent event){
        
    }
    
    public void onPlayerJoin(PlayerEvent event){
        
    }
    
    public void onPlayerQuit(PlayerEvent event){
        
    }   
}
