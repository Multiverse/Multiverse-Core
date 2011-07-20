package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationManipulation {

    /**
     * Convert a Location into a Colon separated string to allow us to store it in text.
     *
     * @param location
     * @return
     */
    public String locationToString(Location location) {
        StringBuilder l = new StringBuilder();
        l.append(location.getBlockX() + ":");
        l.append(location.getBlockY() + ":");
        l.append(location.getBlockZ() + ":");
        l.append(location.getYaw() + ":");
        l.append(location.getPitch());
        return l.toString();
    }

    /**
     * Convert a String to a Location.
     *
     * @param world
     * @param xStr
     * @param yStr
     * @param zStr
     * @param yawStr
     * @param pitchStr
     * @return
     */
    public Location stringToLocation(World world, String xStr, String yStr, String zStr, String yawStr, String pitchStr) {
        double x = Double.parseDouble(xStr);
        double y = Double.parseDouble(yStr);
        double z = Double.parseDouble(zStr);
        float yaw = Float.valueOf(yawStr).floatValue();
        float pitch = Float.valueOf(pitchStr).floatValue();

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Convert a Location to XYZ Coordinates.
     *
     * @param l
     * @return
     */
    public String strCoords(Location l) {
        String result = "";
        result += "X: " + l.getBlockX() + " ";
        result += "Y: " + l.getBlockY() + " ";
        result += "Z: " + l.getBlockZ() + " ";
        return result;
    }

    /**
     * Return the NESW Direction a Location is facing.
     *
     * @param location
     * @return
     */
    public String getDirection(Location location) {
        int r = (int) Math.abs((location.getYaw() - 90) % 360);
        String dir;
        if (r < 23)
            dir = "N";
        else if (r < 68)
            dir = "NE";
        else if (r < 113)
            dir = "E";
        else if (r < 158)
            dir = "SE";
        else if (r < 203)
            dir = "S";
        else if (r < 248)
            dir = "SW";
        else if (r < 293)
            dir = "W";
        else if (r < 338)
            dir = "NW";
        else
            dir = "N";

        return dir;
    }
}
