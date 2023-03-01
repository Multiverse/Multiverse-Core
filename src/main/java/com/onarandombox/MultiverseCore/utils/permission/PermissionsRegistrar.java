package com.onarandombox.MultiverseCore.utils.permission;

import java.util.ArrayList;
import java.util.List;

import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.MVWorld;
import org.bukkit.permissions.PermissionDefault;

public class PermissionsRegistrar {

    private static List<PrefixPermission> worldPermissions;
    private static List<PrefixPermission> destinationPermissions;

    static PrefixPermission worldAccessPermission;
    static PrefixPermission worldGamemodeBypassPermission;
    static PrefixPermission worldExemptPermission;
    static PrefixPermission worldPlayerLimitBypassPermission;
    static PrefixPermission teleportSelfPermission;
    static PrefixPermission teleportOtherPermission;

    public static void setup() {
        worldPermissions = new ArrayList<>() {{
            worldAccessPermission = new PrefixPermission("multiverse.access.", "Allows access to a world.");
            worldGamemodeBypassPermission = new PrefixPermission("mv.bypass.gamemode.", "Allows bypassing of gamemode restrictions.", PermissionDefault.FALSE);
            worldExemptPermission = new PrefixPermission("multiverse.exempt.", "A player who has this does not pay to enter this world.");
            worldPlayerLimitBypassPermission = new PrefixPermission("mv.bypass.playerlimit.", "Allows bypassing of player limit restrictions.");
        }};

        destinationPermissions = new ArrayList<>() {{
            teleportSelfPermission = new PrefixPermission("multiverse.teleport.self.", "Allows teleporting to a world.");
            teleportOtherPermission = new PrefixPermission("multiverse.teleport.other.", "Allows teleporting other players to a world.");
        }};
    }

    public static void registerWorldPermissions(MVWorld world) {
        registerPrefixPermissionList(worldPermissions, world.getName());
    }

    public static void registerDestinationPermissions(Destination<?> destination) {
        registerPrefixPermissionList(destinationPermissions, destination.getIdentifier());
    }

    public static void removeWorldPermissions(MVWorld world) {
        removePrefixPermissions(worldPermissions, world.getName());
    }

    public static void removeDestinationPermissions(Destination<?> destination) {
        removePrefixPermissions(destinationPermissions, destination.getIdentifier());
    }

    public static void removeAllWorldPermissions() {
        removeAllPrefixPermissions(worldPermissions);
    }

    public static void removeAllDestinationPermissions() {
        removeAllPrefixPermissions(destinationPermissions);
    }

    private static void removePrefixPermissions(List<PrefixPermission> worldPermissions, String name) {
        for (PrefixPermission permission : worldPermissions) {
            permission.removePermission(name);
        }
    }

    private static void registerPrefixPermissionList(List<PrefixPermission> permissions, String permissionSuffix) {
        for (PrefixPermission permission : permissions) {
            permission.registerWildcardPermission();
        }
    }

    private static void removeAllPrefixPermissions(List<PrefixPermission> worldPermissions) {
        for (PrefixPermission permission : worldPermissions) {
            permission.removeAllPermissions();
        }
    }
}
