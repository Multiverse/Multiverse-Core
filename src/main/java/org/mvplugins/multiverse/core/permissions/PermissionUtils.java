package org.mvplugins.multiverse.core.permissions;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

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
