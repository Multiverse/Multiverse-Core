/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class MVWeatherListener extends WeatherListener {
    private MultiverseCore plugin;

    public MVWeatherListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onWeatherChange(WeatherChangeEvent event) {

        MVWorld world = this.plugin.getMVWorldManager().getMVWorld(event.getWorld().getName());
        if (world != null) {
            // If it's going to start raining and we have weather disabled
            event.setCancelled((event.toWeatherState() && !world.getWeatherEnabled()));
        }
    }

    @Override
    public void onThunderChange(ThunderChangeEvent event) {

        MVWorld world = this.plugin.getMVWorldManager().getMVWorld(event.getWorld().getName());
        if (world != null) {
            // If it's going to start raining and we have weather disabled
            event.setCancelled((event.toThunderState() && !world.getWeatherEnabled()));
        }
    }
}
