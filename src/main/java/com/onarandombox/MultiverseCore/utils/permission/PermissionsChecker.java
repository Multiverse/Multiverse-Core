package com.onarandombox.MultiverseCore.utils.permission;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PermissionsChecker {

    public static boolean hasWorldAccessPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, PermissionsRegistrar.worldAccessPermission, world.getName());
    }

    public static boolean hasWorldGamemodeBypassPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, PermissionsRegistrar.worldGamemodeBypassPermission, world.getName());
    }

    public static boolean hasWorldExemptPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, PermissionsRegistrar.worldExemptPermission, world.getName());
    }

    public static boolean hasWorldPlayerLimitBypassPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, PermissionsRegistrar.worldPlayerLimitBypassPermission, world.getName());
    }

    public static boolean hasTeleportSelfPermission(@NotNull CommandSender sender, @NotNull ParsedDestination<?> destination) {
        return hasTeleportSelfPermission(sender, destination.getDestination());
    }

    public static boolean hasTeleportSelfPermission(@NotNull CommandSender sender, @NotNull Destination<?> destination) {
        return hasPermission(sender, PermissionsRegistrar.teleportSelfPermission, destination.getIdentifier());
    }

    public static boolean hasTeleportSelfFinerPermission(@NotNull CommandSender sender, @NotNull ParsedDestination<?> destination) {
        return hasPermission(sender, PermissionsRegistrar.teleportSelfPermission,
                destination.getIdentifier() + "." + destination.getFinerPermissionSuffix());
    }

    public static boolean hasTeleportOtherPermission(@NotNull CommandSender sender, @NotNull ParsedDestination<?> destination) {
        return hasTeleportOtherPermission(sender, destination.getDestination());
    }

    public static boolean hasTeleportOtherPermission(@NotNull CommandSender sender, @NotNull Destination<?> destination) {
        return hasPermission(sender, PermissionsRegistrar.teleportOtherPermission, destination.getIdentifier());
    }

    public static boolean hasTeleportOtherFinerPermission(@NotNull CommandSender sender, @NotNull ParsedDestination<?> destination) {
        return hasPermission(sender, PermissionsRegistrar.teleportOtherPermission,
                destination.getIdentifier() + "." + destination.getFinerPermissionSuffix());
    }

    /**
     * Internal method to check if a player has a permission and does logging.
     *
     * @param sender            The sender to check.
     * @param permission        The permission to check.
     * @param permissionSuffix  The suffix to append to the permission.
     * @return True if the player has the permission, false otherwise.
     */
    private static boolean hasPermission(@NotNull CommandSender sender, @NotNull PrefixPermission permission, @NotNull String permissionSuffix) {
        return hasPermission(sender, permission.getPermissionName(permissionSuffix));
    }

    /**
     * Internal method to check if a player has a permission and does logging.
     *
     * @param sender        The sender to check.
     * @param permission    The permission to check.
     * @return True if the player has the permission, false otherwise.
     */
    private static boolean hasPermission(@NotNull CommandSender sender, @NotNull String permission) {
        if (sender.hasPermission(permission)) {
            Logging.finer("Checking to see if sender [" + sender.getName() + "] has permission [" + permission + "]... YES");
            return true;
        }
        Logging.finer("Checking to see if sender [" + sender.getName() + "] has permission [" + permission + "]... NO");
        return false;
    }
}
