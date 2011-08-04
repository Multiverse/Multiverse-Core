package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public interface MVDestination {
    public String getIdentifer();
    public boolean isThisType(JavaPlugin plugin, String dest);
    public Location getLocation();
    public boolean isValid();
    public void setDestination(JavaPlugin plugin, String dest);
    public String getType();
    public String getName();
    public String toString();
    public String getRequiredPermission();
}
