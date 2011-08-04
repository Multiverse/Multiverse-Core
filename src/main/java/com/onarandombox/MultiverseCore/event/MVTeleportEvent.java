package com.onarandombox.MultiverseCore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.onarandombox.utils.MVDestination;

public class MVTeleportEvent extends Event {
    private static final long serialVersionUID = 854826818438649269L;
    private Player player;
    private MVDestination dest;
    private String teleportString;


    public MVTeleportEvent(MVDestination dest, Player p, String teleportString) {
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

    public Class<? extends MVDestination> getDestType() {
        return this.dest.getClass();
    }
}
