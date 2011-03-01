package com.onarandombox.MultiVerseCore;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class MVEntityListener extends EntityListener {

    MultiVerseCore plugin;
    
    public MVEntityListener(MultiVerseCore plugin) {
        this.plugin = plugin;
    }
    
    public void onEntityDamaged(EntityDamageEvent event){
        
    }

}
