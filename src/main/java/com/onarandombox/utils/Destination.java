package com.onarandombox.utils;

import org.bukkit.Location;

import com.onarandombox.MultiverseCore.MultiverseCore;

public abstract class Destination {
    public abstract String getIdentifer();
    public abstract boolean isThisType(MultiverseCore plugin, String dest);
    public abstract Location getLocation();
    public abstract boolean isValid();
    public abstract void setDestination(MultiverseCore plugin, String dest);
    public abstract String getType();
    public abstract String getName();
}
