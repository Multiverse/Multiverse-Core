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
        if(event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
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
        if (!(plugin.worlds.containsKey(world.getName())))
            return; // Check if it's a world which we are meant to be managing.

        CreatureType creature = event.getCreatureType();

        // event.getEntity().getWorld().spawnCreature(arg0, arg1);

        MVWorld mvworld = plugin.worlds.get(world.getName());

        // TODO: Look of this and see if there's a cleaner/better method of doing so...

        /**
         * Animal Handling
         */
        if (event.getEntity() instanceof Animals) {
            // If we have no exceptions for Animals then we just follow the Spawn setting.
            if (mvworld.animalList.size() <= 0) {
                if (mvworld.animals) {
                    return;
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
            if (mvworld.animalList.contains(creature.toString())) {
                if (mvworld.animals) {
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
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Ghast || event.getEntity() instanceof PigZombie || event.getEntity() instanceof Slime) {
            // If we have no exceptions for Monsters then we just follow the Spawn setting.
            if (mvworld.monsterList.size() <= 0) {
                if (mvworld.monsters) {
                    return;
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
            if (mvworld.monsterList.contains(creature.toString())) {
                if (mvworld.monsters) {
                    event.setCancelled(true);
                    return;
                } else {
                    return;
                }
            }
        }
    }

}
