package org.mvplugins.multiverse.core.permissions;

import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.CoreConfig;
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

/**
 * Handles permission checks for Multiverse features, including world access,
 * teleportation, spawn permissions, and destination checks.
 */
@Service
public final class CorePermissionsChecker {

    private final CoreConfig config;
    private final DestinationsProvider destinationsProvider;
    private final WorldManager worldManager;

    /**
     * Creates a CorePermissionsChecker instance with the required dependencies.
     *
     * @param config The core configuration.
     * @param destinationsProvider The provider for destinations.
     * @param worldManager The manager for Multiverse worlds.
     */
    @Inject
    CorePermissionsChecker(
            @NotNull CoreConfig config,
            @NotNull DestinationsProvider destinationsProvider,
            @NotNull WorldManager worldManager) {
        this.config = config;
        this.destinationsProvider = destinationsProvider;
        this.worldManager = worldManager;
    }

    /**
     * Checks if the sender has permission to access the specified world.
     *
     * @param sender The command sender.
     * @param world The Multiverse world.
     * @return True if the sender has access, false otherwise.
     */
    public boolean hasWorldAccessPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_ACCESS, world.getName()));
    }

    /**
     * Checks if the sender is exempt from world access restrictions.
     *
     * @param sender The command sender.
     * @param world The Multiverse world.
     * @return True if the sender is exempt, false otherwise.
     */
    public boolean hasWorldExemptPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.WORLD_EXEMPT, world.getName()));
    }

    /**
     * Checks if the sender can bypass the player limit for the specified world.
     *
     * @param sender The command sender.
     * @param world The Multiverse world.
     * @return True if the sender can bypass the limit, false otherwise.
     */
    public boolean hasPlayerLimitBypassPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.PLAYERLIMIT_BYPASS, world.getName()));
    }

    /**
     * Checks if the sender can bypass the world's enforced game mode.
     *
     * @param sender The command sender.
     * @param world The Multiverse world.
     * @return True if the sender can bypass the game mode restriction, false otherwise.
     */
    public boolean hasGameModeBypassPermission(@NotNull CommandSender sender, @NotNull MultiverseWorld world) {
        return hasPermission(sender, concatPermission(CorePermissions.GAMEMODE_BYPASS, world.getName()));
    }

    /**
     * Checks if the teleporter has permission to spawn entities in the specified world.
     *
     * @param teleporter The command sender performing the teleport.
     * @param entities The list of entities being teleported.
     * @param world The Multiverse world.
     * @return True if the sender has permission, false otherwise.
     */
    public boolean checkSpawnPermission(
            @NotNull CommandSender teleporter,
            @NotNull List<Entity> entities,
            @NotNull MultiverseWorld world) {
        return Scope.getApplicableScopes(teleporter, entities).stream()
                .allMatch(scope -> checkSpawnPermission(teleporter, scope, world));
    }

    /**
     * Checks if the teleporter has permission to spawn an entity in the specified world.
     *
     * @param teleporter The command sender.
     * @param entity The entity being teleported.
     * @param world The Multiverse world.
     * @return True if the sender has permission, false otherwise.
     */
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
        return hasAnySpawnPermission(sender, Scope.values());
    }

    public boolean hasAnySpawnPermission(@NotNull CommandSender sender, @NotNull Scope scope) {
        return hasAnySpawnPermission(sender, new Scope[]{scope});
    }

    /**
     * Checks if the sender has permission to spawn other players in any world.
     *
     * @param sender The command sender.
     * @param scopes The scopes to check.
     * @return True if the sender has permission, false otherwise.
     */
    public boolean hasAnySpawnPermission(@NotNull CommandSender sender, @NotNull Scope[] scopes) {
        if (config.getUseFinerTeleportPermissions()) {
            return worldManager.getLoadedWorlds().stream().anyMatch(world ->
                    Arrays.stream(scopes).anyMatch(scope -> hasSpawnPermission(sender, scope, world)));
        }
        return Arrays.stream(scopes).anyMatch(scope -> hasSpawnPermission(sender, scope, null));
    }

    private boolean hasSpawnPermission(@NotNull CommandSender sender, @NotNull Scope scope, @Nullable MultiverseWorld world) {
        if (world == null) {
            return hasPermission(sender, concatPermission(CorePermissions.SPAWN, scope.getScope()));
        }
        return hasPermission(sender, concatPermission(CorePermissions.SPAWN, scope.getScope(), world.getName()));
    }

    /**
     * Checks if the sender has permission to send a destination packet for teleporting entities.
     *
     * @param teleporter The sender.
     * @param teleportees The list of entities being teleported.
     * @param packet The destination suggestion packet.
     * @return True if the sender has permission, false otherwise.
     */
    public boolean checkDestinationPacketPermission(
            @NotNull CommandSender teleporter,
            @NotNull List<Entity> teleportees,
            @NotNull DestinationSuggestionPacket packet) {
        return Scope.getApplicableScopes(teleporter, teleportees).stream()
                .allMatch(scope -> checkDestinationPacketPermission(teleporter, scope, packet));
    }

    /**
     * Checks if the sender has permission to send a destination packet for teleporting an entity.
     *
     * @param teleporter The sender.
     * @param teleportee The entity being teleported.
     * @param packet The destination suggestion packet.
     * @return True if the sender has permission, false otherwise.
     */
    public boolean checkDestinationPacketPermission(
            @NotNull CommandSender teleporter,
            @NotNull Entity teleportee,
            @NotNull DestinationSuggestionPacket packet) {
        return checkDestinationPacketPermission(teleporter, Scope.getApplicableScope(teleporter, teleportee), packet);
    }

    public boolean checkDestinationPacketPermission(
            @NotNull CommandSender teleporter,
            @NotNull Scope scope,
            @NotNull DestinationSuggestionPacket packet) {
        return hasTeleportPermission(
                teleporter,
                scope,
                packet.destination().getIdentifier(),
                config.getUseFinerTeleportPermissions() ? packet.finerPermissionSuffix() : null);
    }

    /**
     * Checks if the teleporter has permission to teleport multiple entities to a destination.
     *
     * @param teleporter The sender.
     * @param teleportees The list of entities being teleported.
     * @param destination The teleport destination.
     * @return True if the sender has permission, false otherwise.
     */
    public boolean checkTeleportPermission(
            @NotNull CommandSender teleporter,
            @NotNull List<Entity> teleportees,
            @NotNull DestinationInstance<?, ?> destination) {
        return Scope.getApplicableScopes(teleporter, teleportees).stream()
                .allMatch(scope -> checkTeleportPermission(teleporter, scope, destination));
    }

    /**
     * Checks if the teleporter has permission to teleport a single entity to a destination.
     *
     * @param teleporter The sender.
     * @param teleportee The entity being teleported.
     * @param destination The teleport destination.
     * @return True if the sender has permission, false otherwise.
     */
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
    public boolean hasAnyTeleportPermission(@NotNull CommandSender sender) {
        return hasAnyTeleportPermission(sender, Scope.values());
    }

    /**
     * Checks if the issuer has permission to teleport to at least one destination.
     *
     * @param sender    The sender to check.
     * @param scope    The scope to check.
     * @return True if the issuer has permission, false otherwise.
     */
    public boolean hasAnyTeleportPermission(@NotNull CommandSender sender, @NotNull Scope scope) {
        return hasAnyTeleportPermission(sender, new Scope[]{scope});
    }

    /**
     * Checks if the issuer has permission to teleport to at least one destination.
     *
     * @param sender    The sender to check.
     * @param scopes    The scopes to check.
     * @return True if the issuer has permission, false otherwise.
     */
    public boolean hasAnyTeleportPermission(@NotNull CommandSender sender, @NotNull Scope[] scopes) {
        if (!config.getUseFinerTeleportPermissions()) {
            // Just loop over the destination
            for (Destination destination : destinationsProvider.getDestinations()) {
                for (Scope scope : scopes) {
                    if (hasTeleportPermission(sender, scope, destination.getIdentifier(), null)) {
                        return true;
                    }
                }
            }
            return false;
        }

        // Loop through all finer possibilities
        for (DestinationSuggestionPacket suggestion : destinationsProvider.suggestDestinations(sender, null)) {
            for (Scope scope : scopes) {
                if (hasTeleportPermission(sender, scope, suggestion.destination().getIdentifier(), suggestion.finerPermissionSuffix())) {
                    return true;
                }
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
