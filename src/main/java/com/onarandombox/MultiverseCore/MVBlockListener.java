package com.onarandombox.MultiverseCore;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

//import org.bukkit.event.block.BlockRightClickEvent;

public class MVBlockListener extends BlockListener {

    MultiverseCore plugin;

    public MVBlockListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    // public void onBlockRightClicked(BlockRightClickEvent event){

    // }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {

    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.isCancelled()) {
            return;
        }

        int id = event.getChangedTypeId();

        if (id == 90) { // && config.getBoolean("portalanywhere", false)
            event.setCancelled(true);
            return;
        }
    }

    public void onBlockPlaced(BlockPlaceEvent event) {

    }

}
