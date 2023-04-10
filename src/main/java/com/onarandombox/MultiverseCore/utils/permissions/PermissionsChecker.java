package com.onarandombox.MultiverseCore.utils.permissions;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
public class PermissionsChecker {

    private final MVCorePermissions permissions;

    @Inject
    PermissionsChecker(MVCorePermissions permissions) {
        this.permissions = permissions;
    }

    public boolean hasWorldAccessPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, permissions.WORLD_ACCESS.child(world.getName()));
    }

    public boolean hasWorldExemptPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, permissions.WORLD_EXEMPT.child(world.getName()));
    }

    public boolean hasPlayerLimitBypassPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, permissions.PLAYERLIMIT_BYPASS.child(world.getName()));
    }

    public boolean hasGameModeBypassPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, permissions.GAMEMODE_BYPASS.child(world.getName()));
    }

    public boolean hasTeleportPermission(@NotNull CommandSender sender, @NotNull ParsedDestination<?> destination, boolean self) {
        ChildPermission permission = self ? permissions.TELEPORT_SELF : permissions.TELEPORT_OTHER;
        if (!hasPermission(sender, permission.child(destination.getIdentifier()))) {
            return false;
        }
        if (destination.getFinerPermissionSuffix() == null) {
            return true;
        }
        return hasPermission(sender, permission.child(destination.getIdentifier()).child(destination.getFinerPermissionSuffix()));
    }

    private boolean hasPermission(CommandSender sender, Permission permission) {
        if (sender.hasPermission(permission)) {
            Logging.finer("Checking to see if sender [%s] has permission [%s]... YES", sender.getName(), permission.getName());
            return true;
        }
        Logging.finer("Checking to see if sender [%s] has permission [%s]... NO", sender.getName(), permission.getName());
        return false;
    }
}
