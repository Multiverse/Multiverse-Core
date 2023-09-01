package com.onarandombox.MultiverseCore.permissions;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorld;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
public class CorePermissionsChecker {
    public boolean hasWorldAccessPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_ACCESS, world.getName()));
    }

    public boolean hasWorldExemptPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_EXEMPT, world.getName()));
    }

    public boolean hasPlayerLimitBypassPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.PLAYERLIMIT_BYPASS, world.getName()));
    }

    public boolean hasGameModeBypassPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.GAMEMODE_BYPASS, world.getName()));
    }

    private String concatPermission(String permission, String...child) {
        return permission + "." + String.join(".", child);
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            Logging.finer("Checking to see if sender [%s] has permission [%s]... YES", sender.getName(), permission);
            return true;
        }
        Logging.finer("Checking to see if sender [%s] has permission [%s]... NO", sender.getName(), permission);
        return false;
    }
}
