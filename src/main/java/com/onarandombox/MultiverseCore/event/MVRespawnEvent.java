/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Called
 */
public class MVRespawnEvent extends Event {
    private Player player;
    private Location location;
    private String respawnMethod;


    public MVRespawnEvent(Location spawningAt, Player p, String respawnMethod) {
        super("MVRespawn");
        this.player = p;
        this.location = spawningAt;
        this.respawnMethod = respawnMethod;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getRespawnMethod() {
        return this.respawnMethod;
    }

    public Location getPlayersRespawnLocation() {
        return this.location;
    }

    public void setRespawnLocation(Location l) {
        this.location = l;
    }
}
