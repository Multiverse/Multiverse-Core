package com.onarandombox.MultiverseCore.inject;

import com.onarandombox.MultiverseCore.inject.binder.JavaPluginBinder;
import com.onarandombox.MultiverseCore.inject.binder.PluginBinder;
import com.onarandombox.MultiverseCore.inject.binder.ServerBinder;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.bukkit.plugin.Plugin;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.internal.ServiceLocatorFactoryImpl;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        var systemServiceLocator = Option.of(factory.create("system")).toTry();

        var features = systemServiceLocator
                .mapTry(locator -> locator.getAllServices(InjectionFeature.class));

        return systemServiceLocator
                .flatMap(systemLocator -> createServerServiceLocator(factory, systemLocator))
                .map(serverLocator -> new PluginInjection(pluginBinder, factory, serverLocator))
                .flatMap(pluginInjection -> features.flatMap(pluginInjection::load));
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

    private final PluginBinder<?> pluginBinder;
    private final Plugin plugin;
    private final ServiceLocator pluginServiceLocator;

    private PluginInjection(
            @NotNull PluginBinder<?> pluginBinder,
            @NotNull ServiceLocatorFactory serviceLocatorFactory,
            @NotNull ServiceLocator serverServiceLocator
    ) {
        this.pluginBinder = pluginBinder;
        plugin = pluginBinder.getPlugin();
        pluginServiceLocator = serviceLocatorFactory.create(plugin.getName(), serverServiceLocator);
    }

    private Try<ServiceLocator> load(List<InjectionFeature> features) {
        return Try.runRunnable(() -> ServiceLocatorUtilities.bind(pluginServiceLocator, pluginBinder))
                .flatMap(ignored -> populatePluginServiceLocator(pluginServiceLocator, plugin))
                .andThenTry(() -> loadAncillaryServices(features));
    }

    private void loadAncillaryServices(List<InjectionFeature> features) throws MultiException {
        features.forEach(feature -> feature.preServicesCreation(pluginServiceLocator));
        pluginServiceLocator.getAllServices(PluginService.class);
        features.forEach(feature -> feature.postServicesCreation(pluginServiceLocator));
    }

    @NotNull
    private static Try<ServiceLocator> createServerServiceLocator(
            @NotNull ServiceLocatorFactory serviceLocatorFactory,
            @NotNull ServiceLocator systemServiceLocator
    ) {
        return Try.of(() -> serviceLocatorFactory.create("server", systemServiceLocator))
                .andThenTry(locator -> ServiceLocatorUtilities.bind(locator, new ServerBinder()));
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
                            .populate(new ClasspathDescriptorFileFinder(
                                    plugin.getClass().getClassLoader(),
                                    plugin.getName()));
                    return serviceLocator;
                });
    }
}
