package com.onarandombox.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class ExactDestination extends Destination {
    private final String coordRegex = "(-?[\\d]+\\.?[\\d]*),(-?[\\d]+\\.?[\\d]*),(-?[\\d]+\\.?[\\d]*)";
    private boolean isValid;
    private Location location;

    @Override
    public String getIdentifer() {
        return "e";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        if (!(plugin instanceof MultiverseCore)) {
            return false;
        }
        System.out.print("Checking Exact Dest");
        List<String> parsed = Arrays.asList(destination.split(":"));
        // Need at least: e:world:x,y,z
        // OR e:world:x,y,z:pitch:yaw
        // so basically 3 or 5
        if (!(parsed.size() == 3 || parsed.size() == 5)) {
            System.out.print("Invalid Args:" + parsed.size());
            return false;
        }
        // If it's not an Exact type
        if (!parsed.get(0).equalsIgnoreCase("e")) {
            System.out.print("No E found");
            return false;
        }

        // If it's not a MV world
        if (!((MultiverseCore)plugin).isMVWorld(parsed.get(1))) {
            System.out.print("Not a MV world");
            return false;
        }

        if (!parsed.get(2).matches(coordRegex)) {
            System.out.print("Invalid Regex");
            return false;
        }
        // This is 1 now, because we've removed 2
        if (parsed.size() == 3) {
            return true;
        }

        try {
            Float.parseFloat(parsed.get(3));
            Float.parseFloat(parsed.get(4));
        } catch (NumberFormatException e) {
            return false;
        }

        try {

        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public void setDestination(JavaPlugin plugin, String dest) {
        if (!(plugin instanceof MultiverseCore)) {
            return;
        }
        List<String> parsed = Arrays.asList(dest.split(":"));
        // Need at least: e:world:x,y,z
        // OR e:world:x,y,z:pitch:yaw
        // so basically 3 or 5
        if (!(parsed.size() == 3 || parsed.size() == 5)) {
            this.isValid = false;
            return;
        }

        if (!parsed.get(0).equalsIgnoreCase(this.getIdentifer())) {
            this.isValid = false;
            return;
        }

        if (!((MultiverseCore)plugin).isMVWorld(parsed.get(1))) {
            this.isValid = false;
            return;
        }
        this.location = new Location(((MultiverseCore)plugin).getMVWorld(parsed.get(1)).getCBWorld(), 0, 0, 0);

        if (!parsed.get(2).matches(this.coordRegex)) {
            this.isValid = false;
            return;
        }
        double[] coords = new double[3];
        String[] coordString = parsed.get(2).split(",");
        for (int i = 0; i < 3; i++) {
            try {
                coords[i] = Double.parseDouble(coordString[i]);
            } catch (NumberFormatException e) {
                this.isValid = false;
                return;
            }
        }
        this.location.setX(coords[0]);
        this.location.setY(coords[1]);
        this.location.setZ(coords[2]);

        if (parsed.size() == 3) {
            this.isValid = true;
            return;
        }

        try {
            this.location.setPitch(Float.parseFloat(parsed.get(3)));
            this.location.setYaw(Float.parseFloat(parsed.get(4)));
        } catch (NumberFormatException e) {
            this.isValid = false;
            return;
        }
        this.isValid = true;

    }

    @Override
    public String getType() {
        return "Exact";
    }

    @Override
    public String getName() {
        return "Exact (" + this.location.getX() + ", " + this.location.getY() + ", " + this.location.getZ() + ")";
    }

    public void setDestination(Location location) {
        if (location != null) {
            this.location = location;
            this.isValid = false;
        }
        this.isValid = true;
    }

    @Override
    public String toString() {
        if (isValid) {
            return "e:" + location.getWorld().getName() + ":" + location.getX() + "," + location.getY() + "," + location.getZ() + ":    " + location.getX() + ":" + location.getX();
        }
        return "i:Invalid Destination";
    }
}
