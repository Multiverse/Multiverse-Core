package com.onarandombox.MultiverseCore.inject.features;

import com.onarandombox.MultiverseCore.inject.InjectionFeature;
import com.onarandombox.MultiverseCore.inject.registration.AbstractAutoRegistration;
import com.onarandombox.MultiverseCore.inject.registration.AutoRegistrationBinder;
import com.onarandombox.MultiverseCore.inject.registration.RegistrationFilter;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
class ListenerFeature implements InjectionFeature {

    @Override
    public void postServicesCreation(ServiceLocator locator) {
        ServiceLocatorUtilities.bind(locator, AutoRegistrationBinder.with(ListenerAutoRegistration.class));
    }

    @Service
    private static final class ListenerAutoRegistration extends AbstractAutoRegistration<Listener> {

        private final @NotNull Provider<Plugin> pluginProvider;

        @Inject
        private ListenerAutoRegistration(
                @NotNull Provider<Plugin> pluginProvider,
                @NotNull Provider<RegistrationFilter> filterProvider
        ) {
            super(filterProvider, Listener.class);
            this.pluginProvider = pluginProvider;
        }

        private Plugin getPlugin() {
            return pluginProvider.get();
        }

        @Override
        protected void register(Listener instance) {
            getPlugin().getServer().getPluginManager().registerEvents(instance, getPlugin());
        }
    }
}
