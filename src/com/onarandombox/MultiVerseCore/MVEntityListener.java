package com.onarandombox.MultiVerseCore;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MVEntityListener implements Listener {

    MultiVerseCore plugin;
    
    public MVEntityListener(MultiVerseCore plugin) {
        this.plugin = plugin;
    }
    
    public void onEntityDamaged(EntityDamageEvent event){
        
    }

}
