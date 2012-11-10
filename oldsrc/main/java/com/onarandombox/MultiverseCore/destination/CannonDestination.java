/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

/**
 * A cannon-{@link MVDestination}.
 */
public class CannonDestination implements MVDestination {
    private final String coordRegex = "(-?[\\d]+\\.?[\\d]*),(-?[\\d]+\\.?[\\d]*),(-?[\\d]+\\.?[\\d]*)";
    private boolean isValid;
    private Location location;
    private double speed;

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getVelocity() {
        double pitchRadians = Math.toRadians(location.getPitch());
        double yawRadians = Math.toRadians(location.getYaw());
        double x = Math.sin(yawRadians) * speed * -1;
        double y = Math.sin(pitchRadians) * speed * -1;
        double z = Math.cos(yawRadians) * speed;
        // Account for the angle they were pointed, and take away velocity
        x = Math.cos(pitchRadians) * x;
        z = Math.cos(pitchRadians) * z;
        return new Vector(x, y, z);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return "ca";
    }

    // NEED ca:world:x,y,z:pitch:yaw:speed
    // so basically 6
    private static final int SPLIT_SIZE = 6;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        if (!(plugin instanceof MultiverseCore)) {
            return false;
        }
        List<String> parsed = Arrays.asList(destination.split(":"));
        if (parsed.size() != SPLIT_SIZE) {
            return false;
        }
        // If it's not an Cannon type
        if (!parsed.get(0).equalsIgnoreCase("ca")) {
            return false;
        }

        // If it's not a MV world
        if (!((MultiverseCore) plugin).getMVWorldManager().isMVWorld(parsed.get(1))) {
            return false;
        }
        // Verify X,Y,Z are numbers
        if (!parsed.get(2).matches(coordRegex)) {
            return false;
        }

        try {
            // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
            Float.parseFloat(parsed.get(3));
            Float.parseFloat(parsed.get(4));
            Float.parseFloat(parsed.get(5));
            // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getLocation(Entity e) {
        return this.location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return this.isValid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDestination(JavaPlugin plugin, String destination) {
        if (!(plugin instanceof MultiverseCore)) {
            return;
        }
        List<String> parsed = Arrays.asList(destination.split(":"));

        if (parsed.size() != SPLIT_SIZE) {
            this.isValid = false;
            return;
        }
        if (!parsed.get(0).equalsIgnoreCase(this.getIdentifier())) {
            this.isValid = false;
            return;
        }

        if (!((MultiverseCore) plugin).getMVWorldManager().isMVWorld(parsed.get(1))) {
            this.isValid = false;
            return;
        }

        this.location = new Location(((MultiverseCore) plugin).getMVWorldManager().getMVWorld(parsed.get(1)).getCBWorld(), 0, 0, 0);

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
            // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
            this.location.setPitch(Float.parseFloat(parsed.get(3)));
            this.location.setYaw(Float.parseFloat(parsed.get(4)));
            this.speed = Math.abs(Float.parseFloat(parsed.get(5)));
            // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        } catch (NumberFormatException e) {
            this.isValid = false;
            return;
        }

        this.isValid = true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "Cannon!";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Cannon (" + this.location.getX() + ", " + this.location.getY() + ", " + this.location.getZ() + ":"
                + this.location.getPitch() + ":" + this.location.getYaw() + ":" + this.speed + ")";

    }

    /**
     * Sets this {@link CannonDestination}.
     *
     * @param location The {@link Location}.
     * @param speed The speed.
     */
    public void setDestination(Location location, double speed) {
        if (location != null) {
            this.location = location;
            this.speed = Math.abs(speed);
            this.isValid = true;
        }
        this.isValid = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequiredPermission() {
        return "multiverse.access." + this.location.getWorld().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean useSafeTeleporter() {
        return false;
    }

    @Override
    public String toString() {
        if (isValid) {
            return "ca:" + location.getWorld().getName() + ":" + location.getX() + "," + location.getY()
                    + "," + location.getZ() + ":" + location.getPitch() + ":" + location.getYaw() + ":" + this.speed;
        }
        return "i:Invalid Destination";
    }
}
