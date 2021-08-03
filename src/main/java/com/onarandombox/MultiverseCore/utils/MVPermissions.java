/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.pneumaticraft.commandhandler.PermissionsInterface;
import org.bukkit.ChatColor;
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
        return p.hasPermission("mv.bypass.gamemode." + w.getName());
    }

    /**
     * Check if a Player can ignore Fly restrictions for world they travel to.
     *
     * @param p The {@link Player} to check.
     * @param w The {@link MultiverseWorld} the player wants to teleport to.
     * @return True if they should bypass restrictions.
     */
    public boolean canIgnoreFlyRestriction(Player p, MultiverseWorld w) {
        return p.hasPermission("mv.bypass.fly." + w.getName());
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
        // Now The Bed destination can return null now.
        if (location == null) {
            return false;
        }
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
            Logging.finest("EnforceAccess is OFF. Player was allowed in " + w.getAlias());
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
     * Tells a {@link CommandSender} why another {@link CommandSender} can or can not access a certain {@link MVDestination}.
     * @param asker The {@link CommandSender} that's asking.
     * @param playerInQuestion The {@link CommandSender} whose permissions we want to know.
     * @param d The {@link MVDestination}.
     */
    public void tellMeWhyICantDoThis(CommandSender asker, CommandSender playerInQuestion, MVDestination d) {
        boolean cango = true;
        if (!(playerInQuestion instanceof Player)) {
            asker.sendMessage(String.format("The console can do %severything%s.", ChatColor.RED, ChatColor.WHITE));
            return;
        }
        Player p = (Player) playerInQuestion;
        if (d == null) {
            asker.sendMessage(String.format("The provided Destination is %sNULL%s, and therefore %sINVALID%s.",
                    ChatColor.RED, ChatColor.WHITE, ChatColor.RED, ChatColor.WHITE));
            cango = false;
        }
        // We know it'll be a player here due to the first line of this method.
        if (d.getLocation(p) == null) {
            asker.sendMessage(String.format(
                    "The player will spawn at an %sindeterminate location%s. Talk to the MV Devs if you see this",
                    ChatColor.RED, ChatColor.WHITE));
            cango = false;
        }
        String worldName = d.getLocation(p).getWorld().getName();
        if (!this.worldMgr.isMVWorld(worldName)) {
            asker.sendMessage(String.format("The destination resides in a world(%s%s%s) that is not managed by Multiverse.",
                    ChatColor.AQUA, worldName, ChatColor.WHITE));
            asker.sendMessage(String.format("Type %s/mv import ?%s to see the import command's help page.",
                    ChatColor.DARK_AQUA, ChatColor.WHITE));
            cango = false;
        }
        if (!this.hasPermission(p, "multiverse.access." + worldName, false)) {
            asker.sendMessage(String.format("The player (%s%s%s) does not have the required world entry permission (%s%s%s) to go to the destination (%s%s%s).",
                    ChatColor.AQUA, p.getDisplayName(), ChatColor.WHITE,
                    ChatColor.GREEN, "multiverse.access." + worldName, ChatColor.WHITE,
                    ChatColor.DARK_AQUA, d.getName(), ChatColor.WHITE));
            cango = false;
        }
        if (!this.hasPermission(p, d.getRequiredPermission(), false)) {
            asker.sendMessage(String.format("The player (%s%s%s) does not have the required entry permission (%s%s%s) to go to the destination (%s%s%s).",
                    ChatColor.AQUA, p.getDisplayName(), ChatColor.WHITE,
                    ChatColor.GREEN, d.getRequiredPermission(), ChatColor.WHITE,
                    ChatColor.DARK_AQUA, d.getName(), ChatColor.WHITE));
            cango = false;
        }
        if (cango) {
            asker.sendMessage(String.format("The player (%s%s%s) CAN go to the destination (%s%s%s).",
                    ChatColor.AQUA, p.getDisplayName(), ChatColor.WHITE,
                    ChatColor.DARK_AQUA, d.getName(), ChatColor.WHITE));
        } else {
            asker.sendMessage(String.format("The player (%s%s%s) cannot access the destination %s%s%s. Therefore they can't use mvtp at all for this.",
                    ChatColor.AQUA, p.getDisplayName(), ChatColor.WHITE,
                    ChatColor.DARK_AQUA, d.getName(), ChatColor.WHITE));
            return;
        }
        if (!this.hasPermission(p, "multiverse.teleport.self." + d.getIdentifier(), false)) {
            asker.sendMessage(String.format("The player (%s%s%s) does not have the required teleport permission (%s%s%s) to use %s/mvtp %s%s.",
                    ChatColor.AQUA, p.getDisplayName(), ChatColor.WHITE,
                    ChatColor.GREEN, "multiverse.teleport.self." + d.getIdentifier(), ChatColor.WHITE,
                    ChatColor.DARK_AQUA, d.getName(), ChatColor.WHITE));
        } else {
            asker.sendMessage(String.format("The player (%s%s%s) has the required teleport permission (%s%s%s) to use %s/mvtp %s%s.",
                    ChatColor.AQUA, p.getDisplayName(), ChatColor.WHITE,
                    ChatColor.GREEN, "multiverse.teleport.self." + d.getIdentifier(), ChatColor.WHITE,
                    ChatColor.DARK_AQUA, d.getName(), ChatColor.WHITE));
        }
        if (!this.hasPermission(p, "multiverse.teleport.other." + d.getIdentifier(), false)) {
            asker.sendMessage(String.format("The player (%s%s%s) does not have the required teleport permission (%s%s%s) to send others to %s%s%s via mvtp.",
                    ChatColor.AQUA, p.getDisplayName(), ChatColor.WHITE,
                    ChatColor.GREEN, "multiverse.teleport.other." + d.getIdentifier(), ChatColor.WHITE,
                    ChatColor.DARK_AQUA, d.getName(), ChatColor.WHITE));
        } else {
            asker.sendMessage(String.format("The player (%s%s%s) has required teleport permission (%s%s%s) to send others to %s%s%s via mvtp.",
                    ChatColor.AQUA, p.getDisplayName(), ChatColor.WHITE,
                    ChatColor.GREEN, "multiverse.teleport.other." + d.getIdentifier(), ChatColor.WHITE,
                    ChatColor.DARK_AQUA, d.getName(), ChatColor.WHITE));
        }
    }

    /**
     * Check to see if a player has a permission.
     *
     * @param sender       Who is requesting the permission.
     * @param node         The permission node in string format; multiverse.core.list.worlds for example.
     * @param isOpRequired deprecated This is not used for anything anymore.
     * @return True if they have that permission or any parent.
     */
    @Override
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

        return checkActualPermission(sender, node);
    }

    // TODO: Better player checks, most likely not needed, but safer.
    private boolean checkActualPermission(CommandSender sender, String node) {
        Player player = (Player) sender;

        boolean hasPermission = sender.hasPermission(node);
        if (!sender.isPermissionSet(node)) {
            Logging.finer(String.format("The node [%s%s%s] was %sNOT%s set for [%s%s%s].",
                    ChatColor.RED, node, ChatColor.WHITE, ChatColor.RED, ChatColor.WHITE, ChatColor.AQUA,
                    player.getDisplayName(), ChatColor.WHITE));
        }
        if (hasPermission) {
            Logging.finer("Checking to see if player [" + player.getName() + "] has permission [" + node + "]... YES");
        } else {
            Logging.finer("Checking to see if player [" + player.getName() + "] has permission [" + node + "]... NO");
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
    // TODO remove this...?
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
    private static String pullOneLevelOff(String node) {
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
    private static String getParentPerm(String[] seperated) {
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
