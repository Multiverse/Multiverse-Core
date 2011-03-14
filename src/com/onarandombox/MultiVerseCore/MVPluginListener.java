package com.onarandombox.MultiVerseCore;

import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MVPluginListener extends ServerListener {

    MultiVerseCore plugin;
    
    public MVPluginListener(MultiVerseCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Keep an eye out for Plugins which we can utilize.
     */
    public void onPluginEnabled(PluginEvent event){
        
        /**
         * Check to see if Permissions was just enabled, we only wan't to perform the following if GroupManager is not found.
         */
        if(event.getPlugin().getDescription().getName().equals("Permissions")) {
            MultiVerseCore.Permissions = ((Permissions) plugin.getServer().getPluginManager().getPlugin("Permissions")).getHandler();
            MultiVerseCore.log.info(MultiVerseCore.logPrefix + "- Found Permissions");
        }
        
        /**
         * Use the METHOD supplied by iConomy to register it etc...
         */
        if(MultiVerseCore.getiConomy() == null) {
            Plugin iConomy = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if(iConomy.isEnabled()) {
                    MultiVerseCore.iConomy = (iConomy) iConomy;
                    MultiVerseCore.log.info(MultiVerseCore.logPrefix + "- Found iConomy");
                }
            }
        }
    }
    
    /**
     * We'll check if any of the plugins we rely on decide to Disable themselves.
     */
    public void onPluginDisabled(PluginEvent event){
        /**
         * Check to see if Permissions just disabled.
         */
        if(event.getPlugin().getDescription().getName().equals("Permissions")) {
            MultiVerseCore.Permissions = null;
            MultiVerseCore.log.info(MultiVerseCore.logPrefix + "- Permissions has been Disabled");
        }
        
        /**
         * Check to see if iConomy just disabled.
         */
        if(MultiVerseCore.getiConomy() != null) {
            MultiVerseCore.iConomy = null;
            MultiVerseCore.log.info(MultiVerseCore.logPrefix + "- iConom has been Disabled");
        }
    }
    
}
