package com.onarandombox.MultiVerseCore;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;

import org.anjocaido.groupmanager.GroupManager;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import com.nijiko.coelho.iConomy.iConomy;

import com.onarandombox.MultiVerseCore.commands.*;
import com.onarandombox.MultiVerseCore.configuration.DefaultConfiguration;
import com.onarandombox.utils.UpdateChecker;

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
    
    // Setup a variable to hold our DataFolder which will house everything to do with MultiVerse
    // Using this instead of getDataFolder(), allows all modules to use the same direectory.
    public static final File dataFolder = new File("plugins" + File.separator + "MultiVerse");
    
    // MultiVerse Permissions Handler
    public static MVPermissions ph;
    
    // Permissions Handler
    public static PermissionHandler Permissions = null;
    
    // GroupManager Permissions Handler
    public static GroupManager GroupManager = null;
    
    // iConomy Handler
    public static iConomy iConomy = null;
    public static boolean useiConomy = false;
    
    // Configurations
    public static Configuration configMV = null;
    public static Configuration configWorlds = null;
    
    // Setup the block/player/entity listener.
    private MVPlayerListener playerListener = new MVPlayerListener(this);;
    private MVBlockListener blockListener = new MVBlockListener(this);
    private MVEntityListener entityListener = new MVEntityListener(this);
    private MVPluginListener pluginListener = new MVPluginListener(this);
    
    public UpdateChecker updateCheck;
    
    // HashMap to contain all the Worlds which this Plugin will manage.
    public HashMap<String,MVWorld> worlds = new HashMap<String,MVWorld>();
    
    // HashMap to contain information relating to the Players.
    public HashMap<String, MVPlayerSession> playerSessions = new HashMap<String, MVPlayerSession>();
    
    /**
     * Constructor... Perform the Necessary tasks here.
     */
    public MultiVerseCore(){
        
    }
    
	/**
	 * What happens when the plugin gets around to being enabled...
	 */
    public void onEnable() {
        // Create the Plugin Data folder.
        dataFolder.mkdir();

        // Output a little snippet to show it's enabled.
        log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled - By " + getAuthors());
        
        // Setup & Load our Configuration files.
        loadConfigs();
        
        // Setup all the Events the plugin needs to Monitor.
        registerEvents();
        // Call the Function to load all the Worlds and setup the HashMap
        loadWorlds();
        // Purge Worlds of old Monsters/Animals which don't adhere to the setup.
        purgeWorlds();
        // Setup Group Manager.
        setupGroupManager();
        // Setup Permissions, we'll do an initial check for the Permissions plugin then fall back on isOP().
        setupPermissions();
        // Setup iConomy.
        setupiConomy();
        // Call the Function to assign all the Commands to their Class.
        setupCommands();
        
        // Start the Update Checker
        updateCheck = new UpdateChecker(this.getDescription().getName(),this.getDescription().getVersion());
    }
    
    /**
     * Function to Register all the Events needed.
     */
    private void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Highest, this); // Low so it acts above any other.
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Highest, this); // Cancel Teleports if needed.
        
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,Priority.Normal, this); // To remove Player Sessions

        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this); // To prevent Blocks being destroyed.
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this); // To prevent Blocks being placed.
        
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this); // To Allow/Disallow PVP as well as EnableHealth.
        
        pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this); // To prevent all or certain animals/monsters from spawning.

        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this); // Try to prevent Ghasts from blowing up structures.
        pm.registerEvent(Event.Type.EXPLOSION_PRIMED, entityListener, Priority.Normal, this); // Try to prevent Ghasts from blowing up structures.
        
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this); // Monitor for Permissions Plugin etc.
    }

    /**
     * Check to see if GroupManager is enabled and setup accordingly.
     */
    private void setupGroupManager(){
        Plugin p = this.getServer().getPluginManager().getPlugin("GroupManager");
        if (p != null) {
            if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
                this.getServer().getPluginManager().enablePlugin(p);
            }
            MultiVerseCore.GroupManager = (GroupManager) p;
        }
    }
    
    /**
     * Check for Permissions plugin and then setup our own Permissions Handler.
     */
    private void setupPermissions() {
        Plugin p = this.getServer().getPluginManager().getPlugin("Permissions");

        if (MultiVerseCore.Permissions == null) {
            if (p != null) {
                MultiVerseCore.Permissions = ((Permissions)p).getHandler();
            }
        }
    }
    
    /**
     * Check for the iConomy plugin and set it up accordingly.
     */
    private void setupiConomy() {
        Plugin test = this.getServer().getPluginManager().getPlugin("iConomy");

        if (MultiVerseCore.iConomy == null) {
            if (test != null) {
                MultiVerseCore.iConomy = (iConomy) test;
            }
        }
    }
    
    /**
     * Load the Configuration files OR create the default config files.
     */
    public void loadConfigs() {
        // Call the defaultConfiguration class to create the config files if they don't already exist.
        new DefaultConfiguration(dataFolder, "config.yml");
        new DefaultConfiguration(dataFolder, "worlds.yml", "worlds:");
        
        // Now grab the Configuration Files.
        configMV = new Configuration(new File(dataFolder, "config.yml"));
        configWorlds = new Configuration(new File(dataFolder, "worlds.yml"));
        
        // Now attempt to Load the configurations.
        try{ 
            configMV.load();
            log.info(logPrefix + "- MultiVerse Config -- Loaded");
        } catch (Exception e){ log.info(MultiVerseCore.logPrefix + "- Failed to load config.yml"); }
        
        try{ 
            configWorlds.load();
            log.info(logPrefix + "- World Config -- Loaded");
        } catch (Exception e){ log.info(MultiVerseCore.logPrefix + "- Failed to load worlds.yml"); }
        
        // Setup the Debug option, we'll default to false because this option will not be in the default config.
        MultiVerseCore.debug = configMV.getBoolean("debug", false);
    }

    /**
     * Purge the Worlds of Entities that are disallowed.
     */
    private void purgeWorlds() {
        if(worlds.size()<=0) return;

        Set<String> worldKeys = worlds.keySet();
        for (String key : worldKeys){
            World world = getServer().getWorld(key);
            if(world==null) continue;
            
            // TODO: Sort out the Entity Purge, only purge what is configured to be.
            
            /*List<LivingEntity> entities = world.getLivingEntities();

            MVWorld mvworld = worlds.get(key);
            for (Entity entity: entities){

            }*/
        }
    }

    /**
     * Setup commands to the Command Handler
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
        commands.put("mvwho", new MVWho(this));
        commands.put("mvreload", new MVReload(this));
    }
    
    /**
     * Load the Worlds & Settings from the configuration file.
     */
	public void loadWorlds() {
	    // Basic Counter to count how many Worlds we are loading.
	    int count = 0;
	    List<String> worldKeys = MultiVerseCore.configWorlds.getKeys("worlds"); // Grab all the Worlds from the Config.
	    
	    if(worldKeys != null){
	        for (String worldKey : worldKeys){
	            // If this World already exists within the HashMap then we don't need to process it.
	            if(worlds.containsKey(worldKey)){
	                continue;
	            }
	            
	            String wEnvironment = MultiVerseCore.configWorlds.getString("worlds." + worldKey + ".environment", "NORMAL"); // Grab the Environment as a String.
	         
	            Environment env;
	            if(wEnvironment.equalsIgnoreCase("NETHER")) // Check if the selected Environment is NETHER, otherwise we just default to NORMAL.
	                env = Environment.NETHER;
	            else
	                env = Environment.NORMAL;
	            
	            log.info(logPrefix + "Loading World & Settings - '" + worldKey + "' - " + wEnvironment); // Output to the Log that wea re loading a world, specify the name and environment type.
	            
	            World world = getServer().createWorld(worldKey, env);
                
                worlds.put(worldKey, new MVWorld(world, MultiVerseCore.configWorlds, this)); // Place the World into the HashMap. 
                
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
     * Grab and return the Teleport class.
     * @return
     */
    public MVTeleport getTeleporter() {
    	return new MVTeleport(this);
    }
	
    /**
     * Grab the iConomy setup. 
     * @return
     */
    public static iConomy getiConomy() {
        return iConomy;
    }
    
    /**
     * Grab the Permissions Handler for MultiVerse
     */
    public static MVPermissions getPermissions() {
        return ph;
    }

    /**
     * This fires before plugins get Enabled... Not needed but saves Console Spam.
     */
    public void onLoad() {
    }
    
	/**
	 * onCommand
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	    if(this.isEnabled() == false){
	        sender.sendMessage("This plugin is Disabled!");
	        return true;
	    }
	    
        MVCommandHandler handler = commands.get(command.getName().toLowerCase());
	        
        if (handler!=null) {
            return handler.perform(sender, args);
        } else {
            return false;
        }
	}
	
	/**
	 * Basic Debug Output function, if we've enabled debugging we'll output more information.
	 */
    public static void debugMsg(String msg){
        debugMsg(msg,null);
    }
	public static void debugMsg(String msg, Player p){
	    if(debug){
	        log.info(msg);
	        if(p!=null){
	            p.sendMessage(msg);
	        }
	    }
	}
	
    /**
     * Parse the Authors Array into a readable String with ',' and 'and'.
     * @return
     */
    private String getAuthors(){
        String authors = "";
        for(int i=0;i<this.getDescription().getAuthors().size();i++){
            if(i==this.getDescription().getAuthors().size()-1){
                authors += " and " + this.getDescription().getAuthors().get(i);
            } else {
                authors += ", " + this.getDescription().getAuthors().get(i);
            }
        }
        return authors.substring(2);
    }
}