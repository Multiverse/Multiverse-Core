package org.mvplugins.multiverse.core.commandtools;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandPermission;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.permissions.PermissionUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Maps permission checking to custom logic for commands, to allow more complex permission checking.
 */
@Service
public class MVCommandPermissions {
    private final Map<String, Predicate<CommandIssuer>> permissionsCheckMap;

    @Inject
    MVCommandPermissions(@NotNull CorePermissionsChecker permissionsChecker) {
        this.permissionsCheckMap = new HashMap<>();

        registerPermissionChecker("mvteleport", issuer -> permissionsChecker.hasAnyTeleportPermission(issuer.getIssuer()));
        registerPermissionChecker("mvteleportother", issuer -> permissionsChecker.hasTeleportOtherPermission(issuer.getIssuer()));
        registerPermissionChecker("mvspawn", issuer -> permissionsChecker.hasMinimumSpawnPermission(issuer.getIssuer()));
        registerPermissionChecker("mvspawnother", issuer -> permissionsChecker.hasSpawnOtherPermission(issuer.getIssuer()));
    }

    /**
     * Registers a custom permission checker callback. Use `@id-name` in {@link CommandPermission} decorator to use
     * the callback instead of the default permission string checking.
     *
     * @param id         The permission id
     * @param checker    The permission checker callback
     */
    public void registerPermissionChecker(String id, Predicate<CommandIssuer> checker) {
        permissionsCheckMap.put(prepareId(id), checker);
    }

    private static @NotNull String prepareId(String id) {
        return (id.startsWith("@") ? "" : "@") + id.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Check if the issuer has the given permission.
     * @param issuer        The issuer
     * @param permission    The permission to check
     * @return True if the issuer has the permission
     */
    boolean hasPermission(CommandIssuer issuer, String permission) {
        return Option.of(permissionsCheckMap.get(permission))
                .map(checker -> checker.test(issuer))
                .getOrElse(() -> PermissionUtils.hasPermission(issuer.getIssuer(), permission));
    }
}
