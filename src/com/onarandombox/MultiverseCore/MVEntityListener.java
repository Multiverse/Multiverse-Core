package com.onarandombox.MultiverseCore;

import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
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
        
        System.out.print("A creature spawned: " + event.getEntity());
        System.out.print("Type: " + event.getCreatureType());
        System.out.print("Reason: " + event.getSpawnReason());
        System.out.print("Instanceof Animal: " + (event.getEntity() instanceof Animals));
        System.out.print("Instanceof Monster: " + (event.getEntity() instanceof Monster));
        System.out.print("Animal Allowed: " + mvworld.allowAnimalSpawning());
        System.out.print("Monster Allowed: " + mvworld.allowMonsterSpawning());
        System.out.print("Animal list: " + mvworld.getAnimalList());
        System.out.print("Monster list: " + mvworld.getMonsterList());
        
        // TODO: Look of this and see if there's a cleaner/better method of doing so...
        
        /**
         * Animal Handling
         */
        if (event.getEntity() instanceof Animals) {
            // If we have no exceptions for Animals then we just follow the Spawn setting.
            if (mvworld.getAnimalList().isEmpty() && mvworld.allowAnimalSpawning()) {
                System.out.print("1. There are no exceptions and animals are allowd.");
                return;
            } else if (mvworld.getAnimalList().isEmpty()) {
                System.out.print("2. There are no exceptions and animals are NOT allowed. Kill the " + creature.toString().toUpperCase());
                event.setCancelled(true);
                System.out.print("MV is killing a " + event.getCreatureType());
                return;
            } else if (mvworld.getAnimalList().contains(creature.toString().toUpperCase()) && mvworld.allowAnimalSpawning()) {
                System.out.print("3. There ARE exceptions and animals ARE allowed. Kill the " + creature.toString().toUpperCase());
                event.setCancelled(true);
                System.out.print("MV is killing a " + event.getCreatureType());
                return;
            } else if (!mvworld.getAnimalList().contains(creature.toString().toUpperCase()) && mvworld.allowAnimalSpawning()) {
                System.out.print("4. There ARE exceptions and animals ARE NOT allowed. SAVE the " + creature.toString().toUpperCase());
                return;
            } else if (mvworld.getAnimalList().contains(creature.toString().toUpperCase()) && !mvworld.allowAnimalSpawning()) {
                System.out.print("5. No animals are allowed to be spawned, BUT this one can stay... " + creature.toString().toUpperCase());
                
                return;
            } else if (!mvworld.getAnimalList().contains(creature.toString().toUpperCase()) && !mvworld.allowAnimalSpawning()) {
                System.out.print("6. Animals are NOT allowd to spawn, and this creature is not in the save list... KILL IT " + creature.toString().toUpperCase());
                event.setCancelled(true);
                System.out.print("MV is killing a " + event.getCreatureType());
                return;
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
                    System.out.print("MV is killing a " + event.getCreatureType());
                    return;
                }
            }
            // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
            if (mvworld.getMonsterList().contains(creature.toString().toUpperCase())) {
                if (mvworld.allowMonsterSpawning()) {
                    event.setCancelled(true);
                    System.out.print("MV is killing a " + event.getCreatureType());
                    return;
                } else {
                    return;
                }
            }
        }
    }
    
}
