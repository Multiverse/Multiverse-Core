package com.onarandombox.MultiverseCore.utils.permission;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

public class PrefixPermission {

    private final PluginManager pluginManager;
    private final String permissionPrefix;
    private final String description;
    private final PermissionDefault permissionDefault;

    private Permission wildcardPermission;

    public PrefixPermission(String permissionPrefix, String description) {
        this(permissionPrefix, description, PermissionDefault.OP);
    }

    public PrefixPermission(String permissionPrefix, String description, PermissionDefault permissionDefault) {
        this.pluginManager = Bukkit.getServer().getPluginManager();
        this.permissionPrefix = permissionPrefix;
        this.description = description;
        this.permissionDefault = permissionDefault;
    }

    public Permission registerPermission(String permissionSuffix) {
        String permissionName = getPermissionName(permissionSuffix);
        Permission permission = pluginManager.getPermission(permissionName);
        if (permission != null) {
            Logging.warning("Permission already registered: " + permission.getName());
            return permission;
        }

        permission = new Permission(permissionName, description, permissionDefault);
        pluginManager.addPermission(permission);
        if (wildcardPermission == null) {
            registerWildcardPermission();
        }
        permission.addParent(wildcardPermission, true);
        pluginManager.recalculatePermissionDefaults(permission);
        pluginManager.recalculatePermissionDefaults(wildcardPermission);
        Logging.finest("Registered permission: " + permission.getName());
        return permission;
    }

    public void registerWildcardPermission() {
        String permissionName = getPermissionName("*");
        wildcardPermission = pluginManager.getPermission(permissionName);
        if (wildcardPermission != null) {
            return;
        }
        wildcardPermission = new Permission(permissionName, description, permissionDefault);
        pluginManager.addPermission(wildcardPermission);
        pluginManager.recalculatePermissionDefaults(wildcardPermission);
    }

    public boolean removePermission(String permissionSuffix) {
        String permissionName = getPermissionName(permissionSuffix);
        Permission permission = pluginManager.getPermission(permissionName);
        if (permission == null) {
            return false;
        }
        try {
            wildcardPermission.getChildren().remove(permission.getName());
            pluginManager.removePermission(permission);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public boolean removeAllPermissions() {
        try {
            if (wildcardPermission != null) {
                wildcardPermission.getChildren().forEach((child, value) -> pluginManager.removePermission(child));
                pluginManager.removePermission(wildcardPermission);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Permission getPermission(String permissionSuffix) {
        return pluginManager.getPermission(permissionPrefix + permissionSuffix);
    }

    public Permission getWildcardPermission() {
        return wildcardPermission;
    }

    public String getPermissionName(String permissionSuffix) {
        return permissionPrefix + permissionSuffix;
    }

    public String getPermissionPrefix() {
        return permissionPrefix;
    }

    public String getDescription() {
        return description;
    }

    public PermissionDefault getPermissionDefault() {
        return permissionDefault;
    }
}
