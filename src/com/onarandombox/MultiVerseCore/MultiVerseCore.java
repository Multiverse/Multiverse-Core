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
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
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
import com.onarandombox.MultiVerseCore.configuration.DefaultConfiguration;

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
    
    public static final File dataFolder = new File("plugins" + File.separator + "MultiVerse");
    
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
        log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");

        // Quick check for the Permissions Plugin, if we don't find it here then we'll check the plugin onEnable event.
        // TODO: Sort out Permissions Support...
        /*if(getServer().getPluginManager().getPlugin("Permissions")==null){
            log.info(logPrefix + "- Commands have been DISABLED until Permissions has been found.");
        } else {
            Permissions = com.nijikokun.bukkit.Permissions.Permissions.Security;
        }*/

        loadConfigs();
        
        // Setup all the Events the plugin needs to Monitor.
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Low, this); // Low so it acts above any other.
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this); // Cancel Teleports if needed.
        //pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.High, this); // To Add World Prefixing to Chat. -- Separate Plugin, maybe...
        //pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this); // Respawn Players at the right point. -- No need to handle it anymore with setSpawnLocation()
        
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,Priority.Normal, this); // To remove Player Sessions

        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this); // To prevent Blocks being destroyed.
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this); // To prevent Blocks being placed.
        
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this); // To Allow/Disallow PVP as well as EnableHealth.
        
        pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this); // To prevent all or certain animals/monsters from spawning.

        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this); // Try to prevent Ghasts from blowing up structures.
        pm.registerEvent(Event.Type.EXPLOSION_PRIMED, entityListener, Priority.Normal, this); // Try to prevent Ghasts from blowing up structures.
        
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Normal, this); // Monitor for Permissions Plugin etc.
        
        pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this);
        // Call the Function to load all the Worlds and setup the HashMap
        loadWorlds();
        // Purge Worlds of old Monsters/Animals which don't adhere to the setup.
        purgeWorlds();
        // Call the Function to assign all the Commands to their Class.
        setupCommands();
    }
    
    public void loadConfigs() {
        // Call the defaultConfiguration class to create the config files if they don't already exist.
        new DefaultConfiguration(dataFolder, "config.yml");
        new DefaultConfiguration(dataFolder, "worlds.yml");
        
        // Now grab the Configuration Files.
        configMV = new Configuration(new File(dataFolder, "config.yml"));
        configWorlds = new Configuration(new File(dataFolder, "worlds.yml"));
        
        // Now attempt to Load the configurations.
        try{ 
            configMV.load();
            log.info(logPrefix + "MultiVerse Config -- Loaded");
        } catch (Exception e){ log.info(MultiVerseCore.logPrefix + "- Failed to load config.yml"); }
        
        try{ 
            configWorlds.load();
            log.info(logPrefix + "World Config -- Loaded");
        } catch (Exception e){ log.info(MultiVerseCore.logPrefix + "- Failed to load worlds.yml"); }
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
            
            List<LivingEntity> entities = world.getLivingEntities();

            MVWorld mvworld = worlds.get(key);
            for (Entity entity: entities){

                /**
                 * Animal Handling
                 */
                if(entity instanceof Animals){
                    // If we have no exceptions for Animals then we just follow the Spawn setting.
                    if(mvworld.animalList.size()<=0){
                        if(mvworld.animals){
                            return;
                        } else {
                            entity.remove();
                            return;
                        }
                    }
                    // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
                    if(mvworld.animalList.contains(){
                        if(mvworld.animals){
                            entity.remove();
                            return;
                        } else {
                            return;
                        }
                    }
                }
                /**
                 * Monster Handling
                 */
                /*if(entity instanceof Monster || entity instanceof Ghast || entity instanceof PigZombie){
                    // If we have no exceptions for Monsters then we just follow the Spawn setting.
                    if(mvworld.monsterList.size()<=0){
                        if(mvworld.monsters){
                            return;
                        } else {
                            entity.remove();
                            return;
                        }
                    }
                    // The idea of the Exceptions is they do the OPPOSITE of what the Spawn setting is...
                    if(mvworld.monsterList.contains(event.getMobType().toString().toUpperCase())){
                        if(mvworld.monsters){
                            entity.remove();
                            return;
                        } else {
                            return;
                        }
                    }
                }*/
            }
            
            
            // TODO: Refine this... need to cast to CreatureType or something, we only wan't to remove the creatures they don't want. Not all of them.
            // TODO: Lack of Internet & JavaDocs ftw...
            /*for (Entity entity: entities){
                if(entity instanceof Monster || entity instanceof Animals || entity instanceof Ghast || entity instanceof Squid || entity instanceof PigZombie){
                    entity.remove();
                }
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
	            
	            Boolean monsters = MultiVerseCore.configWorlds.getBoolean("worlds." + worldKey + ".monsters", true); // Grab whether we want to spawn Monsters.
	            Boolean animals = MultiVerseCore.configWorlds.getBoolean("worlds." + worldKey + ".animals", true); // Grab whether we want to spawn Animals.
	         
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
    
    public MVTeleport getTeleporter() {
    	return new MVTeleport(this);
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
}