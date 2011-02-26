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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.iConomy.iConomy;
import com.onarandombox.MultiVerseCore.configuration.defaultConfiguration;

@SuppressWarnings("unused")
public class MultiVerseCore extends JavaPlugin {
    // Variable to state whether we are displaying Debug Messages or not.
    public boolean debug = true;
    
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
    private MVPlayerListener playerListener = new MVPlayerListener(this);;
    private MVBlockListener blockListener = new MVBlockListener(this);
    private MVEntityListener entityListener = new MVEntityListener(this);
    private MVPluginListener pluginListener = new MVPluginListener(this);
    
    // HashMap to contain all the Worlds which this Plugin will manage.
    public HashMap<String,MVWorld> mvWorlds = new HashMap<String,MVWorld>();
    // HashMap to contain information relating to the Players.
    public HashMap<String, MVPlayerSession> playerSessions = new HashMap<String, MVPlayerSession>();
    
    /**
     * Constructor... Perform the Necessary tasks here.
     */
    public MultiVerseCore(){
        
    }
    
	/**
	 * What happens when the plugin gets around to be enabled...
	 */
    public void onEnable() {
        this.getDataFolder().mkdir();
        /**
         * Output a little snippet to state that the plugin is now enabled.
         */
        log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");
        /**
         * Quick check for the Permissions Plugin, if we don't find it here
         * we'll have to check Plugin onEnable Events.
         */
        if(getServer().getPluginManager().getPlugin("Permissions")==null){
            log.info(logPrefix + "Commands have been DISABLED until Permissions has been found.");
        } else {
            Permissions = com.nijikokun.bukkit.Permissions.Permissions.Security;
        }
        /**
         * If the Configuration Files don't exist then create them.
         */
        if(!(new File(this.getDataFolder(), "MultiVerse.yml").exists())){
            new defaultConfiguration().setupMultiVerseConfig(new File(this.getDataFolder(), "MultiVerse.yml"));
        }
        if(!(new File(this.getDataFolder(), "Worlds.yml").exists())){
            new defaultConfiguration().setupMultiVerseConfig(new File(this.getDataFolder(), "Worlds.yml"));
        }
        /**
         * Grab the Configuration Files & Load them.
         */
        configMV = new Configuration(new File(this.getDataFolder(), "MultiVerse.yml"));
        configMV.load();
        configWorlds = new Configuration(new File(this.getDataFolder(), "Worlds.yml"));
        configWorlds.load();
        /**
         * Setup all the events which we will be listening.
         */
        /*
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Low,this); // Low so it acts above any other.
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,Priority.Normal, this);
        
        pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener,Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener,Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_FLOW, blockListener, Priority.High,this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.High,this);
        
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.High,this);
        */
        /**
         * In case of a /reload we need to make sure every player online gets
         * setup with a player session.
         */
        Player[] p = this.getServer().getOnlinePlayers();
        for (int i = 0; i < p.length; i++) {
            debugMsg("Player Sessions - Player " + i + " Out of " + p.length + " Name - " + p[i].getName(), null);
            this.playerSessions.put(p[i].getName(), new MVPlayerSession(p[i],this));
        }
        /**
         * Load up the Worlds & their Settings.
         */
        loadWorlds();
    }
    
    /**
     * Load the Worlds & Settings from the configuration file.
     */
	private void loadWorlds() {
        // TODO Auto-generated method stub
        
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
	    if(Permissions==null || this.isEnabled()==false){
	        return false;
	    }
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