package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Destination {
    public abstract String getIdentifer();
    public abstract boolean isThisType(JavaPlugin plugin, String dest);
    public abstract Location getLocation();
    public abstract boolean isValid();
    public abstract void setDestination(JavaPlugin plugin, String dest);
    public abstract String getType();
    public abstract String getName();
    public abstract String toString();
}
