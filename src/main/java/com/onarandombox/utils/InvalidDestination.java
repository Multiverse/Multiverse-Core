package com.onarandombox.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class InvalidDestination implements MVDestination {

    @Override
    public String getIdentifier() {
        return "i";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        return false;
    }

    @Override
    public Location getLocation(Entity e) {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void setDestination(JavaPlugin plugin, String destination) {
        // Nothing needed, it's invalid.
    }

    @Override
    public String getType() {
        return ChatColor.RED + "Invalid Destination";
    }

    @Override
    public String getName() {
        return ChatColor.RED + "Invalid Destination";
    }

    @Override
    public String toString() {
        return "i:Invalid Destination";
    }

    @Override
    public String getRequiredPermission() {
        return null;
    }
    public Vector getVelocity() {
        return new Vector(0,0,0);
    }

    @Override
    public boolean useSafeTeleporter() {
        return false;
    }

}
