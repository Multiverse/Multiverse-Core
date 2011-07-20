package com.onarandombox.MultiverseCore;

import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class MVPlayerSession {

    private Player player; // Player holder, may be unnecessary.

    private Long teleportLast = 0L; // Timestamp for the Players last Portal Teleportation.
    private Long messageLast = 0L; // Timestamp for the Players last Alert Message.

    // private Location bedSpawn;
    //
    // // Beds are 2 blocks, thus we need to store both places
    // private Location bedA;
    // private Location bedB;

    private Configuration config; // Configuration file to find out Cooldown Timers.

    public MVPlayerSession(Player player, Configuration config, MultiverseCore multiVerseCore) {
        this.player = player;
        this.config = config;
        // this.bedSpawn = null;
    }

    /**
     * Update the Teleport time.
     */
    public void teleport() {
        this.teleportLast = (new Date()).getTime();
    }

    /**
     * Grab whether the cooldown on Portal use has expired or not.
     * 
     * @return
     */
    public boolean getTeleportable() {
        Long time = (new Date()).getTime();
        if ((time - this.teleportLast) > this.config.getInt("portalcooldown", 5000)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Send a Message to the Player as long as enough time has passed since the last message.
     * 
     * @param msg
     */
    public void message(String msg) {
        Long time = (new Date()).getTime();
        if ((time - this.messageLast) > this.config.getInt("messagecooldown", 2000)) {
            this.player.sendMessage(msg);
            this.messageLast = time;
        }
    }
    
    // Commented out bed code, i'll get rid of it soon.
    // --FF
    
    // public void setRespawnLocation(Location location) {
    // this.bedSpawn = location;
    // }

    // // This one simply spawns the player closer to the bed.
    // public Location getBedRespawnLocation() {
    // // There is a bedrespawn set
    // if (this.bedSpawn != null) {
    // if (!this.bs.playerCanSpawnHereSafely(this.bedSpawn) || !bedStillExists(this.bedSpawn)) {
    // this.bedSpawn = null;
    // return this.bedSpawn;
    // }
    // Location actualRespawn = this.bedSpawn;
    // Location bedRespawn = new Location(actualRespawn.getWorld(), actualRespawn.getX(), actualRespawn.getY(), actualRespawn.getZ());
    // bedRespawn.setY(bedRespawn.getY() - .25);
    // return bedRespawn;
    // }
    // return null;
    // }
    //
    // private boolean bedStillExists(Location bedSpawn) {
    // //System.out.print("Dangers:");
    // //this.bs.showDangers(bedSpawn);
    // Location locationDown = new Location(bedSpawn.getWorld(), bedSpawn.getX(), bedSpawn.getY(), bedSpawn.getZ());
    // locationDown.setY(locationDown.getY() - 1);
    // if (locationDown.getBlock().getType() != Material.BED_BLOCK) {
    // return false;
    // }
    // return true;
    // }
}
