/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Multiverse's Weather {@link Listener}.
 */
public class MVWeatherListener implements Listener {
    private MultiverseCore plugin;

    public MVWeatherListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * This method is called when the weather changes.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void weatherChange(WeatherChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(event.getWorld().getName());
        if (world != null) {
            // If it's going to start raining and we have weather disabled
            event.setCancelled((event.toWeatherState() && !world.isWeatherEnabled()));
        }
    }

    /**
     * This method is called when a big storm is going to start.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void thunderChange(ThunderChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(event.getWorld().getName());
        if (world != null) {
            // If it's going to start raining and we have weather disabled
            event.setCancelled((event.toThunderState() && !world.isWeatherEnabled()));
        }
    }
}
