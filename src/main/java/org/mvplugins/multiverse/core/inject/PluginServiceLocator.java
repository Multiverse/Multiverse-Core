package org.mvplugins.multiverse.core.inject;

import io.vavr.control.Try;
import org.bukkit.plugin.Plugin;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.inject.binder.PluginBinder;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public final class PluginServiceLocator {

    private final PluginBinder<?> pluginBinder;
    private final ServiceLocator serviceLocator;

    PluginServiceLocator(@NotNull PluginBinder<?> pluginBinder, @NotNull ServiceLocator serviceLocator) {
        this.pluginBinder = pluginBinder;
        this.serviceLocator = serviceLocator;
    }

    /**
     * Starts and inject the plugin classes.
     *
     * @return The plugin service locator
     */
    public @NotNull Try<PluginServiceLocator> enable() {
        return bindPlugin()
                .flatMap(ignore -> populateServices())
                .flatMap(ignore -> injectPlugin())
                .mapTry(ignore -> this);
    }

    /**
     * Shut down the service locator
     */
    public void disable() {
        serviceLocator.preDestroy(pluginBinder.getPlugin());
    }

    @NotNull
    private Try<Void> bindPlugin() {
        return Try.of(() -> {
            ServiceLocatorUtilities.bind(serviceLocator, pluginBinder);
            return null;
        });
    }

    @NotNull
    private Try<Void> populateServices() {
        Plugin plugin = pluginBinder.getPlugin();
        return Try.of(() -> serviceLocator.getService(DynamicConfigurationService.class))
                .mapTry(dynamicConfigurationService -> {
                    dynamicConfigurationService.getPopulator().populate(new ClasspathDescriptorFileFinder(
                            plugin.getClass().getClassLoader(),
                            plugin.getName())
                    );
                    return null;
                });
    }

    @NotNull
    private Try<Void> injectPlugin() {
        return Try.of(() -> {
            serviceLocator.inject(pluginBinder.getPlugin());
            serviceLocator.postConstruct(pluginBinder.getPlugin());
            return null;
        });
    }

    /**
     * Gets the best service from this plugin that implements the given contract or has the given implementation.
     * Service will be instantiated if it is not already instantiated.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers     The set of qualifiers that must match this service definition
     * @param <T>            The type of the contract to get
     * @return An instance of the contract or impl if it is a service, null otherwise
     * @throws MultiException if there was an error during service lookup
     */
    public @Nullable <T> T getService(@NotNull Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
        return serviceLocator.getService(contractOrImpl, qualifiers);
    }

    /**
     * Gets the best active service from this plugin that implements the given contract or has the given implementation.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers     The set of qualifiers that must match this service definition
     * @param <T>            The type of the contract to get
     * @return An instance of the contract or impl if it is a service and is already instantiated, null otherwise
     * @throws MultiException if there was an error during service lookup
     */
    public @Nullable <T> T getActiveService(@NotNull Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
        var handle = serviceLocator.getServiceHandle(contractOrImpl, qualifiers);
        if (handle != null && handle.isActive()) {
            return handle.getService();
        }
        return null;
    }

    /**
     * Gets all services from this plugin that implement the given contract or have the given implementation and have
     * the provided qualifiers. Services will be instantiated if it is not already instantiated.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers     The set of qualifiers that must match this service definition
     * @param <T>            The type of the contract to get
     * @return A list of services implementing this contract or concrete implementation. May not return null, but may
     * return an empty list.
     * @throws MultiException if there was an error during service lookup
     */
    public @NotNull <T> List<T> getAllServices(
            @NotNull Class<T> contractOrImpl,
            Annotation... qualifiers) throws MultiException {
        return serviceLocator.getAllServices(contractOrImpl, qualifiers);
    }

    /**
     * Gets all services from this plugin that implement the given contract or have the given implementation and have
     * the provided qualifiers.
     *
     * @param contractOrImpl The contract or concrete implementation to get the best instance of
     * @param qualifiers     The set of qualifiers that must match this service definition
     * @param <T>            The type of the contract to get
     * @return A list of services already instantiated implementing this contract or concrete implementation.
     * May not return null, but may return an empty list.
     * @throws MultiException if there was an error during service lookup
     */
    public @NotNull <T> List<T> getAllActiveServices(
            @NotNull Class<T> contractOrImpl,
            Annotation... qualifiers) throws MultiException {
        var handles = serviceLocator.getAllServiceHandles(contractOrImpl, qualifiers);
        return handles.stream()
                .filter(ServiceHandle::isActive)
                .map(ServiceHandle::getService)
                .collect(Collectors.toList());
    }

    @NotNull
    ServiceLocator getServiceLocator() {
        return serviceLocator;
    }
}
