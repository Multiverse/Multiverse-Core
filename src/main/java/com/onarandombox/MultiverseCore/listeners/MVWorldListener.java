/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.inject.InjectableListener;
import jakarta.inject.Inject;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jvnet.hk2.annotations.Service;

/**
 * Multiverse's World Listener.
 */
@Service
public class MVWorldListener implements InjectableListener {

    private MVWorldManager worldManager;

    @Inject
    public MVWorldListener(MVWorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * This method is called when Bukkit fires off a WorldUnloadEvent.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void unloadWorld(WorldUnloadEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getWorld() instanceof World) {
            World world = (World) event.getWorld();
            if (world != null) {
                this.worldManager.unloadWorld(world.getName(), false);
            }
        }
    }

    /**
     * This method is called when Bukkit fires off a WorldLoadEvent.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void loadWorld(WorldLoadEvent event) {
        World world = event.getWorld();
        if (world != null) {
            if (this.worldManager.getUnloadedWorlds().contains(world.getName())) {
                this.worldManager.loadWorld(world.getName());
            }
            MVWorld mvWorld = worldManager.getMVWorld(world);
            if (mvWorld != null) {
                // This is where we can temporarily fix those pesky property issues!
                world.setPVP(mvWorld.isPVPEnabled());
                world.setDifficulty(mvWorld.getDifficulty());
            }
        }
    }
}
