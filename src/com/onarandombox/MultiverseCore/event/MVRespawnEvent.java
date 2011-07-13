package com.onarandombox.MultiverseCore.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class MVRespawnEvent extends Event {
    private static final long serialVersionUID = -2991894063331856687L;
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
