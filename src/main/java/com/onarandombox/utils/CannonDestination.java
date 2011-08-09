package com.onarandombox.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class CannonDestination implements MVDestination {
    private final String coordRegex = "(-?[\\d]+\\.?[\\d]*),(-?[\\d]+\\.?[\\d]*),(-?[\\d]+\\.?[\\d]*)";
    private boolean isValid;
    private Location location;
    private double speed;

    public Vector getVelocity() {
        double x = Math.sin(Math.toRadians(location.getYaw())) * speed * -1;
        double y = Math.sin(Math.toRadians(location.getPitch())) * speed * -1;
        double z = Math.cos(Math.toRadians(location.getYaw())) * speed;
        x = Math.cos(Math.toRadians(location.getPitch())) * x;
        z = Math.cos(Math.toRadians(location.getPitch())) * z;
        return new Vector(x, y, z);
    }

    @Override
    public String getIdentifer() {
        return "ca";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        if (!(plugin instanceof MultiverseCore)) {
            return false;
        }
        List<String> parsed = Arrays.asList(destination.split(":"));
        // NEED ca:world:x,y,z:pitch:yaw:speed
        // so basically 6
        if (parsed.size() != 6) {
            return false;
        }
        // If it's not an Cannon type
        if (!parsed.get(0).equalsIgnoreCase("ca")) {
            return false;
        }

        // If it's not a MV world
        if (!((MultiverseCore) plugin).isMVWorld(parsed.get(1))) {
            return false;
        }
        // Verify X,Y,Z are numbers
        if (!parsed.get(2).matches(coordRegex)) {
            return false;
        }

        try {
            Float.parseFloat(parsed.get(3));
            Float.parseFloat(parsed.get(4));
            Float.parseFloat(parsed.get(5));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public Location getLocation(Entity e) {
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
        System.out.print(parsed);
        // Need at least: e:world:x,y,z
        // OR e:world:x,y,z:pitch:yaw
        // so basically 3 or 5
        if (parsed.size() != 6) {
            this.isValid = false;
            return;
        }
        if (!parsed.get(0).equalsIgnoreCase(this.getIdentifer())) {
            this.isValid = false;
            return;
        }

        if (!((MultiverseCore) plugin).isMVWorld(parsed.get(1))) {
            this.isValid = false;
            return;
        }

        this.location = new Location(((MultiverseCore) plugin).getMVWorld(parsed.get(1)).getCBWorld(), 0, 0, 0);

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

        try {
            this.location.setPitch(Float.parseFloat(parsed.get(3)));
            this.location.setYaw(Float.parseFloat(parsed.get(4)));
            this.speed = Math.abs(Float.parseFloat(parsed.get(5)));
        } catch (NumberFormatException e) {
            this.isValid = false;
            return;
        }

        this.isValid = true;

    }

    @Override
    public String getType() {
        return "Cannon!";
    }

    @Override
    public String getName() {
        return "Cannon (" + this.location.getX() + ", " + this.location.getY() + ", " + this.location.getZ() + ":" +
                this.location.getPitch() + ":" + this.location.getYaw() + ":" + this.speed + ")";

    }

    public void setDestination(Location location, double speed) {
        if (location != null) {
            this.location = location;
            this.speed = Math.abs(speed);
            this.isValid = true;
        }
        this.isValid = false;
    }

    @Override
    public String toString() {
        if (isValid) {
            return "ca:" + location.getWorld().getName() + ":" + location.getX() + "," + location.getY() + "," + location.getZ() + ":" + location.getPitch() + ":" + location.getYaw() + ":" + this.speed;
        }
        return "i:Invalid Destination";
    }

    @Override
    public String getRequiredPermission() {
        return "multiverse.access." + this.location.getWorld().getName();
    }
}
