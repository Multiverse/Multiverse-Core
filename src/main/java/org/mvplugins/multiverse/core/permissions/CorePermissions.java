package org.mvplugins.multiverse.core.permissions;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

import static org.mvplugins.multiverse.core.permissions.PermissionUtils.concatPermission;

@Service
public final class CorePermissions {
    /**
     * Permission to access a world.
     */
    static final String WORLD_ACCESS = "multiverse.access";

    /**
     * Permission to bypass the entry fee of a world.
     */
    static final String WORLD_EXEMPT = "multiverse.exempt";

    /**
     * Permission to bypass the gamemode of a world.
     */
    static final String GAMEMODE_BYPASS = "mv.bypass.gamemode";

    /**
     * Permission to bypass the player limit of a world.
     */
    static final String PLAYERLIMIT_BYPASS = "mv.bypass.playerlimit";

    /**
     * Permission to teleport to a destination.
     */
    static final String TELEPORT = "multiverse.teleport";

    /**
     * Permission to teleport to spawn in a world.
     */
    static final String SPAWN = "multiverse.core.spawn";

    private final PluginManager pluginManager;

    @Inject
    CorePermissions(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public Try<Void> addWorldPermissions(@NotNull MultiverseWorld world) {
        return Try.run(() -> {
            pluginManager.addPermission(new Permission(
                    concatPermission(WORLD_ACCESS, world.getName()), PermissionDefault.OP));
            pluginManager.addPermission(new Permission(
                    concatPermission(WORLD_EXEMPT, world.getName()), PermissionDefault.OP));
            pluginManager.addPermission(new Permission(
                    concatPermission(GAMEMODE_BYPASS, world.getName()), PermissionDefault.FALSE));
            pluginManager.addPermission(new Permission(
                    concatPermission(PLAYERLIMIT_BYPASS, world.getName()), PermissionDefault.FALSE));
            pluginManager.addPermission(new Permission(
                    concatPermission(SPAWN, world.getName()), PermissionDefault.OP));
            pluginManager.addPermission(new Permission(
                    concatPermission(SPAWN, "self", world.getName()), PermissionDefault.OP));
            pluginManager.addPermission(new Permission(
                    concatPermission(SPAWN, "other", world.getName()), PermissionDefault.OP));
            Logging.fine("Successfully registered permissions for world %s", world.getName());
        });
    }

    public Try<Void> removeWorldPermissions(@NotNull MultiverseWorld world) {
        return Try.run(() -> {
            pluginManager.removePermission(concatPermission(WORLD_ACCESS, world.getName()));
            pluginManager.removePermission(concatPermission(WORLD_EXEMPT, world.getName()));
            pluginManager.removePermission(concatPermission(GAMEMODE_BYPASS, world.getName()));
            pluginManager.removePermission(concatPermission(PLAYERLIMIT_BYPASS, world.getName()));
            pluginManager.removePermission(concatPermission(SPAWN, "self", world.getName()));
            pluginManager.removePermission(concatPermission(SPAWN, "other", world.getName()));
            Logging.fine("Successfully removed permissions for world %s", world.getName());
        });
    }

    public Try<Void> addDestinationPermissions(@NotNull Destination<?, ?> destination) {
        return Try.run(() -> {
            pluginManager.addPermission(new Permission(
                    concatPermission(TELEPORT, "self", destination.getIdentifier()), PermissionDefault.OP));
            pluginManager.addPermission(new Permission(
                    concatPermission(TELEPORT, "other", destination.getIdentifier()), PermissionDefault.OP));
            Logging.fine("Successfully registered permissions for destination %s", destination.getIdentifier());
        });
    }

    public Try<Void> removeDestinationPermissions(@NotNull Destination<?, ?> destination) {
        return Try.run(() -> {
            pluginManager.removePermission(concatPermission(TELEPORT, "self", destination.getIdentifier()));
            pluginManager.removePermission(concatPermission(TELEPORT, "other", destination.getIdentifier()));
            Logging.fine("Successfully removed permissions for destination %s", destination.getIdentifier());
        });
    }
}
