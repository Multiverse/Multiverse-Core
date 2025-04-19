package org.mvplugins.multiverse.core.inject;

import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.internal.ServiceLocatorFactoryImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.inject.binder.PluginBinder;

import java.util.Objects;

/**
 * A factory for creating and managing service locators for Multiverse plugins.
 * This class handles the initialization and shutdown of service locators
 * and provides methods for registering plugins.
 */
public final class PluginServiceLocatorFactory {

    private static PluginServiceLocatorFactory instance = null;

    /**
     * Retrieves the singleton instance of the PluginServiceLocatorFactory.
     * Initializes the factory if it has not been initialized.
     *
     * @return The singleton instance of PluginServiceLocatorFactory.
     */
    public static PluginServiceLocatorFactory get() {
        if (instance == null) {
            instance = new PluginServiceLocatorFactory();
            instance.init().getOrElseThrow(exception -> new IllegalStateException(exception));
        }
        return instance;
    }

    private final ServiceLocatorFactory serviceLocatorFactory;
    private ServiceLocator baseServiceLocator;

    private PluginServiceLocatorFactory() {
        this.serviceLocatorFactory = new ServiceLocatorFactoryImpl();
    }

    /**
     * Initializes the service locator system.
     *
     * @return A Try containing Void if initialization is successful, or an exception if it fails.
     */
    @NotNull
    private Try<Void> init() {
        return createSystemServiceLocator()
                .flatMap(this::createServerServiceLocator)
                .mapTry(locator -> {
                    baseServiceLocator = locator;
                    return null;
                });
    }

    /**
     * Stops injection of all Multiverse plugins and resets the factory.
     */
    public void shutdown() {
        baseServiceLocator.shutdown();
        baseServiceLocator = null;
        instance = null;
    }

    /**
     * Creates the system-level service locator.
     *
     * @return A Try containing the created system service locator.
     */
    @NotNull
    private Try<ServiceLocator> createSystemServiceLocator() {
        return Try.of(() -> serviceLocatorFactory.create("system"))
                .mapTry(systemServiceLocator -> {
                    systemServiceLocator.getService(DynamicConfigurationService.class)
                            .getPopulator()
                            .populate();
                    return systemServiceLocator;
                });
    }

    /**
     * Creates the server-level service locator using the provided system service locator.
     *
     * @param systemServiceLocator The system service locator.
     * @return A Try containing the created server service locator.
     */
    @NotNull
    private Try<ServiceLocator> createServerServiceLocator(@NotNull ServiceLocator systemServiceLocator) {
        return Try.of(() -> serviceLocatorFactory.create("server", systemServiceLocator))
                .mapTry(locator -> {
                    ServiceLocatorUtilities.bind(locator, new ServerBinder());
                    return locator;
                });
    }

    /**
     * Registers a plugin and creates a service locator for it using the base service locator.
     *
     * @param pluginBinder The binder for the plugin.
     * @return A Try containing the created PluginServiceLocator.
     * @throws NullPointerException if the factory has not been initialized.
     */
    public <T extends Plugin> Try<PluginServiceLocator> registerPlugin(@NotNull PluginBinder<T> pluginBinder) {
        Objects.requireNonNull(baseServiceLocator, "PluginServiceLocatorFactory has not been initialized.");
        return createPluginServiceLocator(pluginBinder, baseServiceLocator);
    }

    /**
     * Registers a plugin using an existing parent service locator.
     *
     * @param pluginBinder The binder for the plugin.
     * @param parentServiceLocator The parent service locator.
     * @return A Try containing the created PluginServiceLocator.
     */
    public <T extends Plugin> Try<PluginServiceLocator> registerPlugin(
            @NotNull PluginBinder<T> pluginBinder,
            @Nullable PluginServiceLocator parentServiceLocator) {
        if (parentServiceLocator == null) {
            return registerPlugin(pluginBinder);
        }
        return createPluginServiceLocator(pluginBinder, parentServiceLocator.getServiceLocator());
    }

    /**
     * Creates a service locator for a specific plugin.
     *
     * @param pluginBinder The binder for the plugin.
     * @param parentServiceLocator The parent service locator.
     * @return A Try containing the created PluginServiceLocator.
     */
    private <T extends Plugin> Try<PluginServiceLocator> createPluginServiceLocator(
            @NotNull PluginBinder<T> pluginBinder,
            @Nullable ServiceLocator parentServiceLocator) {
        return Try.of(() -> new PluginServiceLocator(
                pluginBinder,
                serviceLocatorFactory.create(pluginBinder.getPlugin().getName(), parentServiceLocator)));
    }

    /**
     * A binder for binding server-related services such as the Bukkit server and PluginManager.
     */
    private static final class ServerBinder extends AbstractBinder {
        /**
         * Configures bindings for the Bukkit server and plugin manager.
         */
        @Override
        protected void configure() {
            bind(Bukkit.getServer()).to(Server.class);
            bind(Bukkit.getPluginManager()).to(PluginManager.class);
        }
    }
}
