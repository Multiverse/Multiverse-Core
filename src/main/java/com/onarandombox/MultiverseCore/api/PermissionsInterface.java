package com.onarandombox.MultiverseCore.api;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface PermissionsInterface {
    public boolean hasPermission(CommandSender sender, String node, boolean isOpRequired);

    public boolean hasAnyPermission(CommandSender sender, List<String> allPermissionStrings, boolean opRequired);

    public boolean hasAllPermission(CommandSender sender, List<String> allPermissionStrings, boolean opRequired);
}
