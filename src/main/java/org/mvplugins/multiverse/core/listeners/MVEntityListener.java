/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.world.WorldManager;

/**
 * Multiverse's Entity {@link Listener}.
 */
@Service
final class MVEntityListener implements CoreListener {
    private final WorldManager worldManager;

    @Inject
    MVEntityListener(@NotNull WorldManager worldManager) {
        this.worldManager = worldManager;
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
                    if (!world.isHunger() && event.getFoodLevel() < player.getFoodLevel()) {
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
     * Handle Spawn Category settings.
     *
     * @param event The event.
     */
    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }

        // Always allow custom command and plugins to spawn creatures
        if (event.getSpawnReason() == SpawnReason.CUSTOM
                || event.getSpawnReason() == SpawnReason.COMMAND
                || event.getSpawnReason() == SpawnReason.BREEDING) {
            return;
        }

        worldManager.getLoadedWorld(event.getEntity().getWorld())
                .peek(world -> {
                    if (!world.getEntitySpawnConfig().shouldAllowSpawn(event.getEntity())) {
                        Logging.finest("Cancelling Creature Spawn Event for: " + event.getEntity());
                        event.setCancelled(true);
                    }
                });
    }

    /**
     * Handle Spawn Category settings for non-creature entities.
     */
    @EventHandler
    public void entitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            // Handled by CreatureSpawnEvent
            return;
        }

        worldManager.getLoadedWorld(event.getEntity().getWorld())
                .peek(world -> {
                    if (!world.getEntitySpawnConfig().shouldAllowSpawn(event.getEntity())) {
                        Logging.finest("Cancelling Entity Spawn Event for: " + event.getEntity());
                        event.setCancelled(true);
                    }
                });
    }
}
