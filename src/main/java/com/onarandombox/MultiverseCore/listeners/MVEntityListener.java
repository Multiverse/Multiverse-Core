/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.WorldPurger;
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.inject.InjectableListener;
import jakarta.inject.Inject;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

/**
 * Multiverse's Entity {@link Listener}.
 */
@Service
public class MVEntityListener implements InjectableListener {
    private final MVCoreConfig config;
    private final MVWorldManager worldManager;
    private final WorldPurger worldPurger;

    @Inject
    public MVEntityListener(
            @NotNull MVCoreConfig config,
            @NotNull MVWorldManager worldManager,
            @NotNull WorldPurger worldPurger
    ) {
        this.config = config;
        this.worldManager = worldManager;
        this.worldPurger = worldPurger;
    }

    /**
     * This method is called when an entity's food level goes higher or lower.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            MVWorld w = this.worldManager.getMVWorld(p.getWorld().getName());
            if (w != null && !w.getHunger()) {
                // If the world has hunger set to false, do not let the level go down
                if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * This method is called when an entity's health goes up or down.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void entityRegainHealth(EntityRegainHealthEvent event) {
        if (event.isCancelled()) {
            return;
        }
        RegainReason reason = event.getRegainReason();
        MVWorld world = this.worldManager.getMVWorld(event.getEntity().getLocation().getWorld());
        if (world != null && reason == RegainReason.REGEN && !world.getAutoHeal()) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle Animal/Monster Spawn settings, seems like a more concrete method than using CraftBukkit.
     * @param event The event.
     */
    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        // Check to see if the Creature is spawned by a plugin, we don't want to prevent this behaviour.
        // TODO: Allow the egg thing to be a config param. Doubt this will be per world; seems silly.
        if (event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.SPAWNER_EGG
                || event.getSpawnReason() == SpawnReason.BREEDING) {
            return;
        }

        World world = event.getEntity().getWorld();
        if (event.isCancelled())
            return;

        // Check if it's a world which we are meant to be managing.
        if (!(this.worldManager.isMVWorld(world.getName())))
            return;

        EntityType type = event.getEntityType();
        /**
         * Handle people with non-standard animals: ie a patched craftbukkit.
         */
        if (type == null || type.getName() == null) {
            Logging.finer("Found a null typed creature.");
            return;
        }

        MVWorld mvworld = this.worldManager.getMVWorld(world.getName());
        event.setCancelled(this.worldPurger.shouldWeKillThisCreature(mvworld, event.getEntity()));
    }

    /**
     * Handles portal search radius adjustment.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void entityPortal(EntityPortalEvent event) {
        if (event.isCancelled() || event.getTo() == null) {
            return;
        }
        if (!config.isUsingCustomPortalSearch()) {
            event.setSearchRadius(config.getCustomPortalSearchRadius());
        }
    }
}
