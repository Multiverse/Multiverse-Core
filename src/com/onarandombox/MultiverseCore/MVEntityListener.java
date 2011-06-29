package com.onarandombox.MultiverseCore;

import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

//import org.bukkit.event.entity.ExplosionPrimedEvent;

public class MVEntityListener extends EntityListener {
    
    MultiverseCore plugin;
    
    public MVEntityListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }
    
    // Need to find a way to stop the Ghast Fireballs damaging
    // surroundings but still doing damage to players.
    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
        
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            p.sendMessage("You died!");
        }
        super.onEntityDeath(event);
    }
    
    /**
     * Handle Animal/Monster Spawn settings, seems like a more concrete method than using CraftBukkit.
     */
    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World world = event.getEntity().getWorld();
        if (event.isCancelled())
            return;
        
        // Check if it's a world which we are meant to be managing.
        if (!(this.plugin.isMVWorld(world.getName())))
            return;
        
        CreatureType creature = event.getCreatureType();
        
        MVWorld mvworld = this.plugin.getMVWorld(world.getName());
        
        // TODO: Look of this and see if there's a cleaner/better method of doing so...
        
        /**
         * Animal Handling
         */
        if (event.getEntity() instanceof Animals) {
            // If we have no exceptions for Animals then we just follow the Spawn setting.
            if (mvworld.getAnimalList().isEmpty()) {
                if (mvworld.allowAnimalSpawning()) {
                    return;
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
            if (mvworld.getAnimalList().contains(creature.toString().toUpperCase())) {
                if (mvworld.allowAnimalSpawning()) {
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
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Ghast || event.getEntity() instanceof Slime) {
            // If we have no exceptions for Monsters then we just follow the Spawn setting.
            if (mvworld.getMonsterList().isEmpty()) {
                if (mvworld.allowMonsterSpawning()) {
                    return;
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
            if (mvworld.getMonsterList().contains(creature.toString().toUpperCase())) {
                if (mvworld.allowMonsterSpawning()) {
                    event.setCancelled(true);
                    return;
                } else {
                    return;
                }
            }
        }
    }
    
}
