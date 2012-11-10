/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class to manipulate locations.
 *
 * @deprecated Use instead: {@link com.onarandombox.MultiverseCore.api.LocationManipulation} and {@link SimpleLocationManipulation}.
 */
@Deprecated
public class LocationManipulation {
    private LocationManipulation() { }

    private static Map<String, Integer> orientationInts = new HashMap<String, Integer>();

    static {
        // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        orientationInts.put("n", 180);
        orientationInts.put("ne", 225);
        orientationInts.put("e", 270);
        orientationInts.put("se", 315);
        orientationInts.put("s", 0);
        orientationInts.put("sw", 45);
        orientationInts.put("w", 90);
        orientationInts.put("nw", 135);

        // "freeze" the map:
        orientationInts = Collections.unmodifiableMap(orientationInts);
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
    }

    /**
     * Convert a Location into a Colon separated string to allow us to store it in text.
     * <p>
     * WORLD:X,Y,Z:yaw:pitch
     * <p>
     * The corresponding String2Loc function is {@link #stringToLocation}
     *
     * @param location The Location to save.
     * @return The location as a string in this format: WORLD:x,y,z:yaw:pitch
     */
    public static String locationToString(Location location) {
        if (location == null) {
            return "";
        }
        // We set the locale to ENGLISH here so we always save with the format:
        // world:1.2,5.4,3.6:1.8:21.3
        // Otherwise we blow up when parsing!
        return String.format(Locale.ENGLISH, "%s:%.2f,%.2f,%.2f:%.2f:%.2f", location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * This method simply does some rounding, rather than forcing a call to the server to get the blockdata.
     *
     * @param l The location to round to the block location
     * @return A rounded location.
     */
    public static Location getBlockLocation(Location l) {
        l.setX(l.getBlockX());
        l.setY(l.getBlockY());
        l.setZ(l.getBlockZ());
        return l;
    }

    /**
     * Returns a new location from a given string. The format is as follows:
     * <p>
     * WORLD:X,Y,Z:yaw:pitch
     * <p>
     * The corresponding Location2String function is {@link #stringToLocation}
     *
     * @param locationString The location represented as a string (WORLD:X,Y,Z:yaw:pitch)
     * @return A new location defined by the string or null if the string was invalid.
     */
    public static Location stringToLocation(String locationString) {
        //format:
        //world:x,y,z:pitch:yaw
        if (locationString == null) {
            return null;
        }

        // Split the whole string, format is:
        // {'world', 'x,y,z'[, 'pitch', 'yaw']}
        String[] split = locationString.split(":");
        if (split.length < 2 || split.length > 4) { // SUPPRESS CHECKSTYLE: MagicNumberCheck
            return null;
        }
        // Split the xyz string, format is:
        // {'x', 'y', 'z'}
        String[] xyzsplit = split[1].split(",");
        if (xyzsplit.length != 3) {
            return null;
        }

        // Verify the world is valid
        World w = Bukkit.getWorld(split[0]);
        if (w == null) {
            return null;
        }

        try {
            float pitch = 0;
            float yaw = 0;
            if (split.length >= 3) {
                yaw = (float) Double.parseDouble(split[2]);
            }
            if (split.length == 4) { // SUPPRESS CHECKSTYLE: MagicNumberCheck
                pitch = (float) Double.parseDouble(split[3]);
            }
            return new Location(w, Double.parseDouble(xyzsplit[0]), Double.parseDouble(xyzsplit[1]), Double.parseDouble(xyzsplit[2]), yaw, pitch);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns a colored string with the coords.
     *
     * @param l The {@link Location}
     * @return The {@link String}
     */
    public static String strCoords(Location l) {
        String result = "";
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(2);
        result += ChatColor.WHITE + "X: " + ChatColor.AQUA + df.format(l.getX()) + " ";
        result += ChatColor.WHITE + "Y: " + ChatColor.AQUA + df.format(l.getY()) + " ";
        result += ChatColor.WHITE + "Z: " + ChatColor.AQUA + df.format(l.getZ()) + " ";
        result += ChatColor.WHITE + "P: " + ChatColor.GOLD + df.format(l.getPitch()) + " ";
        result += ChatColor.WHITE + "Y: " + ChatColor.GOLD + df.format(l.getYaw()) + " ";
        return result;
    }

    /**
     * Converts a location to a printable readable formatted string including pitch/yaw.
     *
     * @param l The {@link Location}
     * @return The {@link String}
     */
    public static String strCoordsRaw(Location l) {
        if (l == null) {
            return "null";
        }
        String result = "";
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(2);
        result += "X: " + df.format(l.getX()) + " ";
        result += "Y: " + df.format(l.getY()) + " ";
        result += "Z: " + df.format(l.getZ()) + " ";
        result += "P: " + df.format(l.getPitch()) + " ";
        result += "Y: " + df.format(l.getYaw()) + " ";
        return result;
    }

    /**
     * Return the NESW Direction a Location is facing.
     *
     * @param location The {@link Location}
     * @return The NESW Direction
     */
    public static String getDirection(Location location) {
        // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        double r = (location.getYaw() % 360) + 180;
        // Remember, these numbers are every 45 degrees with a 22.5 offset, to detect boundaries.
        String dir;
        if (r < 22.5)
            dir = "n";
        else if (r < 67.5)
            dir = "ne";
        else if (r < 112.5)
            dir = "e";
        else if (r < 157.5)
            dir = "se";
        else if (r < 202.5)
            dir = "s";
        else if (r < 247.5)
            dir = "sw";
        else if (r < 292.5)
            dir = "w";
        else if (r < 337.5)
            dir = "nw";
        else
            dir = "n";
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck

        return dir;
    }

    /**
     * Returns the float yaw position for the given cardinal direction.
     *
     * @param orientation The cardinal direction
     * @return The yaw
     */
    public static float getYaw(String orientation) {
        if (orientation == null) {
            return 0;
        }
        if (orientationInts.containsKey(orientation.toLowerCase())) {
            return orientationInts.get(orientation.toLowerCase());
        }
        return 0;
    }

    /**
     * Returns a speed float from a given vector.
     *
     * @param v The {@link Vector}
     * @return The speed
     */
    public static float getSpeed(Vector v) {
        return (float) Math.sqrt(v.getX() * v.getX() + v.getZ() * v.getZ());
    }

    // X, Y, Z
    // -W/+E,0, -N/+S

    /**
     * Returns a translated vector from the given direction.
     *
     * @param v The old {@link Vector}
     * @param direction The new direction
     * @return The translated {@link Vector}
     */
    public static Vector getTranslatedVector(Vector v, String direction) {
        if (direction == null) {
            return v;
        }
        float speed = getSpeed(v);
        float halfSpeed = (float) (speed / 2.0);
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

    /**
     * Returns the next Location that a {@link Vehicle} is traveling at.
     *
     * @param v The {@link Vehicle}
     * @return The {@link Location}
     */
    public static Location getNextBlock(Vehicle v) {
        Vector vector = v.getVelocity();
        Location location = v.getLocation();
        int x = vector.getX() < 0 ? vector.getX() == 0 ? 0 : -1 : 1;
        int z = vector.getZ() < 0 ? vector.getZ() == 0 ? 0 : -1 : 1;
        return location.add(x, 0, z);
    }
}
