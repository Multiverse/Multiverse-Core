package com.onarandombox.MultiverseCore;

import java.util.logging.Level;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;

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
            this.plugin.ph.setPermissions(((Permissions) this.plugin.getServer().getPluginManager().getPlugin("Permissions")).getHandler());
            this.plugin.log(Level.INFO, "- Attached to Permissions");
        }
        // TODO: Use AllPay
        /**
         * Use the METHOD supplied by iConomy to register it etc...
         */
        if(event.getPlugin().getDescription().getName().equals("iConomy")) {
            //Plugin iConomy = this.plugin.getServer().getPluginManager().getPlugin("iConomy");

//            if (iConomy != null) {
//                if (iConomy.isEnabled()) {
//                    MultiverseCore.iConomy = (iConomy) iConomy;
//                }
//            }
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
            this.plugin.ph.setPermissions(null);
        }

        /**
         * Check to see if iConomy just disabled.
         */
//        if (MultiverseCore.getiConomy() != null) {
//            MultiverseCore.iConomy = null;
//        }
    }

}
