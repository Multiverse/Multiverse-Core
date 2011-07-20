package com.onarandombox.MultiverseCore;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

//import org.bukkit.event.entity.ExplosionPrimedEvent;

public class MVEntityListener extends EntityListener {

    MultiverseCore plugin;

    public MVEntityListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Event - When a Entity is Damaged, we first sort out whether it is of
     * importance to us, such as EntityVSEntity or EntityVSProjectile. Then we
     * grab the attacked and defender and check if its a player. Then deal with
     * the PVP Aspect.
     */
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity attacker = null;
        Entity defender = null;
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
            attacker = sub.getDamager();
            defender = sub.getEntity();
        } else if (event instanceof EntityDamageByProjectileEvent) {
            EntityDamageByProjectileEvent sub = (EntityDamageByProjectileEvent) event;
            attacker = sub.getDamager();
            defender = sub.getEntity();
        } else {
            return;
        }
        if (attacker == null || defender == null) {
            return;
        }
        if (defender instanceof Player) {
            Player player = (Player) defender;
            World w = player.getWorld();

            if (!this.plugin.isMVWorld(w.getName())) {
                //if the world is not handled, we don't care
                return;
            }
            MVWorld world = this.plugin.getMVWorld(w.getName());

            if (attacker != null && attacker instanceof Player) {
                Player pattacker = (Player) attacker;


                if (!world.getPvp() && this.plugin.getConfig().getBoolean("fakepvp", false)) {
                    pattacker.sendMessage(ChatColor.RED + "PVP is disabled in this World.");
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.isCancelled()) {
            return;
        }
        RegainReason reason = event.getRegainReason();
        if (reason == RegainReason.REGEN && this.plugin.getConfig().getBoolean("disableautoheal", false)) {
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
