package org.mvplugins.multiverse.core.destination;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import jakarta.inject.Inject;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.Destination;
import org.mvplugins.multiverse.core.api.DestinationInstance;

/**
 * Provides destinations for teleportation.
 */
@Service
public class DestinationsProvider {
    private static final String SEPARATOR = ":";
    private static final String PERMISSION_PREFIX = "multiverse.teleport.";

    private final PluginManager pluginManager;
    private final Map<String, Destination<?>> destinationMap;

    @Inject
    DestinationsProvider(@NotNull PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.destinationMap = new HashMap<>();
    }

    /**
     * Adds a destination to the provider.
     *
     * @param destination The destination.
     */
    public void registerDestination(@NotNull Destination<?> destination) {
        this.destinationMap.put(destination.getIdentifier(), destination);
        this.registerDestinationPerms(destination);
    }

    private void registerDestinationPerms(@NotNull Destination<?> destination) {
        pluginManager.addPermission(new Permission(PERMISSION_PREFIX + "self." + destination.getIdentifier()));
        pluginManager.addPermission(new Permission(PERMISSION_PREFIX + "other." + destination.getIdentifier()));
    }

    /**
     * Suggest tab completions for a destination string.
     *
     * @param issuer     The command issuer.
     * @param deststring The current destination string.
     * @return A collection of tab completions.
     */
    public @NotNull Collection<String> suggestDestinations(
            @NotNull BukkitCommandIssuer issuer,
            @Nullable String deststring) {
        return destinationMap.values().stream()
                .filter(destination -> issuer.hasPermission(PERMISSION_PREFIX + "self." + destination.getIdentifier())
                        || issuer.hasPermission(PERMISSION_PREFIX + "other." + destination.getIdentifier()))
                .map(destination -> destination.suggestDestinations(issuer, deststring).stream()
                        .map(s -> destination.getIdentifier() + SEPARATOR + s)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Converts a destination string to a destination object.
     *
     * @param destinationString The destination string.
     * @return The destination object, or null if invalid format.
     */
    @Nullable
    public ParsedDestination<?> parseDestination(@NotNull String destinationString) {
        String[] items = destinationString.split(SEPARATOR, 2);

        String idString = items[0];
        String destinationParams;
        Destination<?> destination;

        if (items.length < 2) {
            // Assume world destination
            destination = this.getDestinationById("w");
            destinationParams = items[0];
        } else {
            destination = this.getDestinationById(idString);
            destinationParams = items[1];
        }

        if (destination == null) {
            return null;
        }

        DestinationInstance destinationInstance = destination.getDestinationInstance(destinationParams);
        if (destinationInstance == null) {
            return null;
        }

        return new ParsedDestination<>(destination, destinationInstance);
    }

    /**
     * Gets a destination by its identifier.
     *
     * @param identifier The identifier.
     * @return The destination, or null if not found.
     */
    public @Nullable Destination<?> getDestinationById(@Nullable String identifier) {
        return this.destinationMap.get(identifier);
    }

    /**
     * Gets all registered destinations.
     *
     * @return A collection of destinations.
     */
    public @NotNull Collection<Destination<?>> getDestinations() {
        return this.destinationMap.values();
    }
}
