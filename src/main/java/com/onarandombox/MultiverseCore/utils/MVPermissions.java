/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.pneumaticraft.commandhandler.PermissionsInterface;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;
import java.util.logging.Level;

/**
 * Multiverse's {@link PermissionsInterface}.
 */
public class MVPermissions implements PermissionsInterface {

    private MultiverseCore plugin;
    private MVWorldManager worldMgr;

    public MVPermissions(MultiverseCore plugin) {
        this.plugin = plugin;
        this.worldMgr = plugin.getMVWorldManager();

    }

    /**
     * Check if a Player can ignore GameMode restrictions for world they travel to.
     *
     * @param p The {@link Player} to check.
     * @param w The {@link MultiverseWorld} the player wants to teleport to.
     * @return True if they should bypass restrictions.
     */
    public boolean canIgnoreGameModeRestriction(Player p, MultiverseWorld w) {
        if (p.hasPermission("mv.bypass.gamemode.*")) {
            this.plugin.log(Level.FINER, "Player has mv.bypass.gamemode.* their gamemode is ignored!");
            return true;
        }
        return p.hasPermission("mv.bypass.gamemode." + w.getName());
    }

    /**
     * Check if a Player can teleport to the Destination world from there current world.
     *
     * @param p The {@link Player} to check.
     * @param w The {@link MultiverseWorld} the player wants to teleport to.
     * @return Whether the player can teleport to the given {@link MultiverseWorld}.
     */
    public boolean canTravelFromWorld(Player p, MultiverseWorld w) {
        List<String> blackList = w.getWorldBlacklist();

        boolean returnValue = true;

        for (String s : blackList) {
            if (s.equalsIgnoreCase(p.getWorld().getName())) {
                returnValue = false;
                break;
            }
        }

        return returnValue;
    }

    /**
     * Checks if the specified {@link CommandSender} can travel to the specified {@link Location}.
     * @param sender The {@link CommandSender}.
     * @param location The {@link Location}.
     * @return Whether the {@link CommandSender} can travel to the specified {@link Location}.
     */
    public boolean canTravelFromLocation(CommandSender sender, Location location) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player teleporter = (Player) sender;
        if (!this.worldMgr.isMVWorld(location.getWorld().getName())) {
            return false;
        }
        return canTravelFromWorld(teleporter, this.worldMgr.getMVWorld(location.getWorld().getName()));
    }

    /**
     * Check if the Player has the permissions to enter this world.
     *
     * @param p The {@link Player} player that wants to enter
     * @param w The {@link MultiverseWorld} he wants to enter
     * @return Whether he has the permission to enter the world
     */
    public boolean canEnterWorld(Player p, MultiverseWorld w) {
        // If we're not enforcing access, anyone can enter.
        if (!plugin.getMVConfig().getEnforceAccess()) {
            this.plugin.log(Level.FINEST, "EnforceAccess is OFF. Player was allowed in " + w.getAlias());
            return true;
        }
        return this.hasPermission(p, "multiverse.access." + w.getName(), false);
    }

    private boolean canEnterLocation(Player p, Location l) {
        if (l == null) {
            return false;
        }
        String worldName = l.getWorld().getName();
        if (!this.plugin.getMVWorldManager().isMVWorld(worldName)) {
            return false;
        }
        return this.hasPermission(p, "multiverse.access." + worldName, false);
    }

    /**
     * Check to see if a sender can enter a destination.
     * The reason this is not a player, is it can be used to simply check permissions
     * The console should, for exmaple, always see all worlds
     *
     * @param sender The CommandSender to check.
     * @param d      The destination they are requesting.
     * @return True if that sender can go to that destination
     */
    public boolean canEnterDestination(CommandSender sender, MVDestination d) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;
        if (d == null || d.getLocation(p) == null) {
            return false;
        }
        String worldName = d.getLocation(p).getWorld().getName();
        if (!this.worldMgr.isMVWorld(worldName)) {
            return false;
        }
        if (!canEnterLocation(p, d.getLocation(p))) {
            return false;
        }
        return this.hasPermission(p, d.getRequiredPermission(), false);
    }

    /**
     * Check to see if a player has a permission.
     *
     * @param sender       Who is requesting the permission.
     * @param node         The permission node in string format; multiverse.core.list.worlds for example.
     * @param isOpRequired @Deprecated. This is not used for anything anymore.
     * @return True if they have that permission or any parent.
     */
    public boolean hasPermission(CommandSender sender, String node, boolean isOpRequired) {
        if (!(sender instanceof Player)) {
            return true;
        }
        // NO one can access a null permission (mainly used for destinations):w
        if (node == null) {
            return false;
        }
        // Everyone can access an empty permission
        // Currently used for the PlayerDestination
        if (node.equals("")) {
            return true;
        }
        boolean hasPermission = checkActualPermission(sender, node);

        // I consider this a workaround. At the moment, when we add a node AND recalc the permissions, until the perms
        // plugin reloads, when MV asks the API if a player has a perm, it reports that they do NOT.
        // For the moment, we're going to check all of this node's parents to see if the user has those. It stops
        // when if finds a true or there are no more parents. --FF
//        if (!hasPermission) {
//            hasPermission = this.hasAnyParentPermission(sender, node);
//        }

        return hasPermission;
    }

    // TODO: Better player checks, most likely not needed, but safer.
    private boolean checkActualPermission(CommandSender sender, String node) {
        Player player = (Player) sender;

        boolean hasPermission = sender.hasPermission(node);
        if (hasPermission) {
            this.plugin.log(Level.FINEST, "Checking to see if player [" + player.getName() + "] has permission [" + node + "]... YES");
        } else {
            this.plugin.log(Level.FINEST, "Checking to see if player [" + player.getName() + "] has permission [" + node + "]... NO");
        }
        return hasPermission;
    }

    /**
     * Checks to see if the sender has any parent perms.
     * Stops when it finds one or when there are no more parents.
     * This method is recursive.
     *
     * @param sender Who is asking for the permission.
     * @param node   The permission node to check (possibly already a parent).
     * @return True if they have any parent perm, false if none.
     */
    private boolean hasAnyParentPermission(CommandSender sender, String node) {
        String parentPerm = this.pullOneLevelOff(node);
        // Base case
        if (parentPerm == null) {
            return false;
        }
        // If they have a parent, they're good
        if (this.checkActualPermission(sender, parentPerm + ".*")) {
            return true;
        }
        return hasAnyParentPermission(sender, parentPerm);
    }

    /**
     * Pulls one level off of a yaml style node.
     * Given multiverse.core.list.worlds will return multiverse.core.list
     *
     * @param node The root node to check.
     * @return The parent of the node
     */
    private String pullOneLevelOff(String node) {
        if (node == null) {
            return null;
        }
        int index = node.lastIndexOf(".");
        if (index > 0) {
            return node.substring(0, index);
        }
        return null;
    }

    /**
     * Gets the type of this {@link PermissionsInterface}.
     * @return The type of this {@link PermissionsInterface}.
     */
    public String getType() {
        return "Bukkit Permissions (SuperPerms)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAnyPermission(CommandSender sender, List<String> nodes, boolean isOpRequired) {
        for (String node : nodes) {
            if (this.hasPermission(sender, node, isOpRequired)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAllPermission(CommandSender sender, List<String> nodes, boolean isOpRequired) {
        for (String node : nodes) {
            if (!this.hasPermission(sender, node, isOpRequired)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a permission.
     * @param string The permission as {@link String}.
     * @param defaultValue The default-value.
     * @return The permission as {@link Permission}.
     */
    public Permission addPermission(String string, PermissionDefault defaultValue) {
        if (this.plugin.getServer().getPluginManager().getPermission(string) == null) {
            Permission permission = new Permission(string, defaultValue);
            this.plugin.getServer().getPluginManager().addPermission(permission);
            this.addToParentPerms(string);
        }
        return this.plugin.getServer().getPluginManager().getPermission(string);
    }

    private void addToParentPerms(String permString) {
        String permStringChopped = permString.replace(".*", "");

        String[] seperated = permStringChopped.split("\\.");
        String parentPermString = getParentPerm(seperated);
        if (parentPermString == null) {
            addToRootPermission("*", permStringChopped);
            addToRootPermission("*.*", permStringChopped);
            return;
        }
        Permission parentPermission = this.plugin.getServer().getPluginManager().getPermission(parentPermString);
        // Creat parent and grandparents
        if (parentPermission == null) {
            parentPermission = new Permission(parentPermString);
            this.plugin.getServer().getPluginManager().addPermission(parentPermission);

            this.addToParentPerms(parentPermString);
        }
        // Create actual perm.
        Permission actualPermission = this.plugin.getServer().getPluginManager().getPermission(permString);
        // Extra check just to make sure the actual one is added
        if (actualPermission == null) {

            actualPermission = new Permission(permString);
            this.plugin.getServer().getPluginManager().addPermission(actualPermission);
        }
        if (!parentPermission.getChildren().containsKey(permString)) {
            parentPermission.getChildren().put(actualPermission.getName(), true);
            this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(parentPermission);
        }
    }

    private void addToRootPermission(String rootPerm, String permStringChopped) {
        Permission rootPermission = this.plugin.getServer().getPluginManager().getPermission(rootPerm);
        if (rootPermission == null) {
            rootPermission = new Permission(rootPerm);
            this.plugin.getServer().getPluginManager().addPermission(rootPermission);
        }
        rootPermission.getChildren().put(permStringChopped + ".*", true);
        this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(rootPermission);
    }

    /**
     * If the given permission was 'multiverse.core.tp.self', this would return 'multiverse.core.tp.*'.
     */
    private String getParentPerm(String[] seperated) {
        if (seperated.length == 1) {
            return null;
        }
        String returnString = "";
        for (int i = 0; i < seperated.length - 1; i++) {
            returnString += seperated[i] + ".";
        }
        return returnString + "*";
    }

}
