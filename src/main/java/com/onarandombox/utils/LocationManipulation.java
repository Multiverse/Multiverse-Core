package com.onarandombox.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class LocationManipulation {
    private static Map<String, Integer> orientationInts = new HashMap<String, Integer>();
    static {
        orientationInts.put("n", 180);
        orientationInts.put("ne", 225);
        orientationInts.put("e", 270);
        orientationInts.put("se", 315);
        orientationInts.put("s", 0);
        orientationInts.put("sw", 45);
        orientationInts.put("w", 90);
        orientationInts.put("nw", 135);

    }

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

    public static float getYaw(String orientation) {
        if (orientationInts.containsKey(orientation)) {
            return orientationInts.get(orientation);
        }
        return 0;
    }

    public static float getSpeed(Vector v) {
        return (float) Math.sqrt(v.getX() * v.getX() + v.getZ() * v.getZ());
    }

    // X, Y, Z
    // -W/+E,0, -N/+S
    public static Vector getTranslatedVector(Vector v, String direction) {
        if(direction == null) {
            return v;
        }
        float speed = getSpeed(v);
        float halfSpeed = (float) (speed / 2.0);
        // TODO: Mathmatacize this:
        if (direction.equalsIgnoreCase("n")) {
            return new Vector(0, 0, -1 * speed);
        } else if (direction.equalsIgnoreCase("ne")) {
            return new Vector(halfSpeed, 0, -1 * halfSpeed);
        } else if (direction.equalsIgnoreCase("e")) {
            return new Vector(speed, 0, 0);
        } else if (direction.equalsIgnoreCase("se")) {
            return new Vector(halfSpeed, 0, halfSpeed);
        } else if (direction.equalsIgnoreCase("s")) {
            return new Vector(0, 0, speed);
        } else if (direction.equalsIgnoreCase("sw")) {
            return new Vector(-1 * halfSpeed, 0, halfSpeed);
        } else if (direction.equalsIgnoreCase("w")) {
            return new Vector(-1 * speed, 0, 0);
        } else if (direction.equalsIgnoreCase("nw")) {
            return new Vector(-1 * halfSpeed, 0, -1 * halfSpeed);
        }
        return v;
    }
}
