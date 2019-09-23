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

import com.onarandombox.MultiverseCore.api.LocationManipulation;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The default-implementation of {@link LocationManipulation}.
 */
public class SimpleLocationManipulation implements LocationManipulation {
    private static final Map<String, Integer> ORIENTATION_INTS;

    static {
        Map<String, Integer> orientationInts = new HashMap<String, Integer>();
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
        ORIENTATION_INTS = Collections.unmodifiableMap(orientationInts);
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String locationToString(Location location) {
        if (location == null) {
            return "";
        }
        return String.format(Locale.ENGLISH, "%s:%.2f,%.2f,%.2f:%.2f:%.2f", location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getBlockLocation(Location l) {
        l.setX(l.getBlockX());
        l.setY(l.getBlockY());
        l.setZ(l.getBlockZ());
        return l;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location stringToLocation(String locationString) {
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
     * {@inheritDoc}
     */
    @Override
    public String strCoords(Location l) {
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
     * {@inheritDoc}
     */
    @Override
    public String strCoordsRaw(Location l) {
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
     * {@inheritDoc}
     */
    @Override
    public String getDirection(Location location) {
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
     * {@inheritDoc}
     */
    @Override
    public float getYaw(String orientation) {
        if (orientation == null) {
            return 0;
        }
        if (ORIENTATION_INTS.containsKey(orientation.toLowerCase())) {
            return ORIENTATION_INTS.get(orientation.toLowerCase());
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getSpeed(Vector v) {
        return (float) Math.sqrt(v.getX() * v.getX() + v.getZ() * v.getZ());
    }

    // X, Y, Z
    // -W/+E,0, -N/+S

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getTranslatedVector(Vector v, String direction) {
        if (direction == null) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getNextBlock(Vehicle v) {
        Vector vector = v.getVelocity();
        Location location = v.getLocation();
        int x = vector.getX() < 0 ? vector.getX() == 0 ? 0 : -1 : 1;
        int z = vector.getZ() < 0 ? vector.getZ() == 0 ? 0 : -1 : 1;
        return location.add(x, 0, z);
    }
}
