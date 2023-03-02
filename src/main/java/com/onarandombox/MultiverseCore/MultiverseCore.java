/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.anchor.AnchorManager;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.LocationManipulation;
import com.onarandombox.MultiverseCore.api.MVConfig;
import com.onarandombox.MultiverseCore.api.MVCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.commands.CheckCommand;
import com.onarandombox.MultiverseCore.commands.CloneCommand;
import com.onarandombox.MultiverseCore.commands.ConfirmCommand;
import com.onarandombox.MultiverseCore.commands.CreateCommand;
import com.onarandombox.MultiverseCore.commands.DebugCommand;
import com.onarandombox.MultiverseCore.commands.DeleteCommand;
import com.onarandombox.MultiverseCore.commands.ImportCommand;
import com.onarandombox.MultiverseCore.commands.GameruleCommand;
import com.onarandombox.MultiverseCore.commands.LoadCommand;
import com.onarandombox.MultiverseCore.commands.RegenCommand;
import com.onarandombox.MultiverseCore.commands.ReloadCommand;
import com.onarandombox.MultiverseCore.commands.RemoveCommand;
import com.onarandombox.MultiverseCore.commands.RootCommand;
import com.onarandombox.MultiverseCore.commands.TeleportCommand;
import com.onarandombox.MultiverseCore.commands.UnloadCommand;
import com.onarandombox.MultiverseCore.commands.UsageCommand;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.destination.DestinationsProvider;
import com.onarandombox.MultiverseCore.destination.core.AnchorDestination;
import com.onarandombox.MultiverseCore.destination.core.BedDestination;
import com.onarandombox.MultiverseCore.destination.core.CannonDestination;
import com.onarandombox.MultiverseCore.destination.core.ExactDestination;
import com.onarandombox.MultiverseCore.destination.core.PlayerDestination;
import com.onarandombox.MultiverseCore.destination.core.WorldDestination;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.event.MVDebugModeEvent;
import com.onarandombox.MultiverseCore.listeners.MVChatListener;
import com.onarandombox.MultiverseCore.listeners.MVEntityListener;
import com.onarandombox.MultiverseCore.listeners.MVPlayerListener;
import com.onarandombox.MultiverseCore.listeners.MVPortalListener;
import com.onarandombox.MultiverseCore.listeners.MVWeatherListener;
import com.onarandombox.MultiverseCore.listeners.MVWorldInitListener;
import com.onarandombox.MultiverseCore.listeners.MVWorldListener;
import com.onarandombox.MultiverseCore.teleportation.SimpleBlockSafety;
import com.onarandombox.MultiverseCore.teleportation.SimpleLocationManipulation;
import com.onarandombox.MultiverseCore.teleportation.SimpleSafeTTeleporter;
import com.onarandombox.MultiverseCore.utils.MVPermissions;
import com.onarandombox.MultiverseCore.utils.TestingMode;
import com.onarandombox.MultiverseCore.utils.UnsafeCallWrapper;
import com.onarandombox.MultiverseCore.utils.metrics.MetricsConfigurator;
import com.onarandombox.MultiverseCore.world.SimpleMVWorldManager;
import com.onarandombox.MultiverseCore.world.WorldProperties;
import me.main__.util.SerializationConfig.SerializationConfig;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

/**
 * The implementation of the Multiverse-{@link MVCore}.
 */
public class MultiverseCore extends JavaPlugin implements MVCore {
    private static final int PROTOCOL = 50;

    // Setup various managers
    private final AnchorManager anchorManager = new AnchorManager(this);
    private BlockSafety blockSafety = new SimpleBlockSafety(this);
    private MVCommandManager commandManager;
    private DestinationsProvider destinationsProvider;
    private MVEconomist economist;
    private LocationManipulation locationManipulation = new SimpleLocationManipulation();
    private final MVPermissions mvPermissions = new MVPermissions(this);
    private SafeTTeleporter safeTTeleporter = new SimpleSafeTTeleporter(this);
    private final UnsafeCallWrapper unsafeCallWrapper = new UnsafeCallWrapper(this);
    private final MVWorldManager worldManager = new SimpleMVWorldManager(this);

    // Configurations
    private FileConfiguration multiverseConfig;
    private volatile MultiverseCoreConfiguration config;

    // Listeners
    private MVChatListener chatListener;
    private final MVEntityListener entityListener = new MVEntityListener(this);
    private final MVPlayerListener playerListener = new MVPlayerListener(this);
    private final MVPortalListener portalListener = new MVPortalListener(this);
    private final MVWeatherListener weatherListener = new MVWeatherListener(this);
    private final MVWorldListener worldListener = new MVWorldListener(this);
    private final MVWorldInitListener worldInitListener = new MVWorldInitListener(this);

    // Counter for the number of plugins that have registered with us
    private int pluginCount;

    /**
     * This is the constructor for the MultiverseCore.
     */
    public MultiverseCore() {
        super();
    }

    @Override
    public void onLoad() {
        // Create our DataFolder
        getDataFolder().mkdirs();

        // Setup our Logging
        Logging.init(this);

        // Register our config classes
        SerializationConfig.registerAll(MultiverseCoreConfiguration.class);
        SerializationConfig.registerAll(WorldProperties.class);
        SerializationConfig.initLogging(Logging.getLogger());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        // Load our configs first as we need them for everything else.
        this.loadConfigs();
        if (this.multiverseConfig == null) {
            Logging.severe("Your configs were not loaded.");
            Logging.severe("Please check your configs and restart the server.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Logging.setShowingConfig(!getMVConfig().getSilentStart());

        this.worldManager.getDefaultWorldGenerators();
        this.worldManager.loadDefaultWorlds();
        this.worldManager.loadWorlds(true);

        // Now set the firstspawnworld (after the worlds are loaded):
        this.worldManager.setFirstSpawnWorld(getMVConfig().getFirstSpawnWorld());
        MVWorld firstSpawnWorld = this.worldManager.getFirstSpawnWorld();
        if (firstSpawnWorld != null) {
            getMVConfig().setFirstSpawnWorld(firstSpawnWorld.getName());
        }

        //Setup economy here so vault is loaded
        this.economist = new MVEconomist(this);

        // Init all the other stuff
        this.anchorManager.loadAnchors();
        this.registerEvents();
        this.registerCommands();
        this.setUpLocales();
        this.registerDestinations();
        this.setupMetrics();
        this.saveMVConfig();
        this.logEnableMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        this.saveAllConfigs();
        Logging.shutdown();
    }

    /**
     * Function to Register all the Events needed.
     */
    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        this.chatListener = new MVChatListener(this, this.playerListener);
        pluginManager.registerEvents(this.chatListener, this);
        pluginManager.registerEvents(this.entityListener, this);
        pluginManager.registerEvents(this.playerListener, this);
        pluginManager.registerEvents(this.portalListener, this);
        pluginManager.registerEvents(this.weatherListener, this);
        pluginManager.registerEvents(this.worldListener, this);
        pluginManager.registerEvents(this.worldInitListener, this);
    }

    /**
     * Register Multiverse-Core commands to Command Manager.
     */
    private void registerCommands() {
        this.commandManager = new MVCommandManager(this);
        this.commandManager.registerCommand(new RootCommand(this));
        this.commandManager.registerCommand(new CheckCommand(this));
        this.commandManager.registerCommand(new CloneCommand(this));
        this.commandManager.registerCommand(new ConfirmCommand(this));
        this.commandManager.registerCommand(new CreateCommand(this));
        this.commandManager.registerCommand(new DebugCommand(this));
        this.commandManager.registerCommand(new DeleteCommand(this));
        this.commandManager.registerCommand(new ImportCommand(this));
        this.commandManager.registerCommand(new GameruleCommand(this));
        this.commandManager.registerCommand(new LoadCommand(this));
        this.commandManager.registerCommand(new RegenCommand(this));
        this.commandManager.registerCommand(new ReloadCommand(this));
        this.commandManager.registerCommand(new RemoveCommand(this));
        this.commandManager.registerCommand(new TeleportCommand(this));
        this.commandManager.registerCommand(new UnloadCommand(this));
    }

    /**
     * Register locales
     */
    private void setUpLocales() {
        this.commandManager.usePerIssuerLocale(true, true);
        this.commandManager.getLocales().addFileResClassLoader(this);
        this.commandManager.getLocales().addMessageBundles("multiverse-core");
    }

    /**
     * Register all the destinations.
     */
    private void registerDestinations() {
        this.destinationsProvider = new DestinationsProvider(this);
        this.destinationsProvider.registerDestination(new AnchorDestination(this));
        this.destinationsProvider.registerDestination(new BedDestination());
        this.destinationsProvider.registerDestination(new CannonDestination(this));
        this.destinationsProvider.registerDestination(new ExactDestination(this));
        this.destinationsProvider.registerDestination(new PlayerDestination());
        this.destinationsProvider.registerDestination(new WorldDestination(this));
    }

    /**
     * Setup bstats Metrics.
     */
    private void setupMetrics() {
        if (TestingMode.isDisabled()) {
            MetricsConfigurator.configureMetrics(this);
        }
    }

    /**
     * Logs the enable message.
     */
    private void logEnableMessage() {
        Logging.config("Version %s (API v%s) Enabled - By %s", this.getDescription().getVersion(), PROTOCOL, getAuthors());

        if (getMVConfig().isShowingDonateMessage()) {
            getLogger().config("Help dumptruckman keep this project alive. Become a patron! https://www.patreon.com/dumptruckman");
            getLogger().config("One time donations are also appreciated: https://www.paypal.me/dumptruckman");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MVCore getCore() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getProtocolVersion() {
        return PROTOCOL;
    }

    /**
     * {@inheritDoc}
     */
    public MVEconomist getEconomist() {
        return economist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MVPermissions getMVPerms() {
        return this.mvPermissions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthors() {
        List<String> authorsList = this.getDescription().getAuthors();
        if (authorsList.size() == 0) {
            return "";
        }

        StringBuilder authors = new StringBuilder();
        authors.append(authorsList.get(0));

        for (int i = 1; i < authorsList.size(); i++) {
            if (i == authorsList.size() - 1) {
                authors.append(" and ").append(authorsList.get(i));
            } else {
                authors.append(", ").append(authorsList.get(i));
            }
        }

        return authors.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MVCommandManager getMVCommandManager() {
        return this.commandManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPluginCount() {
        return this.pluginCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementPluginCount() {
        this.pluginCount += 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrementPluginCount() {
        this.pluginCount -= 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DestinationsProvider getDestinationsProvider() {
        return this.destinationsProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MVWorldManager getMVWorldManager() {
        return this.worldManager;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void loadConfigs() {
        // Now grab the Configuration Files.
        this.multiverseConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        InputStream resourceURL = this.getClass().getResourceAsStream("/defaults/config.yml");

        // Read in our default config with UTF-8 now
        Configuration coreDefaults;
        try {
            coreDefaults = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(resourceURL, "UTF-8")));
            this.multiverseConfig.setDefaults(coreDefaults);
        } catch (UnsupportedEncodingException e) {
            Logging.severe("Couldn't load default config with UTF-8 encoding. Details follow:");
            e.printStackTrace();
            Logging.severe("Default configs NOT loaded.");
        }

        this.multiverseConfig.options().copyDefaults(false);
        this.multiverseConfig.options().copyHeader(true);

        MultiverseCoreConfiguration wantedConfig = null;
        try {
            wantedConfig = (MultiverseCoreConfiguration) multiverseConfig.get("multiverse-configuration");
        } catch (Exception ignore) {
        } finally {
            config = ((wantedConfig == null) ? new MultiverseCoreConfiguration() : wantedConfig);
        }
        this.worldManager.loadWorldConfig(new File(getDataFolder(), "worlds.yml"));

        int level = Logging.getDebugLevel();
        Logging.setDebugLevel(getMVConfig().getGlobalDebug());
        if (level != Logging.getDebugLevel()) {
            getServer().getPluginManager().callEvent(new MVDebugModeEvent(level));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveMVConfig() {
        try {
            this.multiverseConfig.set("multiverse-configuration", getMVConfig());
            this.multiverseConfig.save(new File(getDataFolder(), "config.yml"));
            return true;
        } catch (IOException e) {
            Logging.severe("Could not save Multiverse config.yml config. Please check your file permissions.");
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveAllConfigs() {
        return this.saveMVConfig() && this.worldManager.saveWorldsConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnchorManager getAnchorManager() {
        return this.anchorManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockSafety getBlockSafety() {
        return blockSafety;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlockSafety(BlockSafety blockSafety) {
        if (blockSafety == null) {
            throw new NullPointerException("block safety may not be null.");
        }
        this.blockSafety = blockSafety;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocationManipulation getLocationManipulation() {
        return locationManipulation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocationManipulation(LocationManipulation locationManipulation) {
        if (locationManipulation == null) {
            throw new NullPointerException("location manipulation may not be null.");
        }
        this.locationManipulation = locationManipulation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SafeTTeleporter getSafeTTeleporter() {
        return safeTTeleporter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSafeTTeleporter(SafeTTeleporter safeTTeleporter) {
        if (safeTTeleporter == null) {
            throw new NullPointerException("safeTTeleporter may not be null.");
        }
        this.safeTTeleporter = safeTTeleporter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MVConfig getMVConfig() {
        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnsafeCallWrapper getUnsafeCallWrapper() {
        return this.unsafeCallWrapper;
    }


    //TODO: REMOVE THIS STATIC CRAP - START
    private static final Map<String, String> teleportQueue = new HashMap<String, String>();

    /**
     * This method is used to add a teleportation to the teleportQueue.
     *
     * @param teleporter The name of the player that initiated the teleportation.
     * @param teleportee The name of the player that was teleported.
     */
    public static void addPlayerToTeleportQueue(String teleporter, String teleportee) {
        Logging.finest("Adding mapping '%s' => '%s' to teleport queue", teleporter, teleportee);
        teleportQueue.put(teleportee, teleporter);
    }

    /**
     * This method is used to find out who is teleporting a player.
     * @param playerName The teleported player (the teleportee).
     * @return The player that teleported the other one (the teleporter).
     */
    public static String getPlayerTeleporter(String playerName) {
        if (teleportQueue.containsKey(playerName)) {
            String teleportee = teleportQueue.get(playerName);
            teleportQueue.remove(playerName);
            return teleportee;
        }
        return null;
    }
    //TODO: REMOVE THIS STATIC CRAP - END


    // For testing purposes only //

    private File serverFolder = new File(System.getProperty("user.dir"));

    /**
     * This is for unit testing.
     *
     * @param loader The PluginLoader to use.
     * @param description The Description file to use.
     * @param dataFolder The folder that other datafiles can be found in.
     * @param file The location of the plugin.
     */
    public MultiverseCore(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    /**
     * Gets the {@link MVPlayerListener}.
     *
     * @return The {@link MVPlayerListener}.
     */
    public MVPlayerListener getPlayerListener() {
        return this.playerListener;
    }

    /**
     * Gets the {@link MVChatListener}.
     *
     * @return The {@link MVChatListener}.
     */
    public MVChatListener getChatListener() {
        return this.chatListener;
    }

    /**
     * Gets the {@link MVEntityListener}.
     *
     * @return The {@link MVEntityListener}.
     */
    public MVEntityListener getEntityListener() {
        return this.entityListener;
    }

    /**
     * Gets the {@link MVWeatherListener}.
     *
     * @return The {@link MVWeatherListener}.
     */
    public MVWeatherListener getWeatherListener() {
        return this.weatherListener;
    }

    /**
     * Gets the server's root-folder as {@link File}.
     *
     * @return The server's root-folder
     */
    public File getServerFolder() {
        return serverFolder;
    }

    /**
     * Sets this server's root-folder.
     *
     * @param newServerFolder The new server-root
     */
    public void setServerFolder(File newServerFolder) {
        if (!newServerFolder.isDirectory())
            throw new IllegalArgumentException("That's not a folder!");

        this.serverFolder = newServerFolder;
    }
}
