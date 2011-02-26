package com.onarandombox.MultiVerseCore;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;

public class MVBlockListener implements Listener {

    MultiVerseCore plugin;
    
    public MVBlockListener(MultiVerseCore plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockRightClicked(BlockRightClickEvent event){
        
    }
    
    public void onBlockDamage(BlockDamageEvent event){
        
    }
    
    public void onBlockFlow(BlockFromToEvent event){
        
    }
    
    public void onBlockPlaced(BlockPlaceEvent event){
        
    }
    
}
