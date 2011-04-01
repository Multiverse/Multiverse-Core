package com.onarandombox.MultiverseCore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.onarandombox.MultiverseCore.commands.MVCoord;
import com.onarandombox.MultiverseCore.commands.MVCreate;
import com.onarandombox.MultiverseCore.commands.MVImport;
import com.onarandombox.MultiverseCore.commands.MVList;
import com.onarandombox.MultiverseCore.commands.MVModify;
import com.onarandombox.MultiverseCore.commands.MVReload;
import com.onarandombox.MultiverseCore.commands.MVRemove;
import com.onarandombox.MultiverseCore.commands.MVSetSpawn;
import com.onarandombox.MultiverseCore.commands.MVSpawn;
import com.onarandombox.MultiverseCore.commands.MVTP;
import com.onarandombox.MultiverseCore.commands.MVWho;
import com.onarandombox.MultiverseCore.configuration.DefaultConfiguration;
import com.onarandombox.utils.DebugLog;
import com.onarandombox.utils.Messaging;
import com.onarandombox.utils.UpdateChecker;

public class MultiverseCore extends JavaPlugin {

    // Setup a variable to hold our DataFolder which will house everything to do with Multiverse
    public static final File dataFolder = new File("plugins" + File.separator + "Multiverse");

    // Useless stuff to keep us going.
    private static final Logger log = Logger.getLogger("Minecraft");
    private static DebugLog debugLog;

    // Debug Mode
    private boolean debug;

    // Setup our Map for our Commands using the CommandHandler.
    private Map<String, MVCommandHandler> commands = new HashMap<String, MVCommandHandler>();

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
        dataFolder.mkdirs();
        debugLog = new DebugLog("Multiverse", dataFolder + File.separator + "debug.log");
    }

    @Override
    public void onEnable() {
        // Output a little snippet to show it's enabled.
        log(Level.INFO, "- Version " + this.getDescription().getVersion() + " Enabled - By " + getAuthors());

        // Setup & Load our Configuration files.
        loadConfigs();

        // Setup all the Events the plugin needs to Monitor.
        registerEvents();
        // Call the Function to load all the Worlds and setup the HashMap
        loadWorlds();
        // Purge Worlds of old Monsters/Animals which don't adhere to the setup.
        purgeWorlds();
        // Setup Permissions, we'll do an initial check for the Permissions plugin then fall back on isOP().
        setupPermissions();
        // Setup iConomy.
        setupiConomy();
        // Call the Function to assign all the Commands to their Class.
        setupCommands();

        // Start the Update Checker
        // updateCheck = new UpdateChecker(this.getDescription().getName(), this.getDescription().getVersion());
    }

    /**
     * Function to Register all the Events needed.
     */
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Highest, this); // Low so it acts above any other.
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Highest, this); // Cancel Teleports if needed.
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this); // To create the Player Session
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this); // To remove Player Sessions

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
    private void setupiConomy() {
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
        new DefaultConfiguration(dataFolder, "config.yml");
        new DefaultConfiguration(dataFolder, "worlds.yml");

        // Now grab the Configuration Files.
        configMV = new Configuration(new File(dataFolder, "config.yml"));
        configWorlds = new Configuration(new File(dataFolder, "worlds.yml"));

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

        Set<String> worldKeys = worlds.keySet();
        for (String key : worldKeys) {
            World world = getServer().getWorld(key);
            if (world == null)
                continue;

            // TODO: Sort out the Entity Purge, only purge what is configured to be.

            /*
             * List<LivingEntity> entities = world.getLivingEntities();
             * 
             * MVWorld mvworld = worlds.get(key);
             * for (Entity entity: entities){
             * 
             * }
             */
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
        // Grab all the Worlds from the Config.
        List<String> worldKeys = configWorlds.getKeys("worlds");

        // Check that the list actually contains any data.
        if (worldKeys != null && worldKeys.size() > 0) {
            for (String worldKey : worldKeys) {
                // Check if the World is already loaded within the Plugin.
                if (worlds.containsKey(worldKey)) {
                    continue;
                }
                // Grab the initial values from the config file.
                String environment = configWorlds.getString("worlds." + worldKey + ".environment", "NORMAL"); // Grab the Environment as a String.
                String seedString = configWorlds.getString("worlds." + worldKey + ".seed", null);
                Long seed = null;
                // Work out the Environment
                Environment env;
                if (environment.equalsIgnoreCase("NETHER")) { // Check if the selected Environment is NETHER, otherwise we just default to NORMAL.
                    env = Environment.NETHER;
                } else {
                    env = Environment.NORMAL;
                }
                // Output to the Log that wea re loading a world, specify the name and environment type.
                log(Level.INFO, "Loading World & Settings - '" + worldKey + "' - " + environment);
                
                // If a seed was given we need to parse it to a Long Format.
                if (seedString.length() > 0) {
                    try {
                        seed = Long.parseLong(seedString);
                    } catch (NumberFormatException numberformatexception) {
                        seed = (long) seedString.hashCode();
                    }
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
        // Simple Output to the Console to show how many Worlds were loaded.
        log(Level.INFO, count + " - World(s) loaded.");
    }

    /**
     * Add a new World to the Multiverse Setup.
     * @param name World Name
     * @param environment Environment Type
     */
    public void addWorld(String name, Environment environment, Long seed) {
        if (seed != null) {
            World world = getServer().createWorld(name, environment, seed);
            worlds.put(name, new MVWorld(world, configWorlds, this, seed)); // Place the World into the HashMap.
        } else {
            World world = getServer().createWorld(name, environment);
            worlds.put(name, new MVWorld(world, configWorlds, this, null)); // Place the World into the HashMap.
        }
    }

    public void addWorld(String name, Environment environment) {
        addWorld(name, environment, null);
    }

    /**
     * What happens when the plugin gets disabled...
     */
    @Override
    public void onDisable() {
        MultiverseCore.Permissions = null;
        log(Level.INFO, "- Disabled");
    }

    /**
     * Grab the players session if one exists, otherwise create a session then return it.
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

        MVCommandHandler handler = commands.get(command.getName().toLowerCase());

        if (handler != null) {
            return handler.perform(sender, args);
        } else {
            return false;
        }
    }

    /**
     * Print messages to the server Log as well as to our DebugLog.
     * 'debugLog' is used to seperate Heroes information from the Servers Log Output.
     * @param level
     * @param msg
     */
    public void log(Level level, String msg) {
        log.log(level, "[Multiverse-Core] " + msg);
        debugLog.log(level, "[Multiverse-Core] " + msg);
    }

    /**
     * Print messages to the Debug Log, if the servers in Debug Mode then we
     * also wan't to print the messages to the standard Server Console.
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
}
