/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import jakarta.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.jvnet.hk2.annotations.Service;

@Service
public class MVWorldInitListener implements Listener {

    MultiverseCore plugin;

    @Inject
    public MVWorldInitListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void initWorld(WorldInitEvent event) {
        if (!plugin.getMVWorldManager().isKeepingSpawnInMemory(event.getWorld())) {
            event.getWorld().setKeepSpawnInMemory(false);
        }
    }
}
