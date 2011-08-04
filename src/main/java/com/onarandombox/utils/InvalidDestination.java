package com.onarandombox.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class InvalidDestination implements MVDestination {

    @Override
    public String getIdentifer() {
        return "i";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String dest) {
        return false;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void setDestination(JavaPlugin plugin, String dest) {
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

}
