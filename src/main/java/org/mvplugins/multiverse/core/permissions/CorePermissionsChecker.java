package org.mvplugins.multiverse.core.permissions;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

@Service
public class CorePermissionsChecker {

    private final MVCoreConfig config;
    private final DestinationsProvider destinationsProvider;

    @Inject
    CorePermissionsChecker(@NotNull MVCoreConfig config, @NotNull DestinationsProvider destinationsProvider) {
        this.config = config;
        this.destinationsProvider = destinationsProvider;
    }

    public boolean hasWorldAccessPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
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

    /**
     * Checks if the teleporter has permission to teleport the teleportee to the world's spawnpoint.
     *
     * @param teleporter    The teleporter.
     * @param teleportee    The teleportee.
     * @param world         The world.
     * @return True if the teleporter has permission, false otherwise.
     */
    public boolean hasSpawnPermission(
            @NotNull CommandSender teleporter,
            @NotNull Entity teleportee,
            @NotNull LoadedMultiverseWorld world) {
        String permission = concatPermission(CorePermissions.SPAWN, teleportee.equals(teleporter) ? "self" : "other");
        if (!hasPermission(teleporter, permission)) {
            return false;
        }
        // TODO: Config whether to use finer permission
        return hasPermission(teleporter, concatPermission(permission, world.getName()));
    }

    /**
     * Check if the issuer has any base spawn permission of `multiverse.core.spawn.self` or `multiverse.core.spawn.other`
     *
     * @param sender    The sender that ran the command
     * @return True if the sender has any base spawn permission.
     */
    public boolean hasAnySpawnPermission(@NotNull CommandSender sender) {
        if (hasPermission(sender, concatPermission(CorePermissions.SPAWN, "self"))) {
            return true;
        }
        return hasPermission(sender, concatPermission(CorePermissions.SPAWN, "other"));
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
            @NotNull DestinationInstance<?, ?> destination) {

        String permission = concatPermission(
                CorePermissions.TELEPORT,
                teleportee.equals(teleporter) ? "self" : "other",
                destination.getDestination().getIdentifier());
        if (!hasPermission(teleporter, permission)) {
            return false;
        }
        if (!config.getUseFinerTeleportPermissions()) {
            return true;
        }
        return destination.getFinerPermissionSuffix()
                .filter(finerPermissionSuffix -> !finerPermissionSuffix.isEmpty())
                .map(finerPermissionSuffix -> hasPermission(
                        teleporter,
                        concatPermission(permission, finerPermissionSuffix)
                ))
                .getOrElse(true);
    }

    /**
     * Checks if the issuer has permission to teleport to at least one destination.
     *
     * @param sender    The sender to check.
     * @return True if the issuer has permission, false otherwise.
     */
    public boolean hasAnyTeleportPermission(CommandSender sender) {
        for (Destination<?, ?> destination : destinationsProvider.getDestinations()) {
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

    /**
     * Joins permissions with a dot.
     *
     * @param permission    The permission
     * @param child         The string(s) to join
     * @return The newly joined permission node.
     */
    public String concatPermission(String permission, String... child) {
        return permission + "." + String.join(".", child);
    }

    /**
     * Check and log if the sender has the permission.
     *
     * @param sender        The sender
     * @param permission    The permission
     * @return True if the sender has the permission, else false.
     */
    public boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            if (config.getDebugPermissions()) {
                Logging.finer("Checking sender [%s] has permission [%s] : YES", sender.getName(), permission);
            }
            return true;
        }
        if (config.getDebugPermissions()) {
            Logging.finer("Checking sender [%s] has permission [%s] : NO", sender.getName(), permission);
        }
        return false;
    }
}
