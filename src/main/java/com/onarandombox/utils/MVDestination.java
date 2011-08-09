package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public interface MVDestination {
    public String getIdentifer();
    public boolean isThisType(JavaPlugin plugin, String dest);
    public Location getLocation(Entity e);
    public boolean isValid();
    public void setDestination(JavaPlugin plugin, String dest);
    public String getType();
    public String getName();
    public String toString();
    public String getRequiredPermission();
    public Vector getVelocity();
}
