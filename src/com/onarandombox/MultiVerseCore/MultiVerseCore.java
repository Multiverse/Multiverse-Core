package com.onarandombox.MultiVerseCore;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;

//import com.nijikokun.bukkit.Permissions.Permissions;
//import com.nijiko.permissions.PermissionHandler;

import com.onarandombox.MultiVerseCore.commands.*;
import com.onarandombox.MultiVerseCore.configuration.defaultConfiguration;

@SuppressWarnings("unused")
public class MultiVerseCore extends JavaPlugin {
    // Setup our Map for our Commands using the CommandHandler.
    private Map<String, MVCommandHandler> commands = new HashMap<String, MVCommandHandler>();
    
    // Variable to state whether we are displaying Debug Messages or not.
    public static boolean debug = true;
    
    // Useless stuff to keep us going.
    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String logPrefix = "[MultiVerse-Core] ";
    public static Plugin instance;
    public static Server server;
    public static PluginDescriptionFile description;
    
    // Permissions Handler
    // public static PermissionHandler Permissions = null; // Scrapping Permissions till a stable release is out... this will be handled by isOP() for now.
    
    // Configurations
    public static Configuration configMV = null;
    public static Configuration configWorlds = null;
    
    // Setup the block/player/entity listener.
    private MVPlayerListener playerListener = new MVPlayerListener(this);;
    private MVBlockListener blockListener = new MVBlockListener(this);
    private MVEntityListener entityListener = new MVEntityListener(this);
    private MVPluginListener pluginListener = new MVPluginListener(this);
    private MVWorldListener worldListener = new MVWorldListener(this);
    
    // HashMap to contain all the Worlds which this Plugin will manage.
    public static HashMap<String,MVWorld> worlds = new HashMap<String,MVWorld>();
    
    // HashMap to contain information relating to the Players.
    public static HashMap<String, MVPlayerSession> playerSessions = new HashMap<String, MVPlayerSession>();
    
    /**
     * Constructor... Perform the Necessary tasks here.
     */
    public MultiVerseCore(){
        
    }
    
	/**
	 * What happens when the plugin gets around to be enabled...
	 */
    public void onEnable() {
        // Create the Plugin Data folder.
        this.getDataFolder().mkdir();

        // Output a little snippet to show it's enabled.
        log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");

        // Quick check for the Permissions Plugin, if we don't find it here then we'll check the plugin onEnable event.
        // TODO: Sort out Permissions Support...
        /*if(getServer().getPluginManager().getPlugin("Permissions")==null){
            log.info(logPrefix + "- Commands have been DISABLED until Permissions has been found.");
        } else {
            Permissions = com.nijikokun.bukkit.Permissions.Permissions.Security;
        }*/

        // Call the defaultConfiguration class to create the config files if they don't already exist.
        new defaultConfiguration(this.getDataFolder(), "config.yml");
        new defaultConfiguration(this.getDataFolder(), "worlds.yml");
        
        // Now grab the Configuration Files.
        configMV = new Configuration(new File(this.getDataFolder(), "config.yml"));
        configWorlds = new Configuration(new File(this.getDataFolder(), "worlds.yml"));
        
        // Now attempt to Load the configurations.
        try{ configMV.load(); } catch (Exception e){ log.info(MultiVerseCore.logPrefix + "- Failed to load config.yml"); }
        try{ configWorlds.load(); } catch (Exception e){ log.info(MultiVerseCore.logPrefix + "- Failed to load worlds.yml"); }
        
        // Setup all the Events the plugin needs to Monitor.
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Low, this); // Low so it acts above any other.
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.High, this); // To Add World Prefixing to Chat.
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this); // Respawn Players at the right point.

        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,Priority.Normal, this); // To remove Player Sessions

        // These 3 events should only be required in the Portals module.
        //pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener,Priority.Normal, this); // For Set Coord 1 & Info Wand etc...
        //pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener,Priority.Normal, this); // For Set Coord 2
        //pm.registerEvent(Event.Type.BLOCK_FLOW, blockListener, Priority.High,this); // To create Water/Lava Portals
        
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this); // To prevent Blocks being destroyed.
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.High, this); // To prevent Blocks being placed.
        
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.High, this); // To Allow/Disallow PVP.

        pm.registerEvent(Event.Type.WORLD_LOADED, worldListener, Priority.Highest, this); // Setup the Worlds config when a World is created.
        
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Normal, this); // Monitor for Permissions Plugin etc.
        
        // Call the Function to load all the Worlds and setup the HashMap
        loadWorlds();
        
        // Call the Function to assign all the Commands to their Class.
        setupCommands();
    }
    
    /**
     * 
     */
    private void setupCommands() {
        commands.put("mvcreate", new MVCreate(this));
        commands.put("mvimport", new MVImport(this));
        commands.put("mvremove", new MVRemove(this));
        commands.put("mvmodify", new MVModify(this));
        commands.put("mvtp", new MVTP(this));
        commands.put("mvlist", new MVList(this));
        commands.put("mvsetspawn", new MVSetSpawn(this));
        commands.put("mvspawn", new MVSpawn(this));
        commands.put("mvcoord", new MVCoord(this));
    }
    
    /**
     * Load the Worlds & Settings from the configuration file.
     */
	private void loadWorlds() {
	    // Basic Counter to count how many Worlds we are loading.
	    int count = 0; 
        
	    // Grab all the Worlds that already exist.
	    List<World> worlds = getServer().getWorlds();
	    
	    // You never know these days... bloody NPE's.
	    if(worlds != null && worlds.size()>0){
	        for (World world : worlds){
	            log.info(logPrefix + "Loading existing World - '" + world.getName() + "' - " + world.getEnvironment().toString()); // Output to the Log that wea re loading a world, specify the name and environment type.
                
	            MultiVerseCore.worlds.put(world.getName(), new MVWorld(world, false)); // Place the World into the HashMap. 
                count++; // Increment the World Count.
	        }
	    }
	    
	    log.info(logPrefix + count + " - existing World(s) found.");
	    
	    List<String> worldKeys = MultiVerseCore.configWorlds.getKeys("worlds"); // Grab all the Worlds from the Config.
	    count = 0;
	    if(worldKeys != null){
	        for (String worldKey : worldKeys){
	            // If this World already exists within the HashMap then we don't need to process it.
	            if(MultiVerseCore.worlds.containsKey(worldKey)){
	                continue;
	            }
	            
	            String wEnvironment = MultiVerseCore.configWorlds.getString("worlds." + worldKey + ".environment", "NORMAL"); // Grab the Environment as a String.
	            
	            Boolean monsters = MultiVerseCore.configWorlds.getBoolean("worlds." + worldKey + ".monsters", true); // Grab whether we want to spawn Monsters.
	            Boolean animals = MultiVerseCore.configWorlds.getBoolean("worlds." + worldKey + ".animals", true); // Grab whether we want to spawn Animals.
	         
	            Environment env;
	            if(wEnvironment.equalsIgnoreCase("NETHER")) // Check if the selected Environment is NETHER, otherwise we just default to NORMAL.
	                env = Environment.NETHER;
	            else
	                env = Environment.NORMAL;
	            
	            log.info(logPrefix + "Loading World & Settings - '" + worldKey + "' - " + wEnvironment); // Output to the Log that wea re loading a world, specify the name and environment type.
	            
	            World world = getServer().createWorld(worldKey, env);
	            
                // Beta 1.3 = 
                // D = Monsters
                // E = Animals
                ((CraftWorld) world).getHandle().D = monsters;
                ((CraftWorld) world).getHandle().E = animals;

                //((CraftWorld) world).getHandle().q.a(i, j, k);
                //Spawn Crap

                // The following will be used once they accept the pull request.
	            //world.setMonsterSpawn = monsters;
	            //world.setAnimalSpawn = animals;
                
                //MultiVerseCore.worlds.put(worldKey, new MVWorld(world, MultiVerseCore.configWorlds, this)); // Place the World into the HashMap. 
                
                count++; // Increment the World Count.
	        }
	    }
	    log.info(logPrefix + count + " - World(s) loaded.");
	    
    }
	
    /**
	 * What happens when the plugin gets disabled...
	 */
	public void onDisable() {
       log.info(logPrefix + "- Disabled");
	}
	
    /**
     * Grab the players session if one exists, otherwise create a session then return it.
     * @param player
     * @return
     */
    public MVPlayerSession getPlayerSession(Player player){
        if(playerSessions.containsKey(player.getName())){
            return playerSessions.get(player.getName());
        } else {
            playerSessions.put(player.getName(), new MVPlayerSession(player, MultiVerseCore.configMV, this));
            return playerSessions.get(player.getName());
        }
    }
	
	/**
	 * onCommand
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	    /*if(Permissions==null || this.isEnabled()==false){
	        return false;
	    }*/
	    if(this.isEnabled() == false){
	        sender.sendMessage("This plugin is Disabled!");
	        return true;
	    }
	    
        MVCommandHandler handler = commands.get(command.getName().toLowerCase());
	        
        if (handler != null) {
            return handler.perform(sender, args);
        } else {
            return false;
        }
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