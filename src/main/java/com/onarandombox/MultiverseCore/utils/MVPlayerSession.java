/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import java.util.Date;

public class MVPlayerSession {

    @SuppressWarnings("unused") //maybe used in the future?
    private Player player; // Player holder, may be unnecessary.

    private Long teleportLast = 0L; // Timestamp for the Players last Portal Teleportation.
    //private Long messageLast = 0L; // Timestamp for the Players last Alert Message. We don't need this until now so we comment it out.

    private Configuration config; // Configuration file to find out Cooldown Timers.

    public MVPlayerSession(Player player, Configuration config, MultiverseCore multiVerseCore) {
        this.player = player;
        this.config = config;
        // this.bedSpawn = null;
    }

    /** Update the Teleport time. */
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
}
