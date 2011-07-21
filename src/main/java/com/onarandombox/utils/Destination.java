package com.onarandombox.utils;

import java.util.Arrays;
import java.util.List;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class Destination {
    private String name;
    private double x;
    private double y;
    private double z;
    private double pitch;
    private double yaw;
    private DestinationType type;

    public Destination(String name, DestinationType type) {
        this.name = name;
        this.type = type;

        if (name.split(":").length > 1) {
            this.type = DestinationType.Invalid;
        }
    }

    public Destination(String coordString) {
        this.type = DestinationType.Invalid;
        if(this.setExactDest(coordString.split(":"))) {
            this.type = DestinationType.Exact;
        }
    }

    @Override
    public String toString() {
        if (this.type == DestinationType.Portal) {
            return "p:" + this.name;
        } else if (this.type == DestinationType.World) {
            return "w:" + this.name;
        } else if (this.type == DestinationType.Exact) {
            return "e:" + this.x + "," + this.y + "," + this.z + ":" + this.pitch + ":" + this.yaw;
        }
        return "i:" + this.name;

    }

    public String getName() {
        return this.name;
    }

    public DestinationType getType() {
        return this.type;
    }

    private static Destination getBadDestination() {
        return new Destination("", DestinationType.Invalid);
    }

    /**
     * Takes the given string and returns a Destination. This will NEVER be NULL. It will return a destination of type Invalid if the destination is bad.
     * 
     * @param dest The parceable string, ex: w:My World
     * @param plugin The MultiverseCore plugin used to find valid worlds/portals
     * @return A new Destination from the parsed string.
     */
    public static Destination parseDestination(String dest, MultiverseCore plugin) {
        if (dest == null) {
            return getBadDestination();
        }

        String[] items = dest.split(":");
        if (items.length == 0) {
            return getBadDestination();
        }
        if (items.length == 1 && items[0].equalsIgnoreCase("here")) {
            return new Destination(items[0], DestinationType.Exact);
        }

        // If we only found one param, assume world, but still check for validity
        if (items.length == 1 && plugin.isMVWorld(items[0])) {
            return new Destination(items[0], DestinationType.World);
        }

        if (items[0].equalsIgnoreCase("w") && plugin.isMVWorld(items[1])) {
            return new Destination(items[1], DestinationType.World);
        } else if (items[0].equalsIgnoreCase("p")) {
            // TODO: Check for a valid portal, we can't right now, as portals aren't really in yet.
            return new Destination(items[1], DestinationType.Portal);
        } else if (items[0].equalsIgnoreCase("e")) {
            return new Destination(dest);
        }
        System.out.print("Nothing valid found!!");
        return getBadDestination();
    }

    private boolean setExactDest(String[] items) {
        List<String> parsed = Arrays.asList(items);
        // we already know this is an 'e'
        // e:x,y,z:pitch:yaw
        parsed.remove(0);
        String[] coordString = parsed.get(0).split(",");
        Double[] coords = new Double[3];
        for (int i = 0; i < 3; i++) {
            try {
                coords[i] = Double.parseDouble(coordString[0]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        x = coords[0];
        y = coords[1];
        z = coords[2];
        if (parsed.size() == 1) {
            // We parsed with X, Y, And Z, we're good to go!
            return true;
        }
        if (parsed.size() == 3) {
            try {
                this.pitch = Double.parseDouble(parsed.get(1));
                this.yaw = Double.parseDouble(parsed.get(1));
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
