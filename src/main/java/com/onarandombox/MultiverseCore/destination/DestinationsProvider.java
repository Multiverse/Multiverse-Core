package com.onarandombox.MultiverseCore.destination;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.DestinationInstance;
import com.onarandombox.MultiverseCore.api.Teleporter;
import com.onarandombox.MultiverseCore.utils.permission.PermissionsRegistrar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides destinations for teleportation.
 */
public class DestinationsProvider {
    private static final String SEPARATOR = ":";

    private final MultiverseCore plugin;
    private final Map<String, Destination<?>> destinationMap;

    /**
     * Creates a new destinations provider.
     *
     * @param plugin The plugin.
     */
    public DestinationsProvider(@NotNull MultiverseCore plugin) {
        this.plugin = plugin;
        this.destinationMap = new HashMap<>();
    }

    /**
     * Adds a destination to the provider.
     *
     * @param destination The destination.
     */
    public void registerDestination(@NotNull Destination<?> destination) {
        this.destinationMap.put(destination.getIdentifier(), destination);
        PermissionsRegistrar.registerDestinationPermissions(destination);
    }

    /**
     * Suggest tab completions for a destination string.
     *
     * @param issuer     The command issuer.
     * @param deststring The current destination string.
     * @return A collection of tab completions.
     */
    public @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer,
                                                           @Nullable String deststring
    ) {
        return destinationMap.values().stream()
                //.filter(destination -> this.plugin.getPlayerActionChecker().canUseDestinationToTeleport(issuer, destination))
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
    public ParsedDestination<?> parseDestination(String destinationString) {
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

    public @NotNull Collection<Destination<?>> getRegisteredDestinations() {
        return Collections.unmodifiableCollection(this.destinationMap.values());
    }

    /**
     * Teleports the teleportee to the destination.
     *
     * @param teleporter    The teleporter.
     * @param teleportee    The teleportee.
     * @param destination   The destination.
     */
    public void playerTeleport(@NotNull BukkitCommandIssuer teleporter,
                               @NotNull Player teleportee,
                               @NotNull ParsedDestination<?> destination
    ) {
        teleport(teleporter, teleportee, destination);
    }

    /**
     * Teleports the teleportee to the destination.
     *
     * @param teleporter    The teleporter.
     * @param teleportee    The teleportee.
     * @param destination   The destination.
     */
    public void teleport(@NotNull BukkitCommandIssuer teleporter,
                         @NotNull Entity teleportee,
                         @NotNull ParsedDestination<?> destination
    ) {
        Teleporter teleportHandler = destination.getDestination().getTeleporter();
        if (teleportHandler == null) {
            teleportHandler = this.plugin.getSafeTTeleporter();
        }
        teleportHandler.teleport(teleporter, teleportee, destination);
    }
}
