package com.onarandombox.MultiverseCore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.onarandombox.utils.Destination;

public class MVTeleportEvent extends Event {
    private static final long serialVersionUID = 854826818438649269L;
    private Player player;
    private Destination dest;
    private String teleportString;


    public MVTeleportEvent(Destination dest, Player p, String teleportString) {
        super("MVTeleport");
        this.player = p;
        this.dest = dest;
        this.teleportString = teleportString;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getDestString() {
        return this.teleportString;
    }

    public Class<? extends Destination> getDestType() {
        return this.dest.getClass();
    }
}
