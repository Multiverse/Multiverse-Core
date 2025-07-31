/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.dynamiclistener.annotations.DefaultEventPriority;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventMethod;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.options.LoadWorldOptions;
import org.mvplugins.multiverse.core.world.options.UnloadWorldOptions;
import org.mvplugins.multiverse.core.world.reasons.LoadFailureReason;
import org.mvplugins.multiverse.core.world.reasons.UnloadFailureReason;

/**
 * Multiverse's World Listener.
 */
@Service
final class MVWorldListener implements CoreListener {

    private final WorldManager worldManager;

    @Inject
    MVWorldListener(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * This method is called when Bukkit fires off a WorldUnloadEvent.
     *
     * @param event The Event that was fired.
     */
    @EventMethod
    @DefaultEventPriority(EventPriority.MONITOR)
    void worldUnload(WorldUnloadEvent event) {
        if (event.isCancelled()) {
            return;
        }
        worldManager.getLoadedWorld(event.getWorld().getName())
                .peek(world -> worldManager.unloadWorld(UnloadWorldOptions
                                .world(world)
                                .unloadBukkitWorld(false))
                        .onFailure(failure -> {
                            if (failure.getFailureReason() != UnloadFailureReason.WORLD_ALREADY_UNLOADING) {
                                Logging.severe("Failed to unload world: " + failure);
                            }
                        }));
    }

    /**
     * This method is called when Bukkit fires off a WorldLoadEvent.
     *
     * @param event The Event that was fired.
     */
    @EventMethod
    @DefaultEventPriority(EventPriority.MONITOR)
    void worldLoad(WorldLoadEvent event) {
        worldManager.getUnloadedWorld(event.getWorld().getName())
                .peek(world -> {
                    Logging.fine("Loading world: " + world.getName());
                    worldManager.loadWorld(LoadWorldOptions.world(world)).onFailure(failure -> {
                        if (failure.getFailureReason() != LoadFailureReason.WORLD_ALREADY_LOADING) {
                            Logging.severe("Failed to load world: " + failure);
                        }
                    });
                });
    }
}
