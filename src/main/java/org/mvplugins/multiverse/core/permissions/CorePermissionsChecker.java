package org.mvplugins.multiverse.core.permissions;

import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import static org.mvplugins.multiverse.core.permissions.PermissionUtils.concatPermission;
import static org.mvplugins.multiverse.core.permissions.PermissionUtils.hasPermission;

@Service
public final class CorePermissionsChecker {

    private final MVCoreConfig config;
    private final DestinationsProvider destinationsProvider;
    private final WorldManager worldManager;

    @Inject
    CorePermissionsChecker(
            @NotNull MVCoreConfig config,
            @NotNull DestinationsProvider destinationsProvider,
            @NotNull WorldManager worldManager) {
        this.config = config;
        this.destinationsProvider = destinationsProvider;
        this.worldManager = worldManager;
    }

    public boolean hasWorldAccessPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_ACCESS, world.getName()));
    }

    public boolean hasWorldExemptPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_EXEMPT, world.getName()));
    }

    public boolean hasPlayerLimitBypassPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.PLAYERLIMIT_BYPASS, world.getName()));
    }

    public boolean hasGameModeBypassPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
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
    public boolean checkSpawnPermission(
            @NotNull CommandSender teleporter,
            @NotNull Entity teleportee,
            @NotNull MultiverseWorld world) {
        if (config.getUseFinerTeleportPermissions()) {
            return hasPermission(teleporter, concatPermission(
                    CorePermissions.SPAWN, teleportee.equals(teleporter) ? "self" : "other", world.getName()));
        }
        return hasPermission(teleporter, concatPermission(
                CorePermissions.SPAWN, teleportee.equals(teleporter) ? "self" : "other"));
    }

    /**
     * Check if the issuer has any base spawn permission of `multiverse.core.spawn.self` or `multiverse.core.spawn.other`
     *
     * @param sender    The sender that ran the command
     * @return True if the sender has any base spawn permission.
     */
    public boolean hasMinimumSpawnPermission(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            return hasPermission(sender, concatPermission(CorePermissions.SPAWN, "self", player.getWorld().getName()))
                    || hasPermission(sender, concatPermission(CorePermissions.SPAWN, "other", player.getWorld().getName()));
        }
        return hasSpawnOtherPermission(sender);
    }

    public boolean hasSpawnOtherPermission(@NotNull CommandSender sender) {
        return worldManager.getLoadedWorlds().stream()
                .anyMatch(world -> hasPermission(sender,
                        concatPermission(CorePermissions.SPAWN, "other", world.getName())));
    }

    public boolean hasDestinationPermission(
            @NotNull CommandSender teleporter,
            @NotNull CommandSender teleportee,
            @NotNull Destination<?, ?> destination) {
        if (teleportee.equals(teleporter)) {
            return hasPermission(teleporter, concatPermission(CorePermissions.TELEPORT,
                    "self", destination.getIdentifier()));
        }
        return hasPermission(teleporter, concatPermission(CorePermissions.TELEPORT,
                "other", destination.getIdentifier()));
    }

    public boolean hasFinerDestinationPermission(
            @NotNull CommandSender teleporter,
            @NotNull CommandSender teleportee,
            @NotNull Destination<?, ?> destination,
            @NotNull String finerPermissionSuffix) {
        if (!config.getUseFinerTeleportPermissions() || finerPermissionSuffix.isEmpty()) {
            return true;
        }
        if (teleportee.equals(teleporter)) {
            return hasPermission(teleporter, concatPermission(CorePermissions.TELEPORT,
                    "self", destination.getIdentifier(), finerPermissionSuffix));
        }
        return hasPermission(teleporter, concatPermission(CorePermissions.TELEPORT,
                "other", destination.getIdentifier(), finerPermissionSuffix));
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
        if (!hasDestinationPermission(teleporter, teleportee, destination.getDestination())) {
            return false;
        }
        return hasFinerDestinationPermission(
                teleporter, teleportee, destination.getDestination(), destination.getFinerPermissionSuffix().getOrElse(""));
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

    public boolean hasTeleportOtherPermission(CommandSender sender) {
        for (Destination<?, ?> destination : destinationsProvider.getDestinations()) {
            String permission = concatPermission(CorePermissions.TELEPORT, "other", destination.getIdentifier());
            if (hasPermission(sender, permission)) {
                return true;
            }
        }
        return false;
    }
}
