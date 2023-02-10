package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PermissionsTool {
    private static final String TELEPORT_PERM_PREFIX = "multiverse.teleport.";

    private final MultiverseCore plugin;
    private PluginManager pluginManager;
    private Permission worldAccessPermission;
    private Permission bypassGameModePermission;
    private Permission bypassEntryFeePermission;
    private Permission bypassPlayerLimitPermission;

    public PermissionsTool(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    public void setUpPermissions() {
        this.pluginManager = this.plugin.getServer().getPluginManager();
        setUpWorldPermissionWildcards();
    }

    private void setUpWorldPermissionWildcards() {
        this.worldAccessPermission = new Permission("multiverse.access.*",
                "Allows access to all worlds",
                PermissionDefault.OP);
        this.bypassGameModePermission = new Permission("mv.bypass.gamemode.*",
                "Allows players with this permission to ignore gamemode changes.",
                PermissionDefault.FALSE);
        this.bypassEntryFeePermission = new Permission("multiverse.exempt.*",
                "A player who has this does not pay to enter this world, or use any MV portals in it",
                PermissionDefault.OP);
        this.bypassPlayerLimitPermission = new Permission("mv.bypass.playerlimit.*",
                "A player who can enter this world regardless of whether its full",
                PermissionDefault.OP);

        try {
            pluginManager.addPermission(this.worldAccessPermission);
            pluginManager.addPermission(this.bypassGameModePermission);
            pluginManager.addPermission(this.bypassEntryFeePermission);
            pluginManager.addPermission(this.bypassPlayerLimitPermission);
        } catch (IllegalArgumentException e) {
            Logging.finer("World permissions wildcard already registered.");
        }

        Logging.finer("Registered world permissions wildcard.");
    }

    public void registerMVWorldPermissions(MVWorld world) {
        Permission accessPermission = new Permission("multiverse.access." + world.getName(),
                "Allows access to " + world.getName(),
                PermissionDefault.OP);
        Permission ignorePermission = new Permission("mv.bypass.gamemode." + world.getName(),
                "Allows players with this permission to ignore gamemode changes.",
                PermissionDefault.FALSE);
        Permission exemptPermission = new Permission("multiverse.exempt." + world.getName(),
                "A player who has this does not pay to enter this world, or use any MV portals in it " + world.getName(),
                PermissionDefault.OP);
        Permission playerLimitPermissions = new Permission("mv.bypass.playerlimit." + world.getName(),
                "A player who can enter this world regardless of whether its full",
                PermissionDefault.OP);

        try {
            pluginManager.addPermission(accessPermission);
            pluginManager.addPermission(ignorePermission);
            pluginManager.addPermission(exemptPermission);
            pluginManager.addPermission(playerLimitPermissions);
        } catch (IllegalArgumentException e) {
            Logging.finer("World permissions already registered for '" + world.getName() + "'");
            return;
        }

        accessPermission.addParent(this.worldAccessPermission, true);
        ignorePermission.addParent(this.bypassGameModePermission, true);
        exemptPermission.addParent(this.bypassEntryFeePermission, true);
        playerLimitPermissions.addParent(this.bypassPlayerLimitPermission, true);

        Logging.finer("Registered permissions for '" + world.getName() + "'");
    }

    public void removeMVWorldPermissions(MVWorld world) {
        Permission accessPermission = pluginManager.getPermission("multiverse.access." + world.getName());
        if (accessPermission != null) {
            this.worldAccessPermission.getChildren().remove(accessPermission.getName());
            pluginManager.removePermission(accessPermission);
        }
        Permission ignorePermission = pluginManager.getPermission("mv.bypass.gamemode." + world.getName());
        if (ignorePermission != null) {
            this.bypassGameModePermission.getChildren().remove(ignorePermission.getName());
            pluginManager.removePermission(ignorePermission);
        }
        Permission exemptPermission = pluginManager.getPermission("multiverse.exempt." + world.getName());
        if (exemptPermission != null) {
            this.bypassEntryFeePermission.getChildren().remove(exemptPermission.getName());
            pluginManager.removePermission(exemptPermission);
        }
        Permission playerLimitPermissions = pluginManager.getPermission("mv.bypass.playerlimit." + world.getName());
        if (playerLimitPermissions != null) {
            this.bypassPlayerLimitPermission.getChildren().remove(playerLimitPermissions.getName());
            pluginManager.removePermission(playerLimitPermissions);
        }
    }

    public void removeAllMVWorldPermissions() {
        this.worldAccessPermission.getChildren().keySet().forEach(pluginManager::removePermission);
        this.bypassGameModePermission.getChildren().keySet().forEach(pluginManager::removePermission);
        this.bypassEntryFeePermission.getChildren().keySet().forEach(pluginManager::removePermission);
        this.bypassPlayerLimitPermission.getChildren().keySet().forEach(pluginManager::removePermission);

        pluginManager.removePermission(this.worldAccessPermission);
        pluginManager.removePermission(this.bypassGameModePermission);
        pluginManager.removePermission(this.bypassEntryFeePermission);
        pluginManager.removePermission(this.bypassPlayerLimitPermission);

        setUpWorldPermissionWildcards();
    }

    public void registerDestinationTeleportPermissions(Destination<?> destination) {
        try {
            pluginManager.addPermission(new Permission(TELEPORT_PERM_PREFIX + "self." + destination.getIdentifier(), PermissionDefault.OP));
            pluginManager.addPermission(new Permission(TELEPORT_PERM_PREFIX + "other." + destination.getIdentifier(), PermissionDefault.OP));
            pluginManager.addPermission(new Permission(TELEPORT_PERM_PREFIX + "self." + destination.getIdentifier() + ".*", PermissionDefault.OP));
            pluginManager.addPermission(new Permission(TELEPORT_PERM_PREFIX + "other." + destination.getIdentifier() + ".*", PermissionDefault.OP));
        } catch (IllegalArgumentException e) {
            Logging.finer("Destination permissions already registered for '" + destination.getIdentifier() + "'");
        }
        Logging.finer("Registered permissions for '" + destination.getIdentifier() + "'");
    }

    public void registerFinerDestinationTeleportPermissions(Destination<?> destination, String finerSuffix) {
        String finerPermissionName = TELEPORT_PERM_PREFIX + "self." + destination.getIdentifier() + "." + finerSuffix;
        if (pluginManager.getPermission(finerPermissionName) != null) {
            return;
        }
        Permission permission = pluginManager.getPermission(TELEPORT_PERM_PREFIX + "self." + destination.getIdentifier() + ".*");
        if (permission == null) {
            return;
        }

        Permission finerPermission = new Permission(finerPermissionName, PermissionDefault.OP);
        try {
            pluginManager.addPermission(finerPermission);
            return;
        } catch (IllegalArgumentException e) {
            Logging.finer("Finer destination permissions already registered for '" + destination.getIdentifier() + "'");
        }

        finerPermission.addParent(permission, true);
        Logging.finer("Registered finer permissions '" + finerPermissionName + "' for '" + destination.getIdentifier() + "'");
    }

    public boolean hasBypassPlayerLimit(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        return hasPermission(sender, "mv.bypass.playerlimit." + toWorld.getName());
    }

    public boolean hasBypassEntryFee(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        return hasPermission(sender, "multiverse.exempt." + toWorld.getName());
    }

    public boolean hasBypassGameModeEnforcement(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        return hasPermission(sender, "mv.bypass.gamemode." + toWorld.getName());
    }

    public boolean hasWorldAccess(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        return hasPermission(sender, "multiverse.access." + toWorld.getName());
    }

    public boolean hasDestinationTeleportPermission(@NotNull CommandSender teleportee, @NotNull Destination<?> destination) {
        return hasDestinationTeleportPermission(null, teleportee, destination);
    }

    public boolean hasDestinationTeleportPermission(@Nullable CommandSender teleporter,
                                                    @NotNull CommandSender teleportee,
                                                    @NotNull Destination<?> destination
    ) {
        if (teleporter == null || teleportee.equals(teleporter)) {
            return hasPermission(teleportee, TELEPORT_PERM_PREFIX + "self." + destination.getIdentifier());
        }
        return hasPermission(teleporter, TELEPORT_PERM_PREFIX + "other." + destination.getIdentifier());
    }

    public boolean hasFinerDestinationTeleportPermission(@NotNull CommandSender teleportee, @NotNull ParsedDestination<?> destination) {
        return hasFinerDestinationTeleportPermission(null, teleportee, destination);
    }

    public boolean hasFinerDestinationTeleportPermission(@Nullable CommandSender teleporter,
                                                         @NotNull CommandSender teleportee,
                                                         @NotNull ParsedDestination<?> destination
    ) {
        if (teleporter == null || teleportee.equals(teleporter)) {
            return hasPermission(teleportee, TELEPORT_PERM_PREFIX + "self."
                    + destination.getDestination().getIdentifier() + "."
                    + destination.getDestinationInstance().getFinerPermissionSuffix());
        }
        return hasPermission(teleporter, TELEPORT_PERM_PREFIX + "other."
                + destination.getDestination().getIdentifier() + "."
                + destination.getDestinationInstance().getFinerPermissionSuffix());
    }

    public boolean hasAnyDestinationTeleportPermissions(@NotNull CommandSender sender) {
        return this.plugin.getDestinationsProvider().getRegisteredDestinations().stream()
                .anyMatch(destination -> hasDestinationTeleportPermission(sender, this.plugin.getServer().getConsoleSender(), destination)
                        || hasDestinationTeleportPermission(sender, destination));
    }

    private boolean hasPermission(@NotNull CommandSender sender, @NotNull String permission) {
        if (sender.hasPermission(permission)) {
            Logging.finer("Checking to see if sender [" + sender.getName() + "] has permission [" + permission + "]... YES");
            return true;
        }
        Logging.finer("Checking to see if sender [" + sender.getName() + "] has permission [" + permission + "]... NO");
        return false;
    }
}
