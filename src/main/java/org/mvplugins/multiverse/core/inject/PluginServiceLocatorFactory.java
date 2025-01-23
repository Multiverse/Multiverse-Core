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

public final class PluginServiceLocatorFactory {

    private final ServiceLocatorFactory serviceLocatorFactory;
    private ServiceLocator baseServiceLocator;

    public PluginServiceLocatorFactory() {
        this.serviceLocatorFactory = new ServiceLocatorFactoryImpl();
    }

    @NotNull
    public Try<Void> init() {
        return createSystemServiceLocator()
                .flatMap(this::createServerServiceLocator)
                .mapTry(locator -> {
                    baseServiceLocator = locator;
                    return null;
                });
    }

    public void shutdown() {
        baseServiceLocator.shutdown();
    }

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

    @NotNull
    private Try<ServiceLocator> createServerServiceLocator(@NotNull ServiceLocator systemServiceLocator) {
        return Try.of(() -> serviceLocatorFactory.create("server", systemServiceLocator))
                .mapTry(locator -> {
                    ServiceLocatorUtilities.bind(locator, new ServerBinder());
                    return locator;
                });
    }

    public <T extends Plugin> Try<PluginServiceLocator> registerPlugin(@NotNull PluginBinder<T> pluginBinder) {
        return createPluginServiceLocator(pluginBinder, baseServiceLocator);
    }

    public <T extends Plugin> Try<PluginServiceLocator> registerPlugin(
            @NotNull PluginBinder<T> pluginBinder,
            @NotNull PluginServiceLocator parentServiceLocator) {
        return createPluginServiceLocator(pluginBinder, parentServiceLocator.getServiceLocator());
    }

    private <T extends Plugin> Try<PluginServiceLocator> createPluginServiceLocator(
            @NotNull PluginBinder<T> pluginBinder,
            @Nullable ServiceLocator parentServiceLocator) {
        return Try.of(() -> new PluginServiceLocator(
                pluginBinder,
                serviceLocatorFactory.create(pluginBinder.getPlugin().getName(), parentServiceLocator)));
    }

    private static final class ServerBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(Bukkit.getServer()).to(Server.class);
            bind(Bukkit.getPluginManager()).to(PluginManager.class);
        }
    }
}
