/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.onarandombox.utils.MVDestination;
import com.onarandombox.utils.WorldManager;
import com.pneumaticraft.commandhandler.PermissionsInterface;

public class MVPermissions implements PermissionsInterface {

    private MultiverseCore plugin;
    private WorldManager worldMgr;
    private PermissionHandler permissions = null;

    /**
     * Constructor FTW
     *
     * @param plugin Pass along the Core Plugin.
     */
    public MVPermissions(MultiverseCore plugin) {
        this.plugin = plugin;
        this.worldMgr = plugin.getWorldManager();
        // We have to see if permissions was loaded before MV was
        if (this.plugin.getServer().getPluginManager().getPlugin("Permissions") != null) {
            this.setPermissions(((Permissions) this.plugin.getServer().getPluginManager().getPlugin("Permissions")).getHandler());
            this.plugin.log(Level.INFO, "- Attached to Permissions");
        }
    }

    /**
     * Check if a Player can teleport to the Destination world from there current world.
     *
     * @param p
     * @param w
     * @return
     */
    public Boolean canTravelFromWorld(Player p, MVWorld w) {
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

    public boolean canTravelFromLocation(Player teleporter, Location location) {
        if (!this.worldMgr.isMVWorld(location.getWorld().getName())) {
            return false;
        }
        return canTravelFromWorld(teleporter, this.worldMgr.getMVWorld(location.getWorld().getName()));
    }

    /**
     * Check if the Player has the permissions to enter this world.
     *
     * @param p
     * @param w
     * @return
     */
    public Boolean canEnterWorld(Player p, MVWorld w) {
        return this.hasPermission(p, "multiverse.access." + w.getName(), false);
    }

    public Boolean canEnterLocation(Player p, Location l) {
        if (l == null) {
            return false;
        }
        String worldName = l.getWorld().getName();
        if (!this.plugin.getWorldManager().isMVWorld(worldName)) {
            return false;
        }
        return this.hasPermission(p, "multiverse.access." + worldName, false);
    }

    public Boolean canEnterDestination(Player p, MVDestination d) {
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

    public void setPermissions(PermissionHandler handler) {
        this.permissions = handler;
    }

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

        Player player = (Player) sender;
        this.plugin.log(Level.FINEST, "Checking to see if player [" + player.getName() + "] has permission [" + node + "]");
        boolean opFallback = this.plugin.getConfig().getBoolean("opfallback", true);
        if (this.permissions != null && this.permissions.has(player, node)) {
            // If Permissions is enabled we check against them.
            this.plugin.log(Level.FINEST, "Allowed by Permissions or something that looked like it.");
            return true;
        } else if (sender.hasPermission(node)) {
            // If Now check the bukkit permissions
            this.plugin.log(Level.FINEST, "Allowed by the built in Permissions.");
            return true;
        } else if (player.isOp() && opFallback) {
            // If Player is Op we always let them use it if they have the fallback enabled!
            this.plugin.log(Level.FINEST, "Allowed by OP (opfallback was on).");
            return true;
        }

        // If the Player doesn't have Permissions and isn't an Op then
        // we return true if OP is not required, otherwise we return false
        // This allows us to act as a default permission guidance

        // If they have the op fallback disabled, NO commands will work without a permissions plugin.
        if (!isOpRequired && opFallback) {
            this.plugin.log(Level.FINEST, "Allowed because opfallback was set to true.");
        }
        return !isOpRequired && opFallback;

    }

    public String getType() {
        String opsfallback = "";
        if (this.plugin.getConfig().getBoolean("opfallback", true)) {
            opsfallback = " WITH OPs.txt fallback";
        }
        if (this.permissions != null) {
            return "Permissions " + this.plugin.getServer().getPluginManager().getPlugin("Permissions").getDescription().getVersion() + opsfallback;
        }

        return "Bukkit Permissions" + opsfallback;
    }

    public boolean hasAnyPermission(CommandSender sender, List<String> nodes, boolean isOpRequired) {
        for (String node : nodes) {
            if (this.hasPermission(sender, node, isOpRequired)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAllPermission(CommandSender sender, List<String> nodes, boolean isOpRequired) {
        for (String node : nodes) {
            if (!this.hasPermission(sender, node, isOpRequired)) {
                return false;
            }
        }
        return true;
    }

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
     *
     * @param seperated
     * @return
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
