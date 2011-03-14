package com.onarandombox.utils;

import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Zombie;

import com.onarandombox.MultiVerseCore.MultiVerseCore;

public class PurgeWorlds {

    MultiVerseCore plugin;
    
    public PurgeWorlds(MultiVerseCore plugin){
        this.plugin = plugin;
    }
    
    public void purge(World w, List<String> creatures){
        purge(null,w,creatures);
    }
    
    public void purge(CommandSender sender, World w, List<String> creatures){
        
        List<Entity> entities = w.getEntities();        
        int count = 0;
        
        for(Entity e: entities){
            if ((((e instanceof Creeper)) && (creatures.contains("CREEPER"))) || (((e instanceof Skeleton)) && (creatures.contains("SKELETON"))) || 
                    (((e instanceof Spider)) && (creatures.contains("SPIDER"))) || (((e instanceof Zombie)) && (creatures.contains("ZOMBIE"))) || 
                        (((e instanceof Ghast)) && (creatures.contains("GHAST"))) || (((e instanceof PigZombie)) && (creatures.contains("PIGZOMBIE"))) || 
                            (((e instanceof Giant)) && (creatures.contains("GIANT"))) || (((e instanceof Slime)) && (creatures.contains("SLIME"))) || 
                                (((e instanceof Chicken)) && (creatures.contains("CHICKEN"))) || (((e instanceof Cow)) && (creatures.contains("COW"))) || 
                                    (((e instanceof Sheep)) && (creatures.contains("SHEEP"))) || (((e instanceof Pig)) && (creatures.contains("PIG"))) || (
                                        ((e instanceof Squid)) && (creatures.contains("SQUID"))) || creatures.contains("*")) {
                e.remove();
                count++;
            }
        }
        
        if(sender!=null){
            sender.sendMessage(count + " Entities Purged from " + w.getName());
        }
    }
    
}
