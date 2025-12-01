package org.mvplugins.multiverse.core.permissions;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

public final class PermissionUtils {

    private static boolean debugPermissions = false;

    private PermissionUtils() {
        // Prevent instantiation
    }

    public static boolean isDebugPermissions() {
        return debugPermissions;
    }

    public static void setDebugPermissions(boolean debugPermissions) {
        PermissionUtils.debugPermissions = debugPermissions;
    }

    /**
     * Registers a permission along with all its wildcard parents.
     * <br />
     * For example, registering "mv.bypass.joinlocation" will also register "mv.*" and "mv.bypass.*" as parents.
     *
     * @param permission    The permission to register.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    public static void registerPermissionWithWildcards(Permission permission) {
        Bukkit.getServer().getPluginManager().addPermission(permission);
        String[] split = permission.getName().split("\\.");
        StringBuilder prefix = new StringBuilder();
        // Skip the last element since it's the actual permission
        Arrays.stream(Arrays.copyOfRange(split, 0, split.length - 1)).forEach(s -> {
            prefix.append(s).append(".");
            Permission perm = getOrAddPermission(prefix + "*");
            permission.addParent(perm, true);
        });
    }

    private static Permission getOrAddPermission(String permission) {
        Permission perm = Bukkit.getServer().getPluginManager().getPermission(permission);
        if (perm == null) {
            perm = new Permission(permission, PermissionDefault.FALSE);
            Bukkit.getServer().getPluginManager().addPermission(perm);
        }
        return perm;
    }

    /**
     * Joins permissions with a dot.
     *
     * @param permission    The permission
     * @param child         The string(s) to join
     * @return The newly joined permission node.
     */
    public static String concatPermission(String permission, String... child) {
        return permission + "." + String.join(".", child);
    }

    /**
     * Check and log if the sender has the permission.
     *
     * @param sender        The sender
     * @param permission    The permission
     * @return True if the sender has the permission, else false.
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            if (debugPermissions && !(sender instanceof ConsoleCommandSender)) {
                Logging.finer("Checking sender [%s] has permission [%s] : YES", sender.getName(), permission);
            }
            return true;
        }
        if (debugPermissions && !(sender instanceof ConsoleCommandSender)) {
            Logging.finer("Checking sender [%s] has permission [%s] : NO", sender.getName(), permission);
        }
        return false;
    }
}
