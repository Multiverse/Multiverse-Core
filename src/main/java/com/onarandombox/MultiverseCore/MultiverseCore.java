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
import java.lang.annotation.Annotation;
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
import com.onarandombox.MultiverseCore.commands.TeleportCommand;
import com.onarandombox.MultiverseCore.commands.UnloadCommand;
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
import com.onarandombox.MultiverseCore.inject.PluginInjection;
import com.onarandombox.MultiverseCore.listeners.MVChatListener;
import com.onarandombox.MultiverseCore.listeners.MVEntityListener;
import com.onarandombox.MultiverseCore.listeners.MVPlayerListener;
import com.onarandombox.MultiverseCore.listeners.MVPortalListener;
import com.onarandombox.MultiverseCore.listeners.MVWeatherListener;
import com.onarandombox.MultiverseCore.listeners.MVWorldInitListener;
import com.onarandombox.MultiverseCore.listeners.MVWorldListener;
import com.onarandombox.MultiverseCore.utils.MVPermissions;
import com.onarandombox.MultiverseCore.utils.TestingMode;
import com.onarandombox.MultiverseCore.utils.UnsafeCallWrapper;
import com.onarandombox.MultiverseCore.utils.metrics.MetricsConfigurator;
import com.onarandombox.MultiverseCore.world.WorldProperties;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import me.main__.util.SerializationConfig.SerializationConfig;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

/**
 * The implementation of the Multiverse-{@link MVCore}.
 */
@Service
public class MultiverseCore extends JavaPlugin implements MVCore {
    private static final int PROTOCOL = 50;

    // Setup various managers
    private ServiceLocator serviceLocator;
    @Inject
    private Provider<AnchorManager> anchorManager;
    @Inject
    private Provider<BlockSafety> blockSafety;
    @Inject
    private Provider<MVCommandManager> commandManager;
    @Inject
    private Provider<DestinationsProvider> destinationsProvider;
    @Inject
    private Provider<MVEconomist> economist;
    @Inject
    private Provider<LocationManipulation> locationManipulation;
    @Inject
    private Provider<MVPermissions> mvPermissions;
    @Inject
    private Provider<SafeTTeleporter> safeTTeleporter;
    @Inject
    private Provider<UnsafeCallWrapper> unsafeCallWrapper;
    @Inject
    private Provider<MVWorldManager> worldManager;

    // Configurations
    private FileConfiguration multiverseConfig;
    private volatile MultiverseCoreConfiguration config;

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
        initializeDependencyInjection();

        // Load our configs first as we need them for everything else.
        this.loadConfigs();
        if (this.multiverseConfig == null) {
            Logging.severe("Your configs were not loaded.");
            Logging.severe("Please check your configs and restart the server.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Logging.setShowingConfig(!getMVConfig().getSilentStart());

        var worldManager = getMVWorldManager();

        worldManager.getDefaultWorldGenerators();
        worldManager.loadDefaultWorlds();
        worldManager.loadWorlds(true);

        // Now set the firstspawnworld (after the worlds are loaded):
        worldManager.setFirstSpawnWorld(getMVConfig().getFirstSpawnWorld());
        MVWorld firstSpawnWorld = worldManager.getFirstSpawnWorld();
        if (firstSpawnWorld != null) {
            getMVConfig().setFirstSpawnWorld(firstSpawnWorld.getName());
        }

        //Setup economy here so vault is loaded
        // TODO we may need to change MVEconomist to have an enable method or something
        // this.economist = new MVEconomist(this);

        // Init all the other stuff
        getAnchorManager().loadAnchors();
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
        shutdownDependencyInjection();
        Logging.shutdown();
    }

    private void initializeDependencyInjection() {
        serviceLocator = PluginInjection.createServiceLocator(new MultiverseCorePluginBinder(this))
                .andThenTry(locator -> {
                    PluginInjection.enable(this, locator);
                })
                .getOrElseThrow(exception -> {
                    Logging.severe("Failed to initialize dependency injection");
                    getServer().getPluginManager().disablePlugin(this);
                    return new RuntimeException(exception);
                });
    }

    private void shutdownDependencyInjection() {
        if (serviceLocator != null) {
            PluginInjection.disable(this, serviceLocator);
            serviceLocator = null;
        }
    }

    /**
     * Function to Register all the Events needed.
     */
    private void registerEvents() {
        // TODO add automatic listener registration through hk2
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(getService(MVEntityListener.class), this);
        pluginManager.registerEvents(getService(MVPlayerListener.class), this);
        pluginManager.registerEvents(getService(MVChatListener.class), this);
        pluginManager.registerEvents(getService(MVPortalListener.class), this);
        pluginManager.registerEvents(getService(MVWeatherListener.class), this);
        pluginManager.registerEvents(getService(MVWorldListener.class), this);
        pluginManager.registerEvents(getService(MVWorldInitListener.class), this);
    }

    /**
     * Register Multiverse-Core commands to Command Manager.
     */
    private void registerCommands() {
        var commandManager = getMVCommandManager();
        
        commandManager = new MVCommandManager(this);
        commandManager.registerCommand(new CheckCommand(this));
        commandManager.registerCommand(new CloneCommand(this));
        commandManager.registerCommand(new ConfirmCommand(this));
        commandManager.registerCommand(new CreateCommand(this));
        commandManager.registerCommand(new DebugCommand(this));
        commandManager.registerCommand(new DeleteCommand(this));
        commandManager.registerCommand(new ImportCommand(this));
        commandManager.registerCommand(new GameruleCommand(this));
        commandManager.registerCommand(new LoadCommand(this));
        commandManager.registerCommand(new RegenCommand(this));
        commandManager.registerCommand(new ReloadCommand(this));
        commandManager.registerCommand(new RemoveCommand(this));
        commandManager.registerCommand(new TeleportCommand(this));
        commandManager.registerCommand(new UnloadCommand(this));
    }

    /**
     * Register locales
     */
    private void setUpLocales() {
        var commandManager = getMVCommandManager();
        
        commandManager.usePerIssuerLocale(true, true);
        commandManager.getLocales().addFileResClassLoader(this);
        commandManager.getLocales().addMessageBundles("multiverse-core");
    }

    /**
     * Register all the destinations.
     */
    private void registerDestinations() {
        var destinationsProvider = getDestinationsProvider();

        destinationsProvider.registerDestination(new AnchorDestination(this));
        destinationsProvider.registerDestination(new BedDestination());
        destinationsProvider.registerDestination(new CannonDestination(this));
        destinationsProvider.registerDestination(new ExactDestination(this));
        destinationsProvider.registerDestination(new PlayerDestination());
        destinationsProvider.registerDestination(new WorldDestination(this));
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
        return economist.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MVPermissions getMVPerms() {
        return this.mvPermissions.get();
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
        return this.commandManager.get();
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
        return this.destinationsProvider.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MVWorldManager getMVWorldManager() {
        return this.worldManager.get();
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
        getMVWorldManager().loadWorldConfig(new File(getDataFolder(), "worlds.yml"));

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
        return this.saveMVConfig() && getMVWorldManager().saveWorldsConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnchorManager getAnchorManager() {
        return this.anchorManager.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockSafety getBlockSafety() {
        return blockSafety.get();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Use dependency injection instead.
     */
    @Override
    @Deprecated
    public void setBlockSafety(BlockSafety blockSafety) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public LocationManipulation getLocationManipulation() {
        return locationManipulation.get();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Use dependency injection instead.
     */
    @Override
    @Deprecated
    public void setLocationManipulation(LocationManipulation locationManipulation) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public SafeTTeleporter getSafeTTeleporter() {
        return safeTTeleporter.get();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated Use dependency injection instead.
     */
    @Override
    @Deprecated
    public void setSafeTTeleporter(SafeTTeleporter safeTTeleporter) {}

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
        return this.unsafeCallWrapper.get();
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
        return getService(MVPlayerListener.class);
    }

    /**
     * Gets the {@link MVChatListener}.
     *
     * @return The {@link MVChatListener}.
     */
    public MVChatListener getChatListener() {
        return getService(MVChatListener.class);
    }

    /**
     * Gets the {@link MVEntityListener}.
     *
     * @return The {@link MVEntityListener}.
     */
    public MVEntityListener getEntityListener() {
        return getService(MVEntityListener.class);
    }

    /**
     * Gets the {@link MVWeatherListener}.
     *
     * @return The {@link MVWeatherListener}.
     */
    public MVWeatherListener getWeatherListener() {
        return getService(MVWeatherListener.class);
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

    /**
     * Gets the best service from this plugin that implements the given contract or has the given implementation.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers The set of qualifiers that must match this service definition
     * @return An instance of the contract or impl. May return null if there is no provider that provides the given
     * implementation or contract
     */
    @Nullable
    public <T> T getService(@NotNull Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
        return serviceLocator.getService(contractOrImpl, qualifiers);
    }
}
