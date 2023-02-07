package com.onarandombox.MultiverseCore.destination;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.DestinationInstance;
import com.onarandombox.MultiverseCore.api.Teleporter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DestinationsProvider {
    private static final String SEPARATOR = ":";
    private static final String PERMISSION_PREFIX = "multiverse.teleport.";

    private final MultiverseCore plugin;
    private final Map<String, Destination<?>> destinationMap;

    public DestinationsProvider(MultiverseCore plugin) {
        this.plugin = plugin;
        this.destinationMap = new HashMap<>();
    }

    public void registerDestination(Destination<?> destination) {
        this.destinationMap.put(destination.getIdentifier(), destination);
        this.registerDestinationPerms(destination);
    }

    private void registerDestinationPerms(Destination<?> destination) {
        PluginManager pluginManager = this.plugin.getServer().getPluginManager();
        pluginManager.addPermission(new Permission(PERMISSION_PREFIX + "self." + destination.getIdentifier()));
        pluginManager.addPermission(new Permission(PERMISSION_PREFIX + "other." + destination.getIdentifier()));
    }

    public Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String deststring) {
        return destinationMap.values().stream()
                .filter(destination -> issuer.hasPermission(PERMISSION_PREFIX + "self." + destination.getIdentifier())
                        || issuer.hasPermission(PERMISSION_PREFIX + "other." + destination.getIdentifier()))
                .map(destination -> destination.suggestDestinations(issuer, deststring).stream()
                        .map(s -> destination.getIdentifier() + SEPARATOR + s)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

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

    public Destination<?> getDestinationById(String identifier) {
        return this.destinationMap.get(identifier);
    }

    public void playerTeleport(BukkitCommandIssuer teleporter, Player teleportee, ParsedDestination<?> destination) {
        if (!checkTeleportPermissions(teleporter, teleportee, destination)) {
            return;
        }
        teleport(teleporter, teleportee, destination);
    }

    public void teleport(BukkitCommandIssuer teleporter, Entity teleportee, ParsedDestination<?> destination) {
        Teleporter teleportHandler = destination.getDestination().getTeleporter();
        if (teleportHandler == null) {
            teleportHandler = this.plugin.getSafeTTeleporter();
        }
        teleportHandler.teleport(teleporter, teleportee, destination);
    }

    public boolean checkTeleportPermissions(CommandIssuer teleporter, Entity teleportee, ParsedDestination<?> destination) {
        String permission = PERMISSION_PREFIX
                + (teleportee.equals(teleporter.getIssuer()) ? "self" : "other") + "."
                + destination.getDestination().getIdentifier();
        if (!teleporter.hasPermission(permission)) {
            teleporter.sendMessage("You don't have permission to teleport to this destination.");
            return false;
        }

        //TODO Config whether to use finer permission
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
