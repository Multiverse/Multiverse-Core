package com.onarandombox.MultiVerseCore;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
    // Variable to state whether we are displaying Debug Messages or not.
    public boolean debug = false;
    
    // Useless stuff to keep us going.
    private final Logger log = Logger.getLogger("Minecraft");
    public final String logPrefix = "[MultiVerse-Core] ";
    public static Plugin instance;
    public static Server server;
    public static PluginDescriptionFile description;
    
    // Permissions Handler
    public static PermissionHandler Permissions = null;
    
    // Configurations
    public static Configuration configMV;
    public static Configuration configWorlds;
    
    // Setup the block/player/entity listener.
    private MVPlayerListener playerListener;
    private MVBlockListener blockListener;
    private MVEntityListener entityListener;
    private MVPluginListener pluginListener;
    
    // HashMap to contain all the Worlds which this Plugin will manage.
    public HashMap<String,MVWorld> mvWorlds = new HashMap<String,MVWorld>();
    
	/**
	 * What happens when the plugin gets around to be enabled...
	 */
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
	public void onDisable() {
       log.info(logPrefix + "- Disabled");
	}
	/**
	 * onCommand
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        return false;
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