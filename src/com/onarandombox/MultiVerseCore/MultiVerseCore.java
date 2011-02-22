package com.onarandombox.MultiVerseCore;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.iConomy.iConomy;

@SuppressWarnings("unused")
public class MultiVerseCore extends JavaPlugin {
    /**
     * Variable for whether we are going to allow Debug Messages or not.
     */
    public boolean debug = false;
    /**
     * Variable to hold the Server Instance.
     */
    public static Server server;
    /**
     * Permissions Plugin
     */
    public static PermissionHandler Permissions = null;
    /**
     * Setup the Logger, also set a public variable which contains the prefix
     * for all log messages, this allows for easy change.
     */
    private final Logger log = Logger.getLogger("Minecraft");
    public final String logPrefix = "[MultiVerse-Core] ";
    /**
     * Variable to hold the Plugin instance.
     */
    public static Plugin instance;
    /**
     * Variable to hold the Plugins Description
     */
    public static PluginDescriptionFile description;
	/**
	 * What happens when the plugin gets around to be enabled...
	 */
	@Override
    public void onEnable() {
        this.getDataFolder().mkdir();
        /**
         * Output a little snippet to state that the plugin is now enabled.
         */
        log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");
    }
	/**
	 * What happens when the plugin gets disabled...
	 */
	@Override
	public void onDisable() {
       log.info(logPrefix + "- Disabled");
	}
	/**
	 * Basic Debug Output function, if we've enabled debugging we'll output more information.
	 */
	public void debugMsg(String msg, Player p){
	    if(this.debug){
	        log.info(msg);
	        if(p!=null){
	            p.sendMessage(msg);
	        }
	    }
	}
}