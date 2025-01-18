package org.mvplugins.multiverse.core.api.destination;

import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;

import java.util.Collection;

/**
 * Provides destinations for teleportation.
 * @since 5.0
 */
@Contract
public interface DestinationsProvider {
    /**
     * Adds a destination to the provider.
     *
     * @param destination The destination.
     * @since 5.0
     */
    void registerDestination(@NotNull Destination<?, ?> destination);

    /**
     * Converts a destination string to a destination object.
     *
     * @param destinationString The destination string.
     * @return The destination object, or null if invalid format.
     * @since 5.0
     */
    @NotNull Option<DestinationInstance<?, ?>> parseDestination(@NotNull String destinationString);

    /**
     * Gets a destination by its identifier.
     *
     * @param identifier The identifier.
     * @return The destination, or null if not found.
     * @since 5.0
     */
    @Nullable Destination<?, ?> getDestinationById(@Nullable String identifier);

    /**
     * Gets all registered destinations.
     *
     * @return A collection of destinations.
     * @since 5.0
     */
    @NotNull Collection<Destination<?, ?>> getDestinations();
}
