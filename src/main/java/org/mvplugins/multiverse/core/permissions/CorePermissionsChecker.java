package org.mvplugins.multiverse.core.permissions;

import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public boolean checkSpawnPermission(
            @NotNull CommandSender teleporter,
            @NotNull List<Entity> entities,
            @NotNull MultiverseWorld world) {
        return Scope.getApplicableScopes(teleporter, entities).stream()
                .allMatch(scope -> checkSpawnPermission(teleporter, scope, world));
    }

    public boolean checkSpawnPermission(
            @NotNull CommandSender teleporter,
            @NotNull Entity entity,
            @NotNull MultiverseWorld world) {
        return checkSpawnPermission(teleporter, Scope.getApplicableScope(teleporter, entity), world);
    }

    /**
     * Checks if the teleporter has permission to teleport the teleportee to the world's spawnpoint.
     *
     * @param teleporter    The teleporter.
     * @param scope         The scope of the permissions.
     * @param world         The world.
     * @return True if the teleporter has permission, false otherwise.
     */
    public boolean checkSpawnPermission(
            @NotNull CommandSender teleporter,
            @NotNull Scope scope,
            @NotNull MultiverseWorld world) {
        if (config.getUseFinerTeleportPermissions()) {
            return hasSpawnPermission(teleporter, scope, world);
        }
        return hasSpawnPermission(teleporter, scope, null);
    }

    /**
     * Check if the issuer has any base spawn permission of `multiverse.core.spawn.self` or `multiverse.core.spawn.other`
     *
     * @param sender    The sender that ran the command
     * @return True if the sender has any base spawn permission.
     */
    public boolean hasAnySpawnPermission(@NotNull CommandSender sender) {
        if (config.getUseFinerTeleportPermissions()) {
            return worldManager.getLoadedWorlds().stream().anyMatch(world ->
                    Arrays.stream(Scope.values()).anyMatch(scope -> hasSpawnPermission(sender, scope, world)));
        }
        return Arrays.stream(Scope.values()).anyMatch(scope -> hasSpawnPermission(sender, scope, null));
    }

    public boolean hasAnySpawnOtherPermission(@NotNull CommandSender sender) {
        if (config.getUseFinerTeleportPermissions()) {
            return worldManager.getLoadedWorlds().stream()
                    .anyMatch(world -> hasSpawnPermission(sender, Scope.OTHER, world));
        }
        return hasSpawnPermission(sender, Scope.OTHER, null);
    }

    private boolean hasSpawnPermission(@NotNull CommandSender sender, @NotNull Scope scope, @Nullable MultiverseWorld world) {
        if (world == null) {
            return hasPermission(sender, concatPermission(CorePermissions.SPAWN, scope.getScope()));
        }
        return hasPermission(sender, concatPermission(CorePermissions.SPAWN, scope.getScope(), world.getName()));
    }

    public boolean checkDestinationPacketPermission(
            @NotNull CommandSender teleporter,
            @NotNull List<Entity> teleportees,
            @NotNull Destination<?, ?> destination,
            @NotNull DestinationSuggestionPacket packet) {
        return Scope.getApplicableScopes(teleporter, teleportees).stream()
                .allMatch(scope -> checkDestinationPacketPermission(teleporter, scope, destination, packet));
    }

    public boolean checkDestinationPacketPermission(
            @NotNull CommandSender teleporter,
            @NotNull Entity teleportee,
            @NotNull Destination<?, ?> destination,
            @NotNull DestinationSuggestionPacket packet) {
        return checkDestinationPacketPermission(teleporter, Scope.getApplicableScope(teleporter, teleportee), destination, packet);
    }

    public boolean checkDestinationPacketPermission(
            @NotNull CommandSender teleporter,
            @NotNull Scope scope,
            @NotNull Destination<?, ?> destination,
            @NotNull DestinationSuggestionPacket packet) {
        if (config.getUseFinerTeleportPermissions()) {
            return hasTeleportPermission(teleporter, scope, destination.getIdentifier(), packet.finerPermissionSuffix());
        }
        return hasTeleportPermission(teleporter, scope, destination.getIdentifier(), null);
    }

    public boolean checkTeleportPermission(
            @NotNull CommandSender teleporter,
            @NotNull List<Entity> teleportees,
            @NotNull DestinationInstance<?, ?> destination) {
        return Scope.getApplicableScopes(teleporter, teleportees).stream()
                .allMatch(scope -> checkTeleportPermission(teleporter, scope, destination));
    }

    public boolean checkTeleportPermission(
            @NotNull CommandSender teleporter,
            @NotNull Entity teleportee,
            @NotNull DestinationInstance<?, ?> destination) {
        return checkTeleportPermission(teleporter, Scope.getApplicableScope(teleporter, teleportee), destination);
    }

    /**
     * Checks if the teleporter has permission to teleport the teleportee to the destination.
     *
     * @param teleporter    The teleporter.
     * @param scope         The scope of the permissions.
     * @param destination   The destination.
     * @return True if the teleporter has permission, false otherwise.
     */
    public boolean checkTeleportPermission(
            @NotNull CommandSender teleporter,
            @NotNull Scope scope,
            @NotNull DestinationInstance<?, ?> destination) {
        if (config.getUseFinerTeleportPermissions()) {
            return hasTeleportPermission(teleporter, scope, destination.getIdentifier(), destination.getFinerPermissionSuffix().getOrNull());
        }
        return hasTeleportPermission(teleporter, scope, destination.getIdentifier(), null);
    }

    /**
     * Checks if the issuer has permission to teleport to at least one destination.
     *
     * @param sender    The sender to check.
     * @return True if the issuer has permission, false otherwise.
     */
    public boolean hasAnyTeleportPermission(CommandSender sender) {
        for (Destination<?, ?> destination : destinationsProvider.getDestinations()) {
            for (Scope scope : Scope.values()) {
                if (config.getUseFinerTeleportPermissions()) {
                    for (DestinationSuggestionPacket suggestion : destination.suggestDestinations(sender, null)) {
                        if (hasTeleportPermission(sender, scope, destination.getIdentifier(), suggestion.finerPermissionSuffix())) {
                            return true;
                        }
                    }
                    continue;
                }
                if (hasTeleportPermission(sender, scope, destination.getIdentifier(), null)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasTeleportOtherPermission(CommandSender sender) {
        for (Destination<?, ?> destination : destinationsProvider.getDestinations()) {
            if (config.getUseFinerTeleportPermissions()) {
                for (DestinationSuggestionPacket suggestion : destination.suggestDestinations(sender, null)) {
                    if (hasTeleportPermission(sender, Scope.OTHER, destination.getIdentifier(), suggestion.finerPermissionSuffix())) {
                        return true;
                    }
                }
                continue;
            }
            if (hasTeleportPermission(sender, Scope.OTHER, destination.getIdentifier(), null)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTeleportPermission(@NotNull CommandSender sender, @NotNull Scope scope, @NotNull String identifier, @Nullable String finerPermissionSuffix) {
        if (finerPermissionSuffix == null) {
            return hasPermission(sender, concatPermission(CorePermissions.TELEPORT, scope.getScope(), identifier));
        }
        return hasPermission(sender, concatPermission(CorePermissions.TELEPORT, scope.getScope(), identifier, finerPermissionSuffix));
    }

    public enum Scope {
        SELF("self"),
        OTHER("other"),
        ;

        private final String scope;

        Scope(String scope) {
            this.scope = scope;
        }

        public String getScope() {
            return scope;
        }

        @Override
        public String toString() {
            return scope;
        }

        public static Scope getApplicableScope(CommandSender sender, Entity entity) {
            if (sender instanceof Entity senderEntity && senderEntity.equals(entity)) {
                return Scope.SELF;
            }
            return Scope.OTHER;
        }

        public static List<Scope> getApplicableScopes(CommandSender sender, List<Entity> entities) {
            List<Scope> applicableScopes = new ArrayList<>(Scope.values().length);
            if (sender instanceof Entity entity) {
                if (entities.contains(entity)) {
                    applicableScopes.add(Scope.SELF);
                }
                if (entities.stream().anyMatch(e -> !e.equals(entity))) {
                    applicableScopes.add(Scope.OTHER);
                }
            } else {
                applicableScopes.add(Scope.OTHER);
            }
            return applicableScopes;
        }
    }
}
