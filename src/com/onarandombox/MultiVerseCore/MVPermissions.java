package com.onarandombox.MultiVerseCore;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class MVPermissions {

    private MultiVerseCore plugin;
    
    /**
     * Constructor FTW
     * @param plugin Pass along the Core Plugin.
     */
    public MVPermissions(MultiVerseCore plugin){
        this.plugin = plugin;
    }

    /**
     * Check if a Player can teleport to the Destination world from there
     * current world. This checks against the Worlds Blacklist
     * 
     * @param p
     * @param w
     * @return
     */
    public Boolean canTravelFromWorld(Player p, World w) {
        List<String> blackList = this.plugin.worlds.get(w.getName()).worldBlacklist;

        boolean returnValue = true;

        if (blackList.size() == 0) {
            returnValue = true;
        }

        for (int i = 0; i < blackList.size(); i++) {
            if (blackList.get(i).equalsIgnoreCase(p.getWorld().getName())) {
                returnValue = false;
                break;
            }
        }

        return returnValue;
    }
    
    /**
     * Check if the Player has the permissions to enter this world.
     * @param p
     * @param w
     * @return
     */
    public Boolean canEnterWorld(Player p, World w) {
        // First check if we've got the Permissions plugin, we can't perform the group checks without it.
        if(MultiVerseCore.Permissions==null) {
            return true; // If we don't have it we must return true otherwise we are forcing people to use the Permissions plugin.
        }
        
        List<String> whiteList = this.plugin.worlds.get(w.getName()).joinWhitelist;
        List<String> blackList = this.plugin.worlds.get(w.getName()).joinBlacklist;
        @SuppressWarnings("deprecation")
        String group = MultiVerseCore.Permissions.getGroup(p.getName());
        
        boolean returnValue = true;

        // TODO: Not sure if I want this.
        if (whiteList.size() > 0) {
            returnValue = false;
        }

        for (int i = 0; i < whiteList.size(); i++){
            if (whiteList.get(i).contains("g:") && group.equalsIgnoreCase(whiteList.get(i).split(":")[1])) {
                returnValue = true;
                break;
            }
        }

        for (int i = 0; i < blackList.size(); i++){
            if (blackList.get(i).contains("g:") && group.equalsIgnoreCase(blackList.get(i).split(":")[1])) {
                returnValue = false;
                break;
            }
        }

        for (int i = 0; i < whiteList.size(); i++){
            if (whiteList.get(i).equalsIgnoreCase(p.getName())) {
                returnValue = true;
                break;
            }
        }

        for (int i = 0; i < blackList.size(); i++){
            if (blackList.get(i).equalsIgnoreCase(p.getName())) {
                returnValue = false;
                break;
            }
        }
        
        return returnValue;
    }
}
