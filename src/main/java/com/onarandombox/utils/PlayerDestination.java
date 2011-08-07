package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerDestination implements MVDestination {
    String player;
    private boolean isValid;
    private JavaPlugin plugin;

    @Override
    public String getIdentifer() {
        return "pl";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String dest) {
        String[] items = dest.split(":");
        if (items.length != 2) {
            return false;
        }
        if (!items[0].equalsIgnoreCase("pl")) {
            return false;
        }
        return true;
    }

    @Override
    public Location getLocation(Entity e) {
        Player p = plugin.getServer().getPlayer(this.player);
        Player plLoc = null;
        if (e instanceof Player) {
            plLoc = (Player) e;
        } else if (e.getPassenger() instanceof Player) {
            plLoc = (Player) e.getPassenger();
        }

        if (p != null && plLoc != null && !plLoc.getName().equalsIgnoreCase(p.getName())) {
            return p.getLocation();
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public void setDestination(JavaPlugin plugin, String dest) {
        String[] items = dest.split(":");
        if (items.length != 2) {
            this.isValid = false;
        }
        if (!items[0].equalsIgnoreCase("pl")) {
            this.isValid = false;
        }
        this.isValid = true;
        this.player = items[1];
        this.plugin = plugin;
    }

    @Override
    public String getType() {
        return "Player";
    }

    @Override
    public String getName() {
        return this.player;
    }

    @Override
    public String toString() {
        return "pl:" + this.player;
    }

    @Override
    public String getRequiredPermission() {
        return "";
    }

}
