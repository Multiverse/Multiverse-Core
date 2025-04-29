package org.mvplugins.multiverse.core.module;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.MultiverseCommand;
import org.mvplugins.multiverse.core.commands.CoreCommand;
import org.mvplugins.multiverse.core.inject.PluginServiceLocator;
import org.mvplugins.multiverse.core.inject.PluginServiceLocatorFactory;
import org.mvplugins.multiverse.core.inject.binder.PluginBinder;
import org.mvplugins.multiverse.core.listeners.CoreListener;
import org.mvplugins.multiverse.core.utils.REPatterns;

/**
 * Common plugin class for all Multiverse plugins.
 */
@Contract
public abstract class MultiverseModule extends JavaPlugin {

    protected PluginServiceLocator serviceLocator;

    @Inject
    protected Provider<MVCommandManager> commandManagerProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        super.onEnable();
        MultiverseModulesRegistry.get().registerMultiverseModule(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        super.onDisable();
        MultiverseModulesRegistry.get().unregisterMultiverseModule(this);
    }

    /**
     * The minimum version that this plugin is compatible with Multiverse-Core.
     *
     * @return The version number.
     */
    public abstract double getTargetCoreVersion();

    /**
     * Gets the {@link PluginServiceLocator} for this plugin.
     *
     * @return The {@link PluginServiceLocator}
     */
    public PluginServiceLocator getServiceLocator() {
        return serviceLocator;
    }

    protected double getVersionAsNumber() {
        String[] split = REPatterns.DOT.split(this.getDescription().getVersion());
        if (split.length < 2) {
            return -1;
        }
        return Double.parseDouble(split[0] + "." + split[1]);
    }

    /**
     * Initializes the dependency injection for this plugin.
     *
     * @param pluginBinder  The plugin binder
     */
    protected void initializeDependencyInjection(MultiverseModuleBinder<? extends MultiverseModule> pluginBinder) {
        PluginServiceLocator coreServiceLocator = Option.of(MultiverseModulesRegistry.get().getCore())
                .map(MultiverseCore::getServiceLocator)
                .getOrNull();

        serviceLocator = PluginServiceLocatorFactory.get()
                .registerPlugin(pluginBinder, coreServiceLocator)
                .flatMap(PluginServiceLocator::enable)
                .getOrElseThrow(exception -> {
                    Logging.severe("Failed to initialize dependency injection!");
                    getServer().getPluginManager().disablePlugin(this);
                    return new RuntimeException(exception);
                });
    }

    protected void shutdownDependencyInjection() {
        Option.of(serviceLocator)
                .peek(PluginServiceLocator::disable)
                .peek(ignore -> serviceLocator = null);
    }

    /**
     * Function to Register all the Events needed.
     */
    protected void registerEvents(Class<? extends Listener> listenerClass) {
        var pluginManager = getServer().getPluginManager();
        Try.run(() -> serviceLocator.getAllServices(listenerClass).forEach(
                        listener -> pluginManager.registerEvents(listener, this)))
                .onFailure(e -> {
                    throw new RuntimeException("Failed to register listeners. Terminating...", e);
                });
    }

    /**
     * Register all commands to Command Manager.
     */
    protected void registerCommands(Class<? extends MultiverseCommand> commandClass) {
        Try.of(() -> commandManagerProvider.get())
                .andThenTry(commandManager -> {
                    commandManager.registerAllCommands(serviceLocator.getAllServices(commandClass));
                })
                .onFailure(e -> {
                    Logging.severe("Failed to register commands");
                    e.printStackTrace();
                });
    }

    /**
     * Register locales.
     */
    protected void setUpLocales() {
        Try.of(() -> commandManagerProvider.get())
                .mapTry(MVCommandManager::getLocales)
                .andThen(pluginLocales -> {
                    pluginLocales.addFileResClassLoader(this);
                    pluginLocales.addBundleClassLoader(this.getClassLoader());
                    pluginLocales.addMessageBundles(this.getDescription().getName().toLowerCase());
                })
                .onFailure(e -> {
                    Logging.severe("Failed to register locales");
                    e.printStackTrace();
                });
    }
}
