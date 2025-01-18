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
import org.mvplugins.multiverse.core.api.ServiceProvider;
import org.mvplugins.multiverse.core.inject.binder.PluginBinder;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class PluginServiceLocator implements ServiceProvider {

    private final PluginBinder<?> pluginBinder;
    private final ServiceLocator serviceLocator;

    PluginServiceLocator(@NotNull PluginBinder<?> pluginBinder, @NotNull ServiceLocator serviceLocator) {
        this.pluginBinder = pluginBinder;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public @NotNull Try<PluginServiceLocator> enable() {
        return bindPlugin()
                .flatMap(ignore -> populateServices())
                .flatMap(ignore -> injectPlugin())
                .mapTry(ignore -> this);
    }

    @Override
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

    @Override
    public @Nullable <T> T getService(@NotNull Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
        return serviceLocator.getService(contractOrImpl, qualifiers);
    }

    @Override
    public @Nullable <T> T getActiveService(@NotNull Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
        var handle = serviceLocator.getServiceHandle(contractOrImpl, qualifiers);
        if (handle != null && handle.isActive()) {
            return handle.getService();
        }
        return null;
    }

    @Override
    public @NotNull <T> List<T> getAllServices(
            @NotNull Class<T> contractOrImpl,
            Annotation... qualifiers) throws MultiException {
        return serviceLocator.getAllServices(contractOrImpl, qualifiers);
    }

    @Override
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
