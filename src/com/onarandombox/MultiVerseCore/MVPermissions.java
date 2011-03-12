package com.onarandombox.MultiVerseCore;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class MVPermissions {

    private MultiVerseCore plugin;
    
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

        if (blackList.size() == 0)
            returnValue = true;

        for (int i = 0; i < blackList.size(); i++)
            if (blackList.get(i).equalsIgnoreCase(p.getWorld().getName())) {
                returnValue = false;
                break;
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
        List<String> whiteList = this.plugin.worlds.get(w.getName()).joinWhitelist;
        List<String> blackList = this.plugin.worlds.get(w.getName()).joinBlacklist;
        String group = MultiVerseCore.Permissions.getGroup(p.getName());
        
        boolean returnValue = true;

        if (whiteList.size() > 0)
            returnValue = false;

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
