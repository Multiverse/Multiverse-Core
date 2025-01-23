/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.WorldPurger;

/**
 * Multiverse's Entity {@link Listener}.
 */
@Service
final class MVEntityListener implements CoreListener {
    private final WorldManager worldManager;
    private final WorldPurger worldPurger;

    @Inject
    MVEntityListener(
            @NotNull WorldManager worldManager,
            @NotNull WorldPurger worldPurger) {
        this.worldManager = worldManager;
        this.worldPurger = worldPurger;
    }

    /**
     * This method is called when an entity's food level goes higher or lower.
     *
     * @param event The Event that was fired.
     */
    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        worldManager.getLoadedWorld(player.getWorld())
                .peek(world -> {
                    if (!world.getHunger() && event.getFoodLevel() < player.getFoodLevel()) {
                        event.setCancelled(true);
                    }
                });
    }

    /**
     * This method is called when an entity's health goes up or down.
     *
     * @param event The Event that was fired.
     */
    @EventHandler
    public void entityRegainHealth(EntityRegainHealthEvent event) {
        if (event.isCancelled() || event.getRegainReason() != RegainReason.REGEN) {
            return;
        }

        worldManager.getLoadedWorld(event.getEntity().getWorld())
                .peek(world -> {
                    if (!world.getAutoHeal()) {
                        event.setCancelled(true);
                    }
                });
    }

    /**
     * Handle Animal/Monster Spawn settings, seems like a more concrete method than using CraftBukkit.
     *
     * @param event The event.
     */
    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }

        // Check to see if the Creature is spawned by a plugin, we don't want to prevent this behaviour.
        if (event.getSpawnReason() == SpawnReason.CUSTOM
                || event.getSpawnReason() == SpawnReason.SPAWNER_EGG
                || event.getSpawnReason() == SpawnReason.BREEDING) {
            return;
        }

        worldManager.getLoadedWorld(event.getEntity().getWorld())
                .peek(world -> {
                    if (this.worldPurger.shouldWeKillThisCreature(world, event.getEntity())) {
                        Logging.finer("Cancelling Creature Spawn Event for: " + event.getEntity());
                        event.setCancelled(true);
                    }
                });
    }
}
