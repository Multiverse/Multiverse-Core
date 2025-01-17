package org.mvplugins.multiverse.core.destination;

import java.util.Collection;
import java.util.Collections;

import co.aikar.commands.BukkitCommandIssuer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Destination<D extends Destination<D, T>, T extends DestinationInstance<T, D>> {
    /**
     * Returns the identifier or prefix that is required for this destination.
     *
     * <p>Portals have a prefix of "p" for example and OpenWarp (third party plugin) uses "ow". This is derived from a
     * hash and cannot have duplicate values. Read that as your plugin cannot use 'p' because it's already used.
     * Please check the wiki when adding a custom destination!</p>
     *
     * @return The identifier or prefix that is required for this destination.
     */
    @NotNull String getIdentifier();

    /**
     * Returns the destination instance for the given destination parameters.
     *
     * @param destinationParams The destination parameters. ex: p:MyPortal:nw
     * @return The destination instance, or null if the destination parameters are invalid.
     */
    @Nullable T getDestinationInstance(@Nullable String destinationParams);

    /**
     * Returns a list of possible destinations for the given destination parameters.
     *
     * @param issuer            The command issuer.
     * @param destinationParams The destination parameters. ex: p:MyPortal:nw
     * @return A list of possible destinations.
     * @deprecated Use {@link #suggestDestinationPackets(BukkitCommandIssuer, String)}
     */
    @Deprecated
    @NotNull
    default Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams) {
        return Collections.emptyList();
    }

    /**
     * Returns a list of possible destinations for the given destination parameters.
     *
     * @param issuer            The command issuer.
     * @param destinationParams The destination parameters. ex: p:MyPortal:nw
     * @return A list of possible destinations
     */
    @NotNull
    default Collection<DestinationSuggestionPacket> suggestDestinationPackets(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams) {
        return suggestDestinations(issuer, destinationParams).stream()
                .map(s -> new DestinationSuggestionPacket(s, ""))
                .toList();
    }
}
