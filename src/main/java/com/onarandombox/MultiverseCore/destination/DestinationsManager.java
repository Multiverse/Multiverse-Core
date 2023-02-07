package com.onarandombox.MultiverseCore.destination;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

public class DestinationsManager {
    private static final String SEPARATOR = ":";
    private static final String PERMISSION_PREFIX = "multiverse.teleport.";

    private final MultiverseCore plugin;
    private final Map<String, Destination<?>> destinationMap;

    public DestinationsManager(MultiverseCore plugin) {
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

    public Collection<String> suggestDestinations(String deststring) {
        //TODO
        return Collections.emptyList();
    }

    public ParsedDestination<?> parseDestination(String deststring) {
        //TODO Parse destination without ID, such as world and player name

        String[] items = deststring.split(SEPARATOR, 2);
        if (items.length < 2) {
            return null;
        }

        String idStr = items[0];
        String destParams = items[1];

        Destination<?> destination = this.getDestinationById(idStr);
        if (destination == null) {
            return null;
        }

        DestinationInstance destinationInstance = destination.getDestinationInstance(destParams);
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
}
