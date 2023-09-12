package org.mvplugins.multiverse.core.permissions;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.Destination;
import org.mvplugins.multiverse.core.api.MVWorld;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.destination.ParsedDestination;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.MultiverseWorld;

@Service
public class CorePermissionsChecker {

    private DestinationsProvider destinationsProvider;

    @Inject
    CorePermissionsChecker(@NotNull DestinationsProvider destinationsProvider) {
        this.destinationsProvider = destinationsProvider;
    }

    public boolean hasWorldAccessPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_ACCESS, world.getName()));
    }

    @Deprecated // TODO: Remove old MVWorld
    public boolean hasWorldAccessPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_ACCESS, world.getName()));
    }

    public boolean hasWorldExemptPermission(@NotNull CommandSender sender, @NotNull LoadedMultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_EXEMPT, world.getName()));
    }

    public boolean hasPlayerLimitBypassPermission(@NotNull CommandSender sender, @NotNull LoadedMultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.PLAYERLIMIT_BYPASS, world.getName()));
    }

    public boolean hasGameModeBypassPermission(@NotNull CommandSender sender, @NotNull LoadedMultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.GAMEMODE_BYPASS, world.getName()));
    }

    @Deprecated // TODO: Remove old MVWorld
    public boolean hasGameModeBypassPermission(@NotNull CommandSender sender, @NotNull MVWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.GAMEMODE_BYPASS, world.getName()));
    }

    /**
     * Checks if the teleporter has permission to teleport the teleportee to the destination.
     *
     * @param teleporter    The teleporter.
     * @param teleportee    The teleportee.
     * @param destination   The destination.
     * @return True if the teleporter has permission, false otherwise.
     */
    public boolean checkTeleportPermissions(
            @NotNull CommandSender teleporter,
            @NotNull Entity teleportee,
            @NotNull ParsedDestination<?> destination) {

        String permission = concatPermission(
                CorePermissions.TELEPORT,
                teleportee.equals(teleporter) ? "self" : "other",
                destination.getDestination().getIdentifier());
        if (!hasPermission(teleporter, permission)) {
            return false;
        }

        // TODO: Config whether to use finer permission
        String finerPermissionSuffix = destination.getDestinationInstance().getFinerPermissionSuffix();
        if (finerPermissionSuffix == null || finerPermissionSuffix.isEmpty()) {
            return true;
        }

        String finerPermission = concatPermission(permission, finerPermissionSuffix);
        return hasPermission(teleporter, finerPermission);
    }

    /**
     * Checks if the issuer has permission to teleport to at least one destination.
     *
     * @param sender    The sender to check.
     * @return True if the issuer has permission, false otherwise.
     */
    public boolean hasAnyTeleportPermission(CommandSender sender) {
        for (Destination<?> destination : destinationsProvider.getDestinations()) {
            String permission = concatPermission(CorePermissions.TELEPORT, "self", destination.getIdentifier());
            if (hasPermission(sender, permission)) {
                return true;
            }
            permission = concatPermission(CorePermissions.TELEPORT, "other", destination.getIdentifier());
            if (hasPermission(sender, permission)) {
                return true;
            }
        }
        return false;
    }

    private String concatPermission(String permission, String... child) {
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
