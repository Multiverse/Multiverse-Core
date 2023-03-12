package com.onarandombox.MultiverseCore.inject.features;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
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
class BaseCommandFeature implements InjectionFeature {

    @Override
    public void postServicesCreation(ServiceLocator locator) {
        ServiceLocatorUtilities.bind(locator, AutoRegistrationBinder.with(BaseCommandAutoRegistration.class));
    }

    @Service
    private static final class BaseCommandAutoRegistration extends AbstractAutoRegistration<BaseCommand> {

        private final @NotNull Provider<BukkitCommandManager> commandManagerProvider;

        @Inject
        private BaseCommandAutoRegistration(
                @NotNull Provider<BukkitCommandManager> commandManagerProvider,
                @NotNull Provider<RegistrationFilter> filterProvider
        ) {
            super(filterProvider, BaseCommand.class);
            this.commandManagerProvider = commandManagerProvider;
        }

        private BukkitCommandManager getCommandManager() {
            return commandManagerProvider.get();
        }

        @Override
        protected void register(BaseCommand instance) {
            getCommandManager().registerCommand(instance);
        }
    }
}
