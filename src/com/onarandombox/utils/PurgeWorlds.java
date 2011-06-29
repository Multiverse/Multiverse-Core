package com.onarandombox.utils;

import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Zombie;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class PurgeWorlds {
    
    MultiverseCore plugin;
    
    public PurgeWorlds(MultiverseCore plugin) {
        this.plugin = plugin;
    }
    
    public void purge(World w, List<String> creatures) {
        purge(null, w, creatures);
    }
    
    public void purge(CommandSender sender, World w, List<String> creatures) {
        
        List<Entity> entities = w.getEntities();
        int count = 0;
        
        for (Entity e : entities) {
            if ((((e instanceof Creeper)) && (creatures.contains("CREEPER"))) || (((e instanceof Skeleton)) && (creatures.contains("SKELETON"))) || (((e instanceof Spider)) && (creatures.contains("SPIDER")))
                    || (((e instanceof Zombie)) && (creatures.contains("ZOMBIE"))) || (((e instanceof Ghast)) && (creatures.contains("GHAST"))) || (((e instanceof PigZombie)) && (creatures.contains("PIGZOMBIE")))
                    || (((e instanceof Giant)) && (creatures.contains("GIANT"))) || (((e instanceof Slime)) && (creatures.contains("SLIME"))) || (((e instanceof Chicken)) && (creatures.contains("CHICKEN")))
                    || (((e instanceof Cow)) && (creatures.contains("COW"))) || (((e instanceof Sheep)) && (creatures.contains("SHEEP"))) || (((e instanceof Pig)) && (creatures.contains("PIG")))
                    || (((e instanceof Squid)) && (creatures.contains("SQUID"))) || creatures.contains("*")) {
                e.remove();
                count++;
            }
        }
        
        if (sender != null) {
            sender.sendMessage(count + " Entities Purged from " + w.getName());
        }
    }
    
    /**
     * Synchronizes the given world with it's settings
     */
    public void purgeWorlds(MVWorld world) {
        
    }
    
    public void purgeWorlds(CommandSender sender, List<MVWorld> worlds, List<String> whatToKill) {
        if (worlds.isEmpty())
            return;
        
        for (MVWorld mvworld : worlds) {
            World world = this.plugin.getServer().getWorld(mvworld.getName());
            if (world == null)
                continue;
            List<String> monsters = mvworld.getMonsterList();
            List<String> animals = mvworld.getAnimalList();
            System.out.print("Monster Size:" + monsters.size() + " - " + "Animal Size: " + animals.size());
            for (Entity e : world.getEntities()) {
                String creatureName = e.toString().replaceAll("Craft", "").toLowerCase();
                // Check against Monsters
                killMonster(mvworld, e, creatureName);
                // Check against Animals
                killCreature(mvworld, e, creatureName);
            }
        }
    }
    
    private boolean killCreature(MVWorld mvworld, Entity e, String creatureName) {
        String entityName = e.toString().replaceAll("Craft", "").toUpperCase();
        if (e instanceof Squid || e instanceof Animals) {
            if (entityName.contains(creatureName.toUpperCase())) {
                System.out.print(creatureName + " - Removed");
                e.remove();
                return true;
            }
        }
        return false;
    }
    
    private boolean killMonster(MVWorld mvworld, Entity e, String creatureName) {
        String entityName = e.toString().replaceAll("Craft", "").toUpperCase();
        if (e instanceof Slime || e instanceof Monster) {
            if (entityName.contains(creatureName.toUpperCase())) {
                System.out.print(creatureName + " - Removed");
                e.remove();
                return true;
            }
        }
        return false;
    }
    
}
