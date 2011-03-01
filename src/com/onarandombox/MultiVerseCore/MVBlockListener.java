package com.onarandombox.MultiVerseCore;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
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
    
    public void onBlockFlow(BlockFromToEvent event){
        
    }
    
    public void onBlockPlaced(BlockPlaceEvent event){
        
    }
    
}
