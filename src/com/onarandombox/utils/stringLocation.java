package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class stringLocation {
        
    public Location stringToLocation(World world, String xStr, String yStr, String zStr, String yawStr, String pitchStr){
        double x = Double.parseDouble(xStr);
        double y = Double.parseDouble(yStr);
        double z = Double.parseDouble(zStr);
        float yaw = Float.valueOf(yawStr).floatValue();
        float pitch = Float.valueOf(pitchStr).floatValue();

        return new Location(world, x, y, z, yaw, pitch);
    }

    public String locationToString(Location location) {
        StringBuilder l = new StringBuilder();
        l.append(location.getBlockX() + ":");
        l.append(location.getBlockY() + ":");
        l.append(location.getBlockZ() + ":");
        l.append(location.getYaw() + ":");
        l.append(location.getPitch());
        return l.toString();
    }
    
}
