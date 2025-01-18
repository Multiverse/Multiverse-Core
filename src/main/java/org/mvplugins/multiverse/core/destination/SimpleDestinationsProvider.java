package org.mvplugins.multiverse.core.destination;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.api.destination.Destination;
import org.mvplugins.multiverse.core.api.destination.DestinationInstance;
import org.mvplugins.multiverse.core.api.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.permissions.CorePermissions;

@Service
public class SimpleDestinationsProvider implements DestinationsProvider {
    private static final String SEPARATOR = ":";

    private final Map<String, Destination<?, ?>> destinationMap;
    private final CorePermissions corePermissions;

    @Inject
    SimpleDestinationsProvider(@NotNull CorePermissions corePermissions) {
        this.corePermissions = corePermissions;
        this.destinationMap = new HashMap<>();
    }

    @Override
    public void registerDestination(@NotNull Destination<?, ?> destination) {
        this.destinationMap.put(destination.getIdentifier(), destination);
        this.corePermissions.addDestinationPermissions(destination);
    }

    @Override
    public @NotNull Option<DestinationInstance<?, ?>> parseDestination(@NotNull String destinationString) {
        String[] items = destinationString.split(SEPARATOR, 2);

        String idString = items[0];
        String destinationParams;
        Destination<?, ?> destination;

        if (items.length < 2) {
            // Assume world destination
            destination = this.getDestinationById("w");
            destinationParams = items[0];
        } else {
            destination = this.getDestinationById(idString);
            destinationParams = items[1];
        }

        if (destination == null) {
            return Option.none();
        }

        return Option.of(destination.getDestinationInstance(destinationParams));
    }

    @Override
    public @Nullable Destination<?, ?> getDestinationById(@Nullable String identifier) {
        return this.destinationMap.get(identifier);
    }

    @Override
    public @NotNull Collection<Destination<?, ?>> getDestinations() {
        return this.destinationMap.values();
    }
}
