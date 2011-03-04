package com.onarandombox.MultiVerseCore;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimedEvent;

public class MVEntityListener extends EntityListener {

    MultiVerseCore plugin;
    
    public MVEntityListener(MultiVerseCore plugin) {
        this.plugin = plugin;
    }

    // Need to find a way to stop the Ghast Fireballs damaging 
    // surroundings but still doing damage to players.
    public void onEntityExplode(EntityExplodeEvent event){

    }
    public void onExplosionPrimed(ExplosionPrimedEvent event){
        if(event.getEntity() instanceof Fireball){
            MultiVerseCore.log.info("Fireball"); 
            // Fireballs on Explode trigger this, sadly we can't get the blocks it would destroy... thats onEntityExplode
            // However can't figure out a way to check in onEntityExplode if it was a Fireball which caused it...
        }
    }
    
    /**
     * Handle Animal/Monster Spawn settings, seems like a more concrete method than using CraftBukkit.
     */
    public void onCreatureSpawn(CreatureSpawnEvent event){
        World world = event.getEntity().getWorld();
        if(event.isCancelled()) return;
        if(!(plugin.worlds.containsKey(world.getName()))) return; // Check if it's a world which we are meant to be managing.
        
        MVWorld mvworld = plugin.worlds.get(world.getName());

        // TODO: Look of this and see if there's a cleaner/better method of doing so...
        
        /**
         * Animal Handling
         */
        if(event.getEntity() instanceof Animals){
            // If we have no exceptions for Animals then we just follow the Spawn setting.
            if(mvworld.animalList.size()<=0){
                if(mvworld.animals){
                    return;
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
            if(mvworld.animalList.contains(event.getMobType().toString().toUpperCase())){
                if(mvworld.animals){
                    event.setCancelled(true);
                    return;
                } else {
                    return;
                }
            }
        }
        /**
         * Monster Handling
         */
        if(event.getEntity() instanceof Monster){
            // If we have no exceptions for Monsters then we just follow the Spawn setting.
            if(mvworld.monsterList.size()<=0){
                if(mvworld.monsters){
                    return;
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
            if(mvworld.monsterList.contains(event.getMobType().toString().toUpperCase())){
                if(mvworld.monsters){
                    event.setCancelled(true);
                    return;
                } else {
                    return;
                }
            }
        }
        
        /**
         * Ghast Handling -- This should only be temporary, noticed a bug where Ghasts would keep spawning and flood the Nether.
         * However not sure about it... not sure of the effect on performance... got a few 'server overloaded' warnings through testing but not sure of the cause.
         */
        if(event.getEntity() instanceof Ghast){
            List<Entity> entities = world.getEntities();
            int count = 0;
            for(Entity entity : entities){
                if(entity instanceof Ghast){
                    if(count>=MultiVerseCore.configMV.getInt("ghastlimit", 50)){
                        event.setCancelled(true);
                        return;
                    }
                    count++;
                }
            }
        }
    }
    
}