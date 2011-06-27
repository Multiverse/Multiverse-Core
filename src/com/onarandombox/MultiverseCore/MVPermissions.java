package com.onarandombox.MultiverseCore;

import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MVPermissions {
    
    private MultiverseCore plugin;
    
    /**
     * Constructor FTW
     * 
     * @param plugin Pass along the Core Plugin.
     */
    public MVPermissions(MultiverseCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Use hasPermission() Now
     * 
     * @param p The player instance.
     * @param node The permission node we are checking against.
     * @return
     */
    @Deprecated
    public boolean has(Player p, String node) {
        boolean result = false;
        
        if (MultiverseCore.Permissions != null) {
            result = MultiverseCore.Permissions.has(p, node);
        } else if (p.isOp()) {
            result = true;
        }
        
        return result;
    }
    
    public boolean hasPermission(CommandSender sender, String node, boolean isOpRequired) {
        
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        boolean opFallback = this.plugin.configMV.getBoolean("opfallback", true);
        if (player.isOp() && opFallback) {
            // If Player is Op we always let them use it if they have the fallback enabled!
            return true;
        } else if (MultiverseCore.Permissions != null && MultiverseCore.Permissions.has(player, node)) {
            // If Permissions is enabled we check against them.
            return true;
        }
        // If the Player doesn't have Permissions and isn't an Op then
        // we return true if OP is not required, otherwise we return false
        // This allows us to act as a default permission guidance 
        
        // If they have the op fallback disabled, NO commands will work without a permissions plugin.
        return !isOpRequired && opFallback;
    }
    
    
    /**
     * Check if a Player can teleport to the Destination world from there current world. This checks against the Worlds Blacklist
     * 
     * @param p
     * @param w
     * @return
     */
    public Boolean canTravelFromWorld(Player p, World w) {
        List<String> blackList = this.plugin.getMVWorld(w.getName()).getWorldBlacklist();
        
        boolean returnValue = true;
        
        if (blackList.size() == 0) {
            returnValue = true;
        }
        
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
    public Boolean canEnterWorld(Player p, World w) {
        
        List<String> whiteList = this.plugin.getMVWorld(w.getName()).getPlayerWhitelist();
        List<String> blackList = this.plugin.getMVWorld(w.getName()).getPlayerBlacklist();
        boolean returnValue = true;
        
        // I lied. You definitely want this. Sorry Rigby :( You were right. --FF
        // If there's anyone in the whitelist, then the whitelist is ACTIVE, anyone not in it is blacklisted.
        if (whiteList.size() > 0) {
            returnValue = false;
        }
        for (String bls : blackList) {
            if (bls.toLowerCase().contains("g:") && this.inGroup(p, w.getName(), bls.split(":")[1])) {
                returnValue = false;
                break;
            }
            if (bls.equalsIgnoreCase(p.getName())) {
                returnValue = false;
                break;
            }
        }
        for (String wls : whiteList) {
            if (wls.toLowerCase().contains("g:") && this.inGroup(p, w.getName(), wls.split(":")[1])) {
                returnValue = true;
                break;
            }
            if (wls.equalsIgnoreCase(p.getName())) {
                returnValue = true;
                break;
            }
        }
        return returnValue;
    }
    
    /**
     * Returns true if a player is in a group.
     * 
     * @param player The player to check
     * @param worldName The world to check in
     * @param group The group are we checking
     * @return True if the player is in the group, false if not.
     */
    private boolean inGroup(Player player, String worldName, String group) {
        if (MultiverseCore.Permissions != null) {
            return MultiverseCore.Permissions.inGroup(worldName, player.getName(), group);
        } else {
            return player.isOp();
        }
    }
}
