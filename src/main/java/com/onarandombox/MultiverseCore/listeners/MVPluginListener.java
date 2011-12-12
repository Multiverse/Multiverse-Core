/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.fernferret.allpay.AllPay;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import java.util.Arrays;
import java.util.logging.Level;

public class MVPluginListener extends ServerListener {

    private MultiverseCore plugin;

    public MVPluginListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /** Keep an eye out for Plugins which we can utilize. */
    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        // Let AllPay handle all econ plugin loadings, only go for econ plugins we support
        if (Arrays.asList(AllPay.validEconPlugins).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.setBank(this.plugin.getBanker().loadEconPlugin());
        }

        if (event.getPlugin().getDescription().getName().equals("Spout")) {
            this.plugin.setSpout();
            this.plugin.log(Level.INFO, "Spout integration enabled.");
        }
    }

    /** We'll check if any of the plugins we rely on decide to Disable themselves. */
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        // TODO: Disable econ when it disables.
    }

}
