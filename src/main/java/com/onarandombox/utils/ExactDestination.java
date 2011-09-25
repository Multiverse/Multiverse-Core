/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.utils;

import java.util.Arrays;
import java.util.List;

import com.onarandombox.MultiverseCore.api.MVDestination;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class ExactDestination implements MVDestination {
    private final String coordRegex = "(-?[\\d]+\\.?[\\d]*),(-?[\\d]+\\.?[\\d]*),(-?[\\d]+\\.?[\\d]*)";
    private boolean isValid;
    private Location location;

    @Override
    public String getIdentifier() {
        return "e";
    }

    public Vector getVelocity() {
        return new Vector(0,0,0);
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        if (!(plugin instanceof MultiverseCore)) {
            return false;
        }
        List<String> parsed = Arrays.asList(destination.split(":"));
        // Need at least: e:world:x,y,z
        // OR e:world:x,y,z:pitch:yaw
        // so basically 3 or 5
        if (!(parsed.size() == 3 || parsed.size() == 5)) {
            return false;
        }
        // If it's not an Exact type
        if (!parsed.get(0).equalsIgnoreCase("e")) {
            return false;
        }

        // If it's not a MV world
        if (!((MultiverseCore) plugin).getWorldManager().isMVWorld(parsed.get(1))) {
            return false;
        }

        if (!parsed.get(2).matches(coordRegex)) {
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
    public void setDestination(JavaPlugin plugin, String destination) {
        if (!(plugin instanceof MultiverseCore)) {
            return;
        }
        List<String> parsed = Arrays.asList(destination.split(":"));
        // Need at least: e:world:x,y,z
        // OR e:world:x,y,z:pitch:yaw
        // so basically 3 or 5
        if (!(parsed.size() == 3 || parsed.size() == 5)) {
            this.isValid = false;
            return;
        }

        if (!parsed.get(0).equalsIgnoreCase(this.getIdentifier())) {
            this.isValid = false;
            return;
        }

        if (!((MultiverseCore) plugin).getWorldManager().isMVWorld(parsed.get(1))) {
            this.isValid = false;
            return;
        }
        this.location = new Location(((MultiverseCore) plugin).getWorldManager().getMVWorld(parsed.get(1)).getCBWorld(), 0, 0, 0);

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
        return "Exact (" + this.location.getX() + ", " + this.location.getY() + ", " + this.location.getZ() + ":" + location.getPitch() + ":" + location.getYaw() + ")";
    }

    public void setDestination(Location location) {
        if (location != null) {
            this.location = location;
            this.isValid = true;
        }
        this.isValid = false;
    }

    @Override
    public String toString() {
        if (isValid) {
            return "e:" + location.getWorld().getName() + ":" + location.getX() + "," + location.getY() + "," + location.getZ() + ":" + location.getPitch() + ":" + location.getYaw();
        }
        return "i:Invalid Destination";
    }

    @Override
    public String getRequiredPermission() {
        return "multiverse.access." + this.location.getWorld().getName();
    }

    @Override
    public boolean useSafeTeleporter() {
        // This is an EXACT destination, don't safely teleport here.
        return false;
    }
}
