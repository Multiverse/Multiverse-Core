package com.onarandombox.MultiverseCore.inject.features;

import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.destination.DestinationsProvider;
import com.onarandombox.MultiverseCore.inject.InjectionFeature;
import com.onarandombox.MultiverseCore.inject.registration.AbstractAutoRegistration;
import com.onarandombox.MultiverseCore.inject.registration.AutoRegistrationBinder;
import com.onarandombox.MultiverseCore.inject.registration.RegistrationFilter;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
class DestinationFeature implements InjectionFeature {

    @Override
    public void postServicesCreation(ServiceLocator locator) {
        ServiceLocatorUtilities.bind(locator, AutoRegistrationBinder.with(DestinationAutoRegistration.class));
    }

    @Service
    private static final class DestinationAutoRegistration extends AbstractAutoRegistration<Destination> {

        private final @NotNull Provider<DestinationsProvider> destinationsProviderProvider;

        @Inject
        private DestinationAutoRegistration(
                @NotNull Provider<DestinationsProvider> destinationsProviderProvider,
                @NotNull Provider<RegistrationFilter> filterProvider
        ) {
            super(filterProvider, Destination.class);
            this.destinationsProviderProvider = destinationsProviderProvider;
        }

        private DestinationsProvider getDestinationsProvider() {
            return destinationsProviderProvider.get();
        }

        @Override
        protected void register(Destination instance) {
            getDestinationsProvider().registerDestination(instance);
        }
    }
}
