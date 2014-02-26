/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

/**
 * Multiverse's World {@link Listener}.
 */
public class MVWorldListener implements Listener {
    private MultiverseCore plugin;
    private MVWorldManager worldManager;

    public MVWorldListener(MultiverseCore plugin) {
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
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
                this.plugin.getMVWorldManager().unloadWorld(world.getName(), false);
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
            if (this.plugin.getMVWorldManager().getUnloadedWorlds().contains(world.getName())) {
                this.plugin.getMVWorldManager().loadWorld(world.getName());
            }
            MultiverseWorld mvWorld = plugin.getMVWorldManager().getMVWorld(world);
            if (mvWorld != null) {
                // This is where we can temporarily fix those pesky property issues!
                world.setPVP(mvWorld.isPVPEnabled());
                world.setDifficulty(mvWorld.getDifficulty());
            }
        }
    }
}
