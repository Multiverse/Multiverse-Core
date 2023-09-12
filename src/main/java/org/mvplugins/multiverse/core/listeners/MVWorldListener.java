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

import org.mvplugins.multiverse.core.inject.InjectableListener;
import org.mvplugins.multiverse.core.worldnew.WorldManager;
import org.mvplugins.multiverse.core.worldnew.options.UnloadWorldOptions;
import org.mvplugins.multiverse.core.worldnew.reasons.LoadFailureReason;
import org.mvplugins.multiverse.core.worldnew.reasons.UnloadFailureReason;

/**
 * Multiverse's World Listener.
 */
@Service
public class MVWorldListener implements InjectableListener {

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
    @EventHandler(priority = EventPriority.MONITOR)
    public void unloadWorld(WorldUnloadEvent event) {
        if (event.isCancelled()) {
            return;
        }
        worldManager.getLoadedWorld(event.getWorld().getName())
                .peek(world -> worldManager.unloadWorld(UnloadWorldOptions.world(world)).onFailure(failure -> {
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
    @EventHandler(priority = EventPriority.MONITOR)
    public void loadWorld(WorldLoadEvent event) {
        worldManager.getUnloadedWorld(event.getWorld().getName())
                .peek(world -> {
                    Logging.fine("Loading world: " + world.getName());
                    worldManager.loadWorld(world).onFailure(failure -> {
                        if (failure.getFailureReason() != LoadFailureReason.WORLD_ALREADY_LOADING) {
                            Logging.severe("Failed to load world: " + failure);
                        }
                    });
                });
    }
}