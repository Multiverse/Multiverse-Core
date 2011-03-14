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

    public void onPluginEnabled(PluginEvent event){
        if(event.getPlugin().getDescription().getName().equals("Permissions")) {
            MultiVerseCore.Permissions = Permissions.Security;
            MultiVerseCore.log.info(MultiVerseCore.logPrefix + "- Found Permissions/GroupManager");
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
    
}
