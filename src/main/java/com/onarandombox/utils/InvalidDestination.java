package com.onarandombox.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class InvalidDestination extends Destination {

    @Override
    public String getIdentifer() {
        return "i";
    }

    @Override
    public boolean isThisType(String destination) {
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
    public void setDestination(MultiverseCore plugin, String dest) {
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

}
