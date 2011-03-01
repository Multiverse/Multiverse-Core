package com.onarandombox.MultiVerseCore;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldEvent;

public class MVWorldListener implements Listener {

    MultiVerseCore plugin;
    
    public MVWorldListener(MultiVerseCore plugin){
        this.plugin = plugin;
    }
    
    public void onWorldLoad(WorldEvent event){
        World world = event.getWorld();
    }
}
