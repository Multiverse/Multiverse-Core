package com.onarandombox.MultiverseCore.destination;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.DestinationInstance;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.api.Teleporter;
import com.onarandombox.MultiverseCore.teleportation.TeleportResult;
import jakarta.inject.Inject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

/**
 * Provides destinations for teleportation.
 */
@Service
public class DestinationsProvider {
    private static final String SEPARATOR = ":";
    private static final String PERMISSION_PREFIX = "multiverse.teleport.";

    private final PluginManager pluginManager;
    private final SafeTTeleporter safeTTeleporter;
    private final Map<String, Destination<?>> destinationMap;

    /**
     * Creates a new destinations provider.
     */
    @Inject
    public DestinationsProvider(@NotNull PluginManager pluginManager, @NotNull SafeTTeleporter safeTTeleporter) {
        this.pluginManager = pluginManager;
        this.safeTTeleporter = safeTTeleporter;
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
    public @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer,
                                                           @Nullable String deststring
    ) {
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

    /**
     * Teleports the teleportee to the destination.
     *
     * @param teleporter    The teleporter.
     * @param teleportee    The teleportee.
     * @param destination   The destination.
     */
    public CompletableFuture<TeleportResult> playerTeleportAsync(@NotNull BukkitCommandIssuer teleporter,
                               @NotNull Player teleportee,
                               @NotNull ParsedDestination<?> destination
    ) {
        if (!checkTeleportPermissions(teleporter, teleportee, destination)) {
            return CompletableFuture.completedFuture(TeleportResult.FAIL_PERMISSION);
        }
        return teleportAsync(teleporter, teleportee, destination);
    }

    /**
     * Teleports the teleportee to the destination.
     *
     * @param teleporter    The teleporter.
     * @param teleportee    The teleportee.
     * @param destination   The destination.
     */
    public CompletableFuture<TeleportResult> teleportAsync(@NotNull BukkitCommandIssuer teleporter,
                                                           @NotNull Entity teleportee,
                                                           @NotNull ParsedDestination<?> destination
    ) {
        Teleporter teleportHandler = destination.getDestination().getTeleporter();
        if (teleportHandler == null) {
            teleportHandler = safeTTeleporter;
        }
        return teleportHandler.teleportAsync(teleporter, teleportee, destination);
    }

    /**
     * Checks if the teleporter has permission to teleport the teleportee to the destination.
     *
     * @param teleporter    The teleporter.
     * @param teleportee    The teleportee.
     * @param destination   The destination.
     * @return True if the teleporter has permission, false otherwise.
     */
    public boolean checkTeleportPermissions(CommandIssuer teleporter, Entity teleportee, ParsedDestination<?> destination) {
        // TODO: Move permission checking to a separate class
        String permission = PERMISSION_PREFIX
                + (teleportee.equals(teleporter.getIssuer()) ? "self" : "other") + "."
                + destination.getDestination().getIdentifier();
        if (!teleporter.hasPermission(permission)) {
            teleporter.sendMessage("You don't have permission to teleport to this destination.");
            return false;
        }

        // TODO: Config whether to use finer permission
        String finerPermissionSuffix = destination.getDestinationInstance().getFinerPermissionSuffix();
        if (finerPermissionSuffix == null || finerPermissionSuffix.isEmpty()) {
            return true;
        }

        String finerPermission = permission + "." + finerPermissionSuffix;
        if (!teleporter.hasPermission(finerPermission)) {
            teleporter.sendMessage("You don't have permission to teleport to this destination.");
            return false;
        }

        return true;
    }

    /**
     * Checks if the issuer has permission to teleport to at least one destination.
     *
     * @param issuer The issuer.
     * @return True if the issuer has permission, false otherwise.
     */
    public boolean hasAnyTeleportPermission(CommandIssuer issuer) {
        for (Destination<?> destination : this.destinationMap.values()) {
            String permission = PERMISSION_PREFIX + "self." + destination.getIdentifier();
            if (issuer.hasPermission(permission)) {
                return true;
            }
            permission = PERMISSION_PREFIX + "other." + destination.getIdentifier();
            if (issuer.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
}
