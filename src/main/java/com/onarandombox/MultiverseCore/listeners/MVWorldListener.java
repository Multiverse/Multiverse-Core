/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.inject.InjectableListener;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import jakarta.inject.Inject;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jvnet.hk2.annotations.Service;

/**
 * Multiverse's World Listener.
 */
@Service
public class MVWorldListener implements InjectableListener {

    private final WorldManager worldManager;

    @Inject
    public MVWorldListener(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * This method is called when Bukkit fires off a WorldUnloadEvent.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void unloadWorld(WorldUnloadEvent event) {
        if (event.isCancelled()) {
            return;
        }
        this.worldManager.unloadWorld(event.getWorld());
    }

    /**
     * This method is called when Bukkit fires off a WorldLoadEvent.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void loadWorld(WorldLoadEvent event) {
        worldManager.getOfflineOnlyWorld(event.getWorld().getName())
                .peek(offlineWorld -> {
                    Logging.fine("Loading world: " + offlineWorld.getName());
                    worldManager.loadWorld(offlineWorld);
                });
    }
}
