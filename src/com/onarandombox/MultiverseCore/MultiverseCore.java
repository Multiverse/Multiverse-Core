package com.onarandombox.MultiverseCore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.iConomy.iConomy;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.onarandombox.MultiverseCore.command.CommandManager;
import com.onarandombox.MultiverseCore.command.commands.CoordCommand;
import com.onarandombox.MultiverseCore.command.commands.HelpCommand;
import com.onarandombox.MultiverseCore.command.commands.ListCommand;
import com.onarandombox.MultiverseCore.command.commands.CreateCommand;
import com.onarandombox.MultiverseCore.command.commands.ImportCommand;
import com.onarandombox.MultiverseCore.command.commands.SpawnCommand;
import com.onarandombox.MultiverseCore.command.commands.SetSpawnCommand;
import com.onarandombox.MultiverseCore.command.commands.WhoCommand;
import com.onarandombox.MultiverseCore.command.commands.TeleportCommand;
import com.onarandombox.MultiverseCore.commands.MVModify;
import com.onarandombox.MultiverseCore.commands.MVReload;
import com.onarandombox.MultiverseCore.commands.MVRemove;
import com.onarandombox.MultiverseCore.configuration.DefaultConfiguration;
import com.onarandombox.utils.DebugLog;
import com.onarandombox.utils.Messaging;
import com.onarandombox.utils.UpdateChecker;

public class MultiverseCore extends JavaPlugin {
    
    // Useless stuff to keep us going.
    private static final Logger log = Logger.getLogger("Minecraft");
    private static DebugLog debugLog;
    
    // Debug Mode
    private boolean debug;
    
    // Setup our Map for our Commands using the CommandHandler.
    private Map<String, MVCommandHandler> commands = new HashMap<String, MVCommandHandler>();
    private CommandManager commandManager = new CommandManager();
    
    private final String tag = "[Multiverse-Core]";
    
    // Messaging
    private Messaging messaging = new Messaging();
    
    // Multiverse Permissions Handler
    public MVPermissions ph = new MVPermissions(this);
    
    // Permissions Handler
    public static PermissionHandler Permissions = null;
    
    // iConomy Handler
    public static iConomy iConomy = null;
    public static boolean useiConomy = false;
    
    // Configurations
    public Configuration configMV = null;
    public Configuration configWorlds = null;
    
    // Setup the block/player/entity listener.
    private MVPlayerListener playerListener = new MVPlayerListener(this);;
    @SuppressWarnings("unused")
    private MVBlockListener blockListener = new MVBlockListener(this);
    private MVEntityListener entityListener = new MVEntityListener(this);
    private MVPluginListener pluginListener = new MVPluginListener(this);
    
    public UpdateChecker updateCheck;
    
    // HashMap to contain all the Worlds which this Plugin will manage.
    public HashMap<String, MVWorld> worlds = new HashMap<String, MVWorld>();
    
    // HashMap to contain information relating to the Players.
    public HashMap<String, MVPlayerSession> playerSessions = new HashMap<String, MVPlayerSession>();
    
    @Override
    public void onLoad() {
        // Create our DataFolder
        getDataFolder().mkdirs();
        // Setup our Debug Log
        debugLog = new DebugLog("Multiverse", getDataFolder() + File.separator + "debug.log");
        
        // Setup & Load our Configuration files.
        loadConfigs();
        // Call the Function to load all the Worlds and setup the HashMap
        loadWorlds();
    }
    
    @Override
    public void onEnable() {
        // Output a little snippet to show it's enabled.
        log(Level.INFO, "- Version " + this.getDescription().getVersion() + " Enabled - By " + getAuthors());
        
        // Setup all the Events the plugin needs to Monitor.
        registerEvents();
        // Setup Permissions, we'll do an initial check for the Permissions plugin then fall back on isOP().
        setupPermissions();
        // Setup iConomy.
        setupEconomy();
        // Call the Function to assign all the Commands to their Class.
        setupCommands();
        registerCommands();
        
        // Start the Update Checker
        // updateCheck = new UpdateChecker(this.getDescription().getName(), this.getDescription().getVersion());
        
        // Purge Worlds of old Monsters/Animals which don't adhere to the setup.
        purgeWorlds();
    }
    
    /**
     * Function to Register all the Events needed.
     */
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        // pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Highest, this); // Low so it acts above any other.
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Highest, this); // Cancel Teleports if needed.
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this); // To create the Player Session
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this); // To remove Player Sessions
        pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Highest, this);
        
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this); // To Allow/Disallow PVP as well as EnableHealth.
        pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this); // To prevent all or certain animals/monsters from spawning.
        
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, pluginListener, Priority.Monitor, this);
        
        // pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this); // To prevent Blocks being destroyed.
        // pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this); // To prevent Blocks being placed.
        // pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this); // Try to prevent Ghasts from blowing up structures.
        // pm.registerEvent(Event.Type.EXPLOSION_PRIMED, entityListener, Priority.Normal, this); // Try to prevent Ghasts from blowing up structures.
    }
    
    /**
     * Check for Permissions plugin and then setup our own Permissions Handler.
     */
    private void setupPermissions() {
        Plugin p = this.getServer().getPluginManager().getPlugin("Permissions");
        
        if (MultiverseCore.Permissions == null) {
            if (p != null && p.isEnabled()) {
                MultiverseCore.Permissions = ((Permissions) p).getHandler();
                log(Level.INFO, "- Attached to Permissions");
            }
        }
    }
    
    /**
     * Check for the iConomy plugin and set it up accordingly.
     */
    private void setupEconomy() {
        Plugin test = this.getServer().getPluginManager().getPlugin("iConomy");
        
        if (MultiverseCore.iConomy == null) {
            if (test != null) {
                MultiverseCore.iConomy = (iConomy) test;
            }
        }
    }
    
    /**
     * Load the Configuration files OR create the default config files.
     */
    public void loadConfigs() {
        // Call the defaultConfiguration class to create the config files if they don't already exist.
        new DefaultConfiguration(getDataFolder(), "config.yml");
        new DefaultConfiguration(getDataFolder(), "worlds.yml");
        
        // Now grab the Configuration Files.
        configMV = new Configuration(new File(getDataFolder(), "config.yml"));
        configWorlds = new Configuration(new File(getDataFolder(), "worlds.yml"));
        
        // Now attempt to Load the configurations.
        try {
            configMV.load();
            log(Level.INFO, "- Multiverse Config -- Loaded");
        } catch (Exception e) {
            log(Level.INFO, "- Failed to load config.yml");
        }
        
        try {
            configWorlds.load();
            log(Level.INFO, "- World Config -- Loaded");
        } catch (Exception e) {
            log(Level.INFO, "- Failed to load worlds.yml");
        }
        
        // Setup the Debug option, we'll default to false because this option will not be in the default config.
        this.debug = configMV.getBoolean("debug", false);
    }
    
    /**
     * Purge the Worlds of Entities that are disallowed.
     */
    private void purgeWorlds() {
        if (worlds.size() <= 0)
            return;
        
        // TODO: Need a better method than this... too messy and atm it's not complete.
        
        Set<String> worldKeys = worlds.keySet();
        for (String key : worldKeys) {
            World world = getServer().getWorld(key);
            if (world == null)
                continue;
            MVWorld mvworld = worlds.get(key);
            List<String> monsters = mvworld.monsterList;
            List<String> animals = mvworld.animalList;
            System.out.print(monsters.size() + " - " + animals.size());
            for (Entity e : world.getEntities()) {
                // Check against Monsters
                if (e instanceof Creeper || e instanceof Skeleton || e instanceof Spider || e instanceof Zombie || e instanceof Ghast || e instanceof PigZombie || e instanceof Giant || e instanceof Slime || e instanceof Monster) {
                    // If Monsters are disabled and there's no exceptions we can simply remove them.
                    if (mvworld.monsters == false && !(monsters.size() > 0)) {
                        e.remove();
                        continue;
                    }
                    // If monsters are enabled and there's no exceptions we can continue to the next set.
                    if (mvworld.monsters == true && !(monsters.size() > 0)) {
                        continue;
                    }
                    String creature = e.toString().replaceAll("Craft", "");
                    if (monsters.contains(creature.toUpperCase())) {
                        if (mvworld.monsters) {
                            System.out.print(creature + " - Removed");
                            e.remove();
                            continue;
                        }
                    }
                }
                // Check against Animals
                if (e instanceof Chicken || e instanceof Cow || e instanceof Sheep || e instanceof Pig || e instanceof Squid || e instanceof Animals) {
                    // If Monsters are disabled and there's no exceptions we can simply remove them.
                    if (mvworld.animals == false && !(animals.size() > 0)) {
                        e.remove();
                        continue;
                    }
                    // If monsters are enabled and there's no exceptions we can continue to the next set.
                    if (mvworld.animals == true && !(animals.size() > 0)) {
                        continue;
                    }
                    String creature = e.toString().replaceAll("Craft", "");
                    if (animals.contains(creature.toUpperCase())) {
                        if (mvworld.animals) {
                            e.remove();
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Register Heroes commands to DThielke's Command Manager.
     */
    private void registerCommands() {
        // Page 1
        commandManager.addCommand(new HelpCommand(this));
        commandManager.addCommand(new CoordCommand(this));
        commandManager.addCommand(new TeleportCommand(this));
        commandManager.addCommand(new ListCommand(this));
        commandManager.addCommand(new WhoCommand(this));
        commandManager.addCommand(new SetSpawnCommand(this));
        commandManager.addCommand(new CreateCommand(this));
        commandManager.addCommand(new ImportCommand(this));
        commandManager.addCommand(new SpawnCommand(this));
    }
    
    /**
     * Setup commands to the Command Handler
     */
    private void setupCommands() {
        // commands.put("mvcreate", new CreateCommand(this));
        // commands.put("mvimport", new ImportCommand(this));
        commands.put("mvremove", new MVRemove(this));
        commands.put("mvmodify", new MVModify(this));
        // commands.put("mvtp", new TeleportCommand(this));
        // commands.put("mvlist", new ListCommand(this));
        // commands.put("mvsetspawn", new SetSpawnCommand(this));
        //commands.put("mvspawn", new SpawnCommand(this));
        // commands.put("mvcoord", new MVCoord(this));
        // commands.put("mvwho", new WhoCommand(this));
        commands.put("mvreload", new MVReload(this));
    }
    
    /**
     * Load the Worlds & Settings from the configuration file.
     */
    public void loadWorlds() {
        // Basic Counter to count how many Worlds we are loading.
        int count = 0;
        // Grab all the Worlds from the Config.
        List<String> worldKeys = configWorlds.getKeys("worlds");
        
        // Check that the list is not null.
        if (worldKeys != null) {
            for (String worldKey : worldKeys) {
                // Check if the World is already loaded within the Plugin.
                if (worlds.containsKey(worldKey)) {
                    continue;
                }
                // Grab the initial values from the config file.
                String environment = configWorlds.getString("worlds." + worldKey + ".environment", "NORMAL"); // Grab the Environment as a String.
                String seedString = configWorlds.getString("worlds." + worldKey + ".seed", "");
                Long seed = null;
                // Work out the Environment
                Environment env = getEnvFromString(environment, null);
                if(env == null) {
                    env = Environment.NORMAL;
                }
                // If a seed was given we need to parse it to a Long Format.
                if (seedString.length() > 0) {
                    // Output to the Log that we are loading a world, specify the name, environment type and seed.
                    log(Level.INFO, "Loading World & Settings - '" + worldKey + "' - " + environment + " with seed: " + seedString);
                    try {
                        seed = Long.parseLong(seedString);
                    } catch (NumberFormatException numberformatexception) {
                        seed = (long) seedString.hashCode();
                    }
                } else {
                    // Output to the Log that we are loading a world, specify the name and environment type.
                    log(Level.INFO, "Loading World & Settings - '" + worldKey + "' - " + environment);
                }
                // If we don't have a seed then add a standard World, else add the world with the Seed.
                if (seed == null) {
                    addWorld(worldKey, env, null);
                } else {
                    addWorld(worldKey, env, seed);
                }
                // Increment the World Count.
                count++;
            }
        }
        
        // Ensure that the worlds created by the default server were loaded into MV, useful for first time runs
        count += loadDefaultWorlds();
        
        // Simple Output to the Console to show how many Worlds were loaded.
        log(Level.INFO, count + " - World(s) loaded.");
    }
    
    /**
     * 
     * @return
     */
    private int loadDefaultWorlds() {
        int additonalWorldsLoaded = 0;
        // Load the default world:
        World world = this.getServer().getWorlds().get(0);
        if(!this.worlds.containsKey(world.getName())) {
            log.info("Loading World & Settings - '" + world.getName() + "' - " + world.getEnvironment());
            addWorld(world.getName(), Environment.NORMAL, null);
            additonalWorldsLoaded++;
        }
        
        // This next one could be null if they have it disabled in server.props
        World world_nether = this.getServer().getWorld(world.getName() + "_nether");
        if(world_nether != null && !this.worlds.containsKey(world_nether.getName())) {
            log.info("Loading World & Settings - '" + world.getName() + "' - " + world_nether.getEnvironment());
            addWorld(world_nether.getName(), Environment.NETHER, null);
            additonalWorldsLoaded++;
        }
        
        return additonalWorldsLoaded;
    }

    /**
     * Get the worlds Seed.
     * 
     * @param w World
     * @return Seed
     */
    public long getSeed(World w) {
        return ((CraftWorld) w).getHandle().worldData.b();
    }
    
    /**
     * Add a new World to the Multiverse Setup.
     * 
     * @param name World Name
     * @param environment Environment Type
     */
    public void addWorld(String name, Environment environment, Long seed) {
        if (seed != null) {
            World world = getServer().createWorld(name, environment, seed);
            worlds.put(name, new MVWorld(world, configWorlds, this, seed)); // Place the World into the HashMap.
            System.out.print("Seed - " + getSeed(world));
        } else {
            World world = getServer().createWorld(name, environment);
            worlds.put(name, new MVWorld(world, configWorlds, this, null)); // Place the World into the HashMap.
            System.out.print("Seed - " + getSeed(world));
        }
    }
    
    public void addWorld(String name, Environment environment) {
        addWorld(name, environment, null);
    }
    
    public boolean removeWorld(String name) {
        if(worlds.containsKey(name)) {
            worlds.remove(name);
            return true;
        }
        return false;
    }
    
    /**
     * What happens when the plugin gets disabled...
     */
    @Override
    public void onDisable() {
        debugLog.close();
        MultiverseCore.Permissions = null;
        log(Level.INFO, "- Disabled");
    }
    
    /**
     * Grab the players session if one exists, otherwise create a session then return it.
     * 
     * @param player
     * @return
     */
    public MVPlayerSession getPlayerSession(Player player) {
        if (playerSessions.containsKey(player.getName())) {
            return playerSessions.get(player.getName());
        } else {
            playerSessions.put(player.getName(), new MVPlayerSession(player, configMV, this));
            return playerSessions.get(player.getName());
        }
    }
    
    /**
     * Grab and return the Teleport class.
     * 
     * @return
     */
    public MVTeleport getTeleporter() {
        return new MVTeleport(this);
    }
    
    /**
     * Grab the iConomy setup.
     * 
     * @return
     */
    public static iConomy getiConomy() {
        return iConomy;
    }
    
    /**
     * Grab the Permissions Handler for MultiVerse
     */
    public MVPermissions getPermissions() {
        return this.ph;
    }
    
    /**
     * onCommand
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (this.isEnabled() == false) {
            sender.sendMessage("This plugin is Disabled!");
            return true;
        }
        return commandManager.dispatch(sender, command, commandLabel, args);
    }
    
    /**
     * Print messages to the server Log as well as to our DebugLog. 'debugLog' is used to seperate Heroes information from the Servers Log Output.
     * 
     * @param level
     * @param msg
     */
    public void log(Level level, String msg) {
        log.log(level, "[Multiverse-Core] " + msg);
        debugLog.log(level, "[Multiverse-Core] " + msg);
    }
    
    /**
     * Print messages to the Debug Log, if the servers in Debug Mode then we also wan't to print the messages to the standard Server Console.
     * 
     * @param level
     * @param msg
     */
    public void debugLog(Level level, String msg) {
        if (this.debug) {
            log.log(level, "[Debug] " + msg);
        }
        debugLog.log(level, "[Debug] " + msg);
    }
    
    public Messaging getMessaging() {
        return messaging;
    }
    
    /**
     * Parse the Authors Array into a readable String with ',' and 'and'.
     * 
     * @return
     */
    private String getAuthors() {
        String authors = "";
        ArrayList<String> auths = this.getDescription().getAuthors();
        
        if (auths.size() == 1) {
            return auths.get(0);
        }
        
        for (int i = 0; i < auths.size(); i++) {
            if (i == this.getDescription().getAuthors().size() - 1) {
                authors += " and " + this.getDescription().getAuthors().get(i);
            } else {
                authors += ", " + this.getDescription().getAuthors().get(i);
            }
        }
        return authors.substring(2);
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public String getTag() {
        return tag;
    }
    
    /**
     * This code should get moved somewhere more appropriate, but for now, it's here.
     * 
     * @param env
     * @return
     */
    public Environment getEnvFromString(String env, CommandSender sender) {
        Environment environment = null;
        // Don't reference the enum directly as there aren't that many, and we can be more forgiving to users this way
        if (env.equalsIgnoreCase("HELL") || env.equalsIgnoreCase("NETHER"))
            env = "NETHER";
        
        if (env.equalsIgnoreCase("SKYLANDS") || env.equalsIgnoreCase("SKYLAND") || env.equalsIgnoreCase("STARWARS"))
            env = "SKYLANDS";
        
        if (env.equalsIgnoreCase("NORMAL") || env.equalsIgnoreCase("WORLD"))
            env = "NORMAL";
        
        try {
            environment = Environment.valueOf(env);
        } catch (IllegalArgumentException e) {
            // Sender will be null on loadWorlds
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "Environment type " + env + " does not exist!");
            }
            // TODO: Show the player the mvenvironments command.
        }
        return environment;
    }
}
