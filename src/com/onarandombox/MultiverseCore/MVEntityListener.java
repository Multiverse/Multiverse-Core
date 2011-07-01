package com.onarandombox.MultiverseCore;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

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
    
    @Override
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if(event.isCancelled()) {
            return;
        }
        RegainReason reason = event.getRegainReason();
        if(reason == RegainReason.REGEN && this.plugin.configMV.getBoolean("disableautoheal", false)) {
            event.setCancelled(true);
            return;
        }
    }

    /**
     * Handle Animal/Monster Spawn settings, seems like a more concrete method than using CraftBukkit.
     */
    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        // Check to see if the Creature is spawned by a plugin, we don't want to prevent this behaviour.
        if (event.getSpawnReason() == SpawnReason.CUSTOM) {
            return;
        }

        World world = event.getEntity().getWorld();
        if (event.isCancelled())
            return;

        // Check if it's a world which we are meant to be managing.
        if (!(this.plugin.isMVWorld(world.getName())))
            return;

        CreatureType creature = event.getCreatureType();

        MVWorld mvworld = this.plugin.getMVWorld(world.getName());

        /**
         * Animal Handling
         */
        if (event.getEntity() instanceof Animals) {
            event.setCancelled(this.shouldWeKillThisCreature(mvworld.getAnimalList(), mvworld.allowAnimalSpawning(), creature.toString().toUpperCase()));
        }
        /**
         * Monster Handling
         */
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Ghast || event.getEntity() instanceof Slime) {
            event.setCancelled(this.shouldWeKillThisCreature(mvworld.getMonsterList(), mvworld.allowMonsterSpawning(), creature.toString().toUpperCase()));
        }
    }

    private boolean shouldWeKillThisCreature(List<String> creatureList, boolean allowCreatureSpawning, String creature) {
        if (creatureList.isEmpty() && allowCreatureSpawning) {
            // 1. There are no exceptions and animals are allowd. Save it.
            return false;
        } else if (creatureList.isEmpty()) {
            // 2. There are no exceptions and animals are NOT allowed. Kill it.
            return true;
        } else if (creatureList.contains(creature) && allowCreatureSpawning) {
            // 3. There ARE exceptions and animals ARE allowed. Kill it.
            return true;
        } else if (!creatureList.contains(creature.toString().toUpperCase()) && allowCreatureSpawning) {
            // 4. There ARE exceptions and animals ARE NOT allowed. SAVE it.
            return false;
        } else if (creatureList.contains(creature.toString().toUpperCase()) && !allowCreatureSpawning) {
            // 5. No animals are allowed to be spawned, BUT this one can stay...
            return false;
        } else if (!creatureList.contains(creature.toString().toUpperCase()) && !allowCreatureSpawning) {
            // 6. Animals are NOT allowd to spawn, and this creature is not in the save list... KILL IT
            return true;
        }
        // This code should NEVER execute. I just left the verbose conditions in right now.
        return false;
    }

}
