package org.mvplugins.multiverse.core.commandtools;

import co.aikar.commands.CommandIssuer;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class MVCommandPermissions {
    private final Map<String, Predicate<CommandIssuer>> permissionsCheckMap;

    @Inject
    MVCommandPermissions(@NotNull CorePermissionsChecker permissionsChecker) {
        permissionsCheckMap = new HashMap<>();

        registerPermissionChecker("teleport", issuer -> permissionsChecker.hasAnyTeleportPermission(issuer.getIssuer()));
    }

    public void registerPermissionChecker(String id, Predicate<CommandIssuer> checker) {
        permissionsCheckMap.put(prepareId(id), checker);
    }

    private static @NotNull String prepareId(String id) {
        return (id.startsWith("@") ? "" : "@") + id.toLowerCase(Locale.ENGLISH);
    }

    public boolean hasPermission(CommandIssuer issuer, String permission) {
        return Option.of(permissionsCheckMap.get(permission))
                .map(checker -> checker.test(issuer))
                .getOrElse(() -> issuer.hasPermission(permission));
    }
}
