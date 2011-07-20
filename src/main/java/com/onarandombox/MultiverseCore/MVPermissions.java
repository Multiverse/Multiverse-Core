package com.onarandombox.MultiverseCore;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.pneumaticraft.commandhandler.PermissionsInterface;

public class MVPermissions implements PermissionsInterface {

    private MultiverseCore plugin;
    private PermissionHandler permissions = null;

    /**
     * Constructor FTW
     * 
     * @param plugin Pass along the Core Plugin.
     */
    public MVPermissions(MultiverseCore plugin) {
        this.plugin = plugin;
        // We have to see if permissions was loaded before MV was
        if (this.plugin.getServer().getPluginManager().getPlugin("Permissions") != null) {
            this.setPermissions(((Permissions) this.plugin.getServer().getPluginManager().getPlugin("Permissions")).getHandler());
            this.plugin.log(Level.INFO, "- Attached to Permissions");
        }
    }

    /**
     * Check if a Player can teleport to the Destination world from there current world. This checks against the Worlds Blacklist
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

    public void setPermissions(PermissionHandler handler) {
        this.permissions = handler;
    }

    public boolean hasPermission(CommandSender sender, String node, boolean isOpRequired) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        boolean opFallback = this.plugin.getConfig().getBoolean("opfallback", true);
        if (this.permissions != null && this.permissions.has(player, node)) {
            // If Permissions is enabled we check against them.
            return true;
        } else if (sender.hasPermission(node) && !opFallback) {
            // If Now check the bukkit permissions
            // OpFallback must be disabled for this to work
            return true;
        } else if (player.isOp() && opFallback) {
            // If Player is Op we always let them use it if they have the fallback enabled!
            return true;
        }
        // If the Player doesn't have Permissions and isn't an Op then
        // we return true if OP is not required, otherwise we return false
        // This allows us to act as a default permission guidance

        // If they have the op fallback disabled, NO commands will work without a permissions plugin.
        return !isOpRequired && opFallback;

    }

    public String getType() {
        if (this.permissions != null) {
            return "Permissions " + this.plugin.getServer().getPluginManager().getPlugin("Permissions").getDescription().getVersion();
        }
        return "Bukkit Permissions/OPs.txt";
    }
}
