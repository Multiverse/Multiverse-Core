package com.onarandombox.MultiVerseCore;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class MVPlayerSession {

    private Player player; // Player holder, may be unnecessary.
    public Location loc; // Contain the Players Location so on player move we can compare this and check if they've moved a block.
    
    public String portal = null; // Allow a player to target a portal to prevent them typing its name every command.
    
    public Location coord1 = null; // Coordinate 1 (Left Click)
    public Location coord2 = null; // Coordinate 2 (Right Click)
    
    private Long teleportLast = 0L; // Timestamp for the Players last Portal Teleportation.
    private Long messageLast = 0L; // Timestamp for the Players last Alert Message.
    
    private Configuration config; // Configuration file to find out Cooldown Timers.
    
    public MVPlayerSession(Player player, Configuration config, MultiVerseCore multiVerseCore) {
        this.player = player;
        this.loc = player.getLocation();
        this.config = config;
    }
    
    /**
     * Teleport the Player to the destination as long as enough time has passed since the last teleport.
     * @param location
     */
    public void teleport(Location location){
        Long time = (new Date()).getTime();
        if ((time - this.teleportLast) > config.getInt("portalcooldown", 5000)){
            this.player.teleportTo(location);
            this.teleportLast = time;
        }
    }
    
    /**
     * Send a Message to the Player as long as enough time has passed since the last message.
     * @param msg
     */
    public void message(String msg){
        Long time = (new Date()).getTime();
        if((time - this.messageLast) > config.getInt("messagecooldown", 2000)){
            this.player.sendMessage(msg);
            this.messageLast = time;
        }
    }
}
