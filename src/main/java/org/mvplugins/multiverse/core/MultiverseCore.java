/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.api.MVCore;
import org.mvplugins.multiverse.core.commands.CoreCommand;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.listeners.CoreListener;
import org.mvplugins.multiverse.core.inject.PluginServiceLocator;
import org.mvplugins.multiverse.core.inject.PluginServiceLocatorFactory;
import org.mvplugins.multiverse.core.placeholders.MultiverseCorePlaceholders;
import org.mvplugins.multiverse.core.utils.TestingMode;
import org.mvplugins.multiverse.core.utils.metrics.MetricsConfigurator;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.config.NullLocation;
import org.mvplugins.multiverse.core.world.config.SpawnLocation;

/**
 * The implementation of the Multiverse-{@link MVCore}.
 */
@Service
public class MultiverseCore extends JavaPlugin implements MVCore {
    private static final int PROTOCOL = 50;

    private PluginServiceLocatorFactory serviceLocatorFactory;
    private PluginServiceLocator serviceLocator;

    @Inject
    private Provider<MVCoreConfig> configProvider;
    @Inject
    private Provider<WorldManager> worldManagerProvider;
    @Inject
    private Provider<AnchorManager> anchorManagerProvider;
    @Inject
    private Provider<MVCommandManager> commandManagerProvider;
    @Inject
    private Provider<DestinationsProvider> destinationsProviderProvider;
    @Inject
    private Provider<MetricsConfigurator> metricsConfiguratorProvider;
    @Inject
    private Provider<MVEconomist> economistProvider;

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
        // Setup our Logging
        Logging.init(this);

        // Create our DataFolder
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            Logging.severe("Failed to create data folder!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register our config classes
        ConfigurationSerialization.registerClass(NullLocation.class);
        ConfigurationSerialization.registerClass(SpawnLocation.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        initializeDependencyInjection();

        // Load our configs first as we need them for everything else.
        var config = configProvider.get();
        var loadSuccess = config.load().andThenTry(config::save).isSuccess();
        if (!loadSuccess || !config.isLoaded()) {
            Logging.severe("Your configs were not loaded.");
            Logging.severe("Please check your configs and restart the server.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Logging.setShowingConfig(shouldShowConfig());

        // Initialize the worlds
        worldManagerProvider.get().initAllWorlds().andThenTry(() -> {
            // Setup economy here so vault is loaded
            loadEconomist();

            // Init all the other stuff
            loadAnchors();
            registerEvents();
            setUpLocales();
            registerCommands();
            registerDestinations();
            setupMetrics();
            loadPlaceholderApiIntegration();
            saveAllConfigs();
            logEnableMessage();
        }).onFailure(e -> {
            Logging.severe("Failed to multiverse core! Disabling...");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        saveAllConfigs();
        shutdownDependencyInjection();
        Logging.shutdown();
    }

    private void initializeDependencyInjection() {
        serviceLocatorFactory = new PluginServiceLocatorFactory();
        serviceLocator = serviceLocatorFactory.init()
                .flatMap(ignore -> serviceLocatorFactory.registerPlugin(new MultiverseCorePluginBinder(this)))
                .flatMap(PluginServiceLocator::enable)
                .getOrElseThrow(exception -> {
                    Logging.severe("Failed to initialize dependency injection!");
                    getServer().getPluginManager().disablePlugin(this);
                    return new RuntimeException(exception);
                });
    }

    private void shutdownDependencyInjection() {
        if (serviceLocator != null) {
            serviceLocator.disable();
            serviceLocator = null;
        }
        if (serviceLocatorFactory != null) {
            serviceLocatorFactory.shutdown();
            serviceLocatorFactory = null;
        }
    }

    private boolean shouldShowConfig() {
        return !configProvider.get().getSilentStart();
    }

    private void loadEconomist() {
        Try.run(() -> economistProvider.get())
                .onFailure(e -> {
                    Logging.severe("Failed to load economy integration");
                    e.printStackTrace();
                });
    }

    private void loadAnchors() {
        Try.of(() -> anchorManagerProvider.get())
                .onSuccess(AnchorManager::loadAnchors)
                .onFailure(e -> {
                    Logging.severe("Failed to load anchors");
                    e.printStackTrace();
                });
    }

    /**
     * Function to Register all the Events needed.
     */
    private void registerEvents() {
        var pluginManager = getServer().getPluginManager();

        Try.run(() -> serviceLocator.getAllServices(CoreListener.class).forEach(
                listener -> pluginManager.registerEvents(listener, this)))
                .onFailure(e -> {
                    throw new RuntimeException("Failed to register listeners. Terminating...", e);
                });
    }

    /**
     * Register Multiverse-Core commands to Command Manager.
     */
    private void registerCommands() {
        Try.of(() -> commandManagerProvider.get())
                .andThenTry(commandManager -> serviceLocator.getAllServices(CoreCommand.class)
                        .forEach(commandManager::registerCommand))
                .onFailure(e -> {
                    Logging.severe("Failed to register commands");
                    e.printStackTrace();
                });
    }

    /**
     * Register locales.
     */
    private void setUpLocales() {
        Try.of(() -> commandManagerProvider.get())
                .mapTry(MVCommandManager::getLocales)
                .andThen(pluginLocales -> {
                    pluginLocales.addFileResClassLoader(this);
                    pluginLocales.addMessageBundles("multiverse-core");
                })
                .onFailure(e -> {
                    Logging.severe("Failed to register locales");
                    e.printStackTrace();
                });
    }

    /**
     * Register all the destinations.
     */
    private void registerDestinations() {
        Try.of(() -> destinationsProviderProvider.get())
                .andThenTry(destinationsProvider -> {
                    serviceLocator.getAllServices(Destination.class)
                            .forEach(destinationsProvider::registerDestination);
                })
                .onFailure(e -> {
                    Logging.severe("Failed to register destinations");
                    e.printStackTrace();
                });
    }

    /**
     * Setup bstats Metrics.
     */
    private void setupMetrics() {
        if (TestingMode.isDisabled()) {
            // Load metrics
            Try.of(() -> metricsConfiguratorProvider.get())
                    .onFailure(e -> {
                        Logging.severe("Failed to setup metrics");
                        e.printStackTrace();
                    });
        } else {
            Logging.info("Metrics are disabled in testing mode.");
        }
    }

    /**
     * Logs the enable message.
     */
    private void logEnableMessage() {
        Logging.config("Version %s (API v%s) Enabled - By %s", this.getDescription().getVersion(), PROTOCOL, getAuthors());

        if (configProvider.get().isShowingDonateMessage()) {
            Logging.config("Help dumptruckman keep this project alive. Become a patron! https://www.patreon.com/dumptruckman");
            Logging.config("One time donations are also appreciated: https://www.paypal.me/dumptruckman");
        }
    }

    private void loadPlaceholderApiIntegration() {
        if (configProvider.get().isRegisterPapiHook()
                && getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Try.run(() -> serviceLocator.getService(MultiverseCorePlaceholders.class))
                    .onFailure(e -> {
                        Logging.severe("Failed to load PlaceholderAPI integration.");
                        e.printStackTrace();
                    });
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
    @Override
    public String getAuthors() {
        List<String> authorsList = this.getDescription().getAuthors();
        if (authorsList.isEmpty()) {
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
    public PluginServiceLocatorFactory getServiceLocatorFactory() {
        return serviceLocatorFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PluginServiceLocator getServiceLocator() {
        return serviceLocator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPluginCount() {
        return this.pluginCount;
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return Logging.getLogger();
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
    public @NotNull FileConfiguration getConfig() {
        MVCoreConfig mvCoreConfig = this.configProvider.get();
        var config = mvCoreConfig.getConfig();
        if (config != null && mvCoreConfig.isLoaded()) {
            return config;
        }

        var loadSuccess = mvCoreConfig.load().isSuccess();
        if (!loadSuccess || !mvCoreConfig.isLoaded()) {
            throw new RuntimeException("Failed to load configs");
        }
        return mvCoreConfig.getConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reloadConfig() {
        this.configProvider.get().load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveConfig() {
        this.configProvider.get().save();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveAllConfigs() {
        // TODO: Make this all Try<Void>
        return configProvider.get().save().isSuccess()
                && worldManagerProvider.get().saveWorldsConfig()
                && anchorManagerProvider.get().saveAnchors();
    }

    /**
     * This is for unit testing ONLY. Do not use this constructor.
     *
     * @param loader      The PluginLoader to use.
     * @param description The Description file to use.
     * @param dataFolder  The folder that other datafiles can be found in.
     * @param file        The location of the plugin.
     */
    public MultiverseCore(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }
}
