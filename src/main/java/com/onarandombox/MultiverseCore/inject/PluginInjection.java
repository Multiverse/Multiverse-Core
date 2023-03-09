package com.onarandombox.MultiverseCore.inject;

import com.onarandombox.MultiverseCore.inject.binder.JavaPluginBinder;
import com.onarandombox.MultiverseCore.inject.binder.PluginBinder;
import com.onarandombox.MultiverseCore.inject.binder.ServerBinder;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.bukkit.plugin.Plugin;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.internal.ServiceLocatorFactoryImpl;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jetbrains.annotations.NotNull;

/**
 * Provides methods to set up dependency injection for plugins.
 * <br/>
 * This class is a wrapper around the HK2 dependency injection library. It provides methods to create a service locators
 * for plugins and to perform injection tasks on plugin instances on enable and disable.
 */
public final class PluginInjection {

    /**
     * Creates a {@link ServiceLocator} for the given plugin binder.
     * <br/>
     * The service locator is populated with the plugin instance and all dependencies that are bound in the
     * {@link PluginBinder} of the plugin. Plugins must implement their own {@link PluginBinder} to use this class.
     * {@link JavaPluginBinder} is provided as a convenience for plugins that extend
     * {@link org.bukkit.plugin.java.JavaPlugin}.
     *
     * @param pluginBinder The plugin binder for the plugin.
     * @return
     */
    @NotNull
    public static Try<ServiceLocator> createServiceLocator(@NotNull PluginBinder<?> pluginBinder) {
        var factory = new ServiceLocatorFactoryImpl();
        return createServerServiceLocator(factory)
                .map(locator -> new PluginInjection(factory, locator))
                .flatMap(pluginInjection -> pluginInjection.load(pluginBinder));
    }

    /**
     * Performs necessary steps to enable dependency injection for the given plugin.
     * <br/>
     * This method will inject all dependencies into the plugin instance and call the {@code @PostConstruct} methods.
     *
     * @param plugin The plugin to enable dependency injection for.
     * @param pluginServiceLocator The service locator for the plugin.
     */
    public static void enable(@NotNull Plugin plugin, @NotNull ServiceLocator pluginServiceLocator) {
        pluginServiceLocator.inject(plugin);
        pluginServiceLocator.postConstruct(plugin);
    }

    /**
     * Performs necessary steps to disable dependency injection for the given plugin.
     * <br/>
     * This method will call the {@code @PreDestroy} methods and shutdown the service locator.
     *
     * @param plugin The plugin to disable dependency injection for.
     * @param pluginServiceLocator The service locator for the plugin.
     */
    public static void disable(@NotNull Plugin plugin, @NotNull ServiceLocator pluginServiceLocator) {
        pluginServiceLocator.preDestroy(plugin);
        pluginServiceLocator.shutdown();
    }

    private final ServiceLocatorFactory serviceLocatorFactory;
    private final ServiceLocator serverServiceLocator;

    private PluginInjection(
            @NotNull ServiceLocatorFactory serviceLocatorFactory,
            @NotNull ServiceLocator serverServiceLocator
    ) {
        this.serviceLocatorFactory = serviceLocatorFactory;
        this.serverServiceLocator = serverServiceLocator;
    }

    private Try<ServiceLocator> load(@NotNull PluginBinder<?> pluginBinder) {
        var plugin = pluginBinder.getPlugin();

        return Option.of(serviceLocatorFactory.create(plugin.getName(), serverServiceLocator))
                .toTry()
                .andThenTry(pluginServiceLocator -> ServiceLocatorUtilities.bind(pluginServiceLocator, pluginBinder))
                .flatMap(pluginServiceLocator -> populatePluginServiceLocator(pluginServiceLocator, plugin));
    }

    @NotNull
    private static Try<ServiceLocator> createServerServiceLocator(
            @NotNull ServiceLocatorFactory serviceLocatorFactory
    ) {
        return createSystemServiceLocator(serviceLocatorFactory)
                .map(systemServiceLocator -> serviceLocatorFactory.create("server", systemServiceLocator))
                .andThenTry(serverServiceLocator ->
                        ServiceLocatorUtilities.bind(serverServiceLocator, new ServerBinder()));
    }

    @NotNull
    private static Try<ServiceLocator> createSystemServiceLocator(
            @NotNull ServiceLocatorFactory serviceLocatorFactory
    ) {
        ServiceLocator serviceLocator = serviceLocatorFactory.create("system");

        return Try.of(() -> serviceLocator.getService(DynamicConfigurationService.class))
                .mapTry(dynamicConfigurationService -> {
                    dynamicConfigurationService.getPopulator().populate();
                    return serviceLocator;
                });
    }

    @NotNull
    private static Try<ServiceLocator> populatePluginServiceLocator(
            @NotNull ServiceLocator serviceLocator,
            @NotNull Plugin plugin
    ) {
        return Try.of(() -> serviceLocator.getService(DynamicConfigurationService.class))
                .mapTry(dynamicConfigurationService -> {
                    dynamicConfigurationService
                            .getPopulator()
                            .populate(new ClasspathDescriptorFileFinder(plugin.getClass().getClassLoader()));
                    return serviceLocator;
                });
    }
}
