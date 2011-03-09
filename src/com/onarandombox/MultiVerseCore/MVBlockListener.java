package com.onarandombox.MultiVerseCore;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;

public class MVBlockListener extends BlockListener {

    MultiVerseCore plugin;
    
    public MVBlockListener(MultiVerseCore plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockRightClicked(BlockRightClickEvent event){
        
    }
    
    public void onBlockDamage(BlockDamageEvent event){
        
    }
    
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if(event.isCancelled())
        {
            return;
        }

        int id = event.getChangedTypeId();

        if (id == 90) { // && config.getBoolean("portalanywhere", false)
            event.setCancelled(true);
            return;
        }
    }
    
    public void onBlockPlaced(BlockPlaceEvent event){
        
    }
    
}
