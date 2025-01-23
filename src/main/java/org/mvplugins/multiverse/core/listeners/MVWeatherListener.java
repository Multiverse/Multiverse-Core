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
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.world.WorldManager;

/**
 * Multiverse's Weather Listener.
 */
@Service
final class MVWeatherListener implements CoreListener {

    private final WorldManager worldManager;

    @Inject
    MVWeatherListener(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * This method is called when the weather changes.
     *
     * @param event The Event that was fired.
     */
    @EventHandler
    public void weatherChange(WeatherChangeEvent event) {
        if (event.isCancelled() || !event.toWeatherState()) {
            return;
        }
        worldManager.getLoadedWorld(event.getWorld())
                .peek(world -> {
                    if (!world.getAllowWeather()) {
                        Logging.fine("Cancelling weather for %s as getAllowWeather is false", world.getName());
                        event.setCancelled(true);
                    }
                });
    }

    /**
     * This method is called when a big storm is going to start.
     *
     * @param event The Event that was fired.
     */
    @EventHandler
    public void thunderChange(ThunderChangeEvent event) {
        if (event.isCancelled() || !event.toThunderState()) {
            return;
        }
        worldManager.getLoadedWorld(event.getWorld())
                .peek(world -> {
                    if (!world.getAllowWeather()) {
                        Logging.fine("Cancelling thunder for %s as getAllowWeather is false", world.getName());
                        event.setCancelled(true);
                    }
                });
    }
}
