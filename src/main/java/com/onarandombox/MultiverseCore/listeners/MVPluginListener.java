/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.fernferret.allpay.AllPay;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.Arrays;

/**
 * Multiverse's Plugin {@link Listener}.
 */
public class MVPluginListener implements Listener {

    private MultiverseCore plugin;

    public MVPluginListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Keep an eye out for Plugins which we can utilize.
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void pluginEnable(PluginEnableEvent event) {
        // Let AllPay handle all econ plugin loadings, only go for econ plugins we support
        if (Arrays.asList(AllPay.getValidEconPlugins()).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.setBank(this.plugin.getBanker().loadEconPlugin());
        }

        /*
        if (event.getPlugin().getDescription().getName().equals("Spout")) {
            this.plugin.setSpout();
            this.plugin.log(Level.INFO, "Spout integration enabled.");
        }
        */
    }

    /**
     * We'll check if any of the plugins we rely on decide to Disable themselves.
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void pluginDisable(PluginDisableEvent event) {
        // TODO: Disable econ when it disables.
    }

}
