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
     * Update the Teleport time.
     */
    public void teleport(){
        this.teleportLast = (new Date()).getTime();
    }
    
    /**
     * Grab whether the cooldown on Portal use has expired or not.
     * @return
     */
    public boolean getTeleportable(){
        Long time = (new Date()).getTime();
        if ((time - this.teleportLast) > config.getInt("portalcooldown", 5000)) {
            return true;
        } else {
            return false;
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
