package com.onarandombox.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.onarandombox.MultiverseCore.MultiverseCore;
@Deprecated
public class OldDestination {
    private String name;
    private Location location;
    private DestinationType type;
    private World world;

    public OldDestination(String name, DestinationType type) {
        this.name = name;
        this.type = type;

        if (name.split(":").length > 1) {
            this.type = DestinationType.Invalid;
        }
    }

    public OldDestination(String coordString) {
        this.type = DestinationType.Invalid;
//        if (this.setExactDest(coordString.split(":"))) {
//            this.type = DestinationType.Exact;
//        }
    }

    @Override
    public String toString() {
//        if (this.type == DestinationType.Portal) {
//            return "p:" + this.name;
//        } else if (this.type == DestinationType.World) {
//            return "w:" + this.name;
//        } else if (this.type == DestinationType.Exact) {
//            return "e:" + this.name + ":" + this.x + "," + this.y + "," + this.z + ":" + this.pitch + ":" + this.yaw;
//        }
        return "i:" + this.name;

    }

    public String getName() {
        return this.name;
    }

    public DestinationType getType() {
        return this.type;
    }

    private static OldDestination getBadDestination() {
        return new OldDestination("", DestinationType.Invalid);
    }

    /**
     * Takes the given string and returns a Destination. This will NEVER be NULL. It will return a destination of type Invalid if the destination is bad.
     * 
     * @param dest The parceable string, ex: w:My World
     * @param plugin The MultiverseCore plugin used to find valid worlds/portals
     * @return A new Destination from the parsed string.
     */
    public static OldDestination parseDestination(String dest, MultiverseCore plugin) {
        if (dest == null) {
            return getBadDestination();
        }

        String[] items = dest.split(":");
        if (items.length == 0) {
            return getBadDestination();
        }
        if (items.length == 1 && items[0].equalsIgnoreCase("here")) {
            return new OldDestination(items[0], DestinationType.Exact);
        }

        // If we only found one param, assume world, but still check for validity
        if (items.length == 1 && plugin.isMVWorld(items[0])) {
            return new OldDestination(items[0], DestinationType.World);
        }

        if (items[0].equalsIgnoreCase("w") && plugin.isMVWorld(items[1])) {
            return new OldDestination(items[1], DestinationType.World);
        } else if (items[0].equalsIgnoreCase("p")) {
            // TODO: Check for a valid portal, we can't right now, as portals aren't really in yet.
            return new OldDestination(items[1], DestinationType.Portal);
        } else if (items[0].equalsIgnoreCase("e")) {
            return new OldDestination(dest);
        }
        System.out.print("Nothing valid found!!");
        return getBadDestination();
    }

//    private boolean setExactDest(String[] items) {
//        List<String> parsed = Arrays.asList(items);
//        // we already know this is an 'e'
//
//        parsed.remove(0);
//        // e:name:x,y,z:pitch:yaw
//        this.name = parsed.remove(0);
//        this.
//        String[] coordString = parsed.get(0).split(",");
//        Double[] coords = new Double[3];
//        for (int i = 0; i < 3; i++) {
//            try {
//                coords[i] = Double.parseDouble(coordString[0]);
//            } catch (NumberFormatException e) {
//                return false;
//            }
//        }
//        if (parsed.size() == 1) {
//            // We parsed with X, Y, And Z, we're good to go!
//            location = new Location(this.world, coords[0], coords[1], coords[2]);
//            return true;
//        }
//        if (parsed.size() == 3) {
//            try {
//                this.pitch = Double.parseDouble(parsed.get(1));
//                this.yaw = Double.parseDouble(parsed.get(1));
//            } catch (NumberFormatException e) {
//                return false;
//            }
//            return true;
//        }
//        return false;
//    }
}
