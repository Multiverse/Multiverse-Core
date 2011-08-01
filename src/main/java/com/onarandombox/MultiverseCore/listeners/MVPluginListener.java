package com.onarandombox.MultiverseCore.listeners;

import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.fernferret.allpay.AllPay;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class MVPluginListener extends ServerListener {

    MultiverseCore plugin;

    public MVPluginListener(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Keep an eye out for Plugins which we can utilize.
     */
    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        /**
         * Check to see if Permissions was just enabled
         */
        if (event.getPlugin().getDescription().getName().equals("Permissions")) {
            this.plugin.getPermissions().setPermissions(((Permissions) this.plugin.getServer().getPluginManager().getPlugin("Permissions")).getHandler());
            this.plugin.log(Level.INFO, "- Attached to Permissions");
        }
        // Let AllPay handle all econ plugin loadings, only go for econ plugins we support
        if (Arrays.asList(AllPay.validEconPlugins).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.setBank(this.plugin.getBanker().loadEconPlugin());
        }
        if (event.getPlugin().getDescription().getName().equals("MultiVerse")) {
            if (event.getPlugin().isEnabled()) {
                this.plugin.getServer().getPluginManager().disablePlugin(event.getPlugin());
                this.plugin.log(Level.WARNING, "I just disabled the old version of Multiverse for you. You should remove the JAR now, your configs have been migrated.");
            }
        }
    }

    /**
     * We'll check if any of the plugins we rely on decide to Disable themselves.
     */
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        /**
         * Check to see if Permissions just disabled.
         */
        if (event.getPlugin().getDescription().getName().equals("Permissions")) {
            this.plugin.log(Level.INFO, "Permissions disabled");
            this.plugin.getPermissions().setPermissions(null);
        }
        // TODO: Disable econ when it disables.
    }

}
