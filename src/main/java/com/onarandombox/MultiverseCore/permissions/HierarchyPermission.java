package com.onarandombox.MultiverseCore.permissions;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionDefault;

/**
 * Provides improved parent-child relationships for permissions. This is the standard implementation of {@link PermissionNode}.
 * <p>
 * Apparently parents override their children in Bukkit:
 * Let {@code permissionA} be a parent permission of {@code permissionB}, setting {@code permissionB} to {@code true}.
 * Now a player has {@code permissionA=true} and {@code permissionB=false}. Result: Both are {@code true}.
 * Another player has {@code permissionA=false} and {@code permissionB=true}. Result: Both are {@code false}.
 * <p>
 * This is completely useless for things like MV's world access permissions where you could for example want to deny
 * access to all worlds by default, with a few exceptions.
 * <p>
 * This class takes over all the permission hierarchy checks and provides a way that makes sense. In the
 * above examples, the effective permissions would be equal to the permissions that were set.
 */
public class HierarchyPermission implements PermissionNode {
    private final PermissionNode parent;
    private final Permission permission;

    public HierarchyPermission(final PermissionNode parent, final String permission, final String description) {
        this.parent = parent;
        Permission p = Bukkit.getServer().getPluginManager().getPermission(permission);
        if (p == null)
            p = new Permission(permission, description, PermissionDefault.FALSE);
        else {
            p.getChildren().clear();
            //p.recalculatePermissibles(); // Not necessary because it's done automatically after setting the default
            p.setDefault(PermissionDefault.FALSE);
        }
        this.permission = p;
    }

    /**
     * Checks whether a given {@link Permissible} has this {@link HierarchyPermission}.
     *
     * @param permissible The {@link Permissible} that might have the permission.
     * @return Whether it has the permission.
     */
    public boolean has(Permissible permissible) {
        if (permissible.isPermissionSet(permission))
            return permissible.hasPermission(permission);

        // ask the guy over there
        return parent.has(permissible);
    }
}
