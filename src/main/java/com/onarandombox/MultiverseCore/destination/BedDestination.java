/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.api.MVDestination;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class BedDestination implements MVDestination {

    private boolean isValid;
    private Location knownBedLoc;

    @Override
    public String getIdentifier() {
        return "b";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        String[] split = destination.split(":");
        this.isValid = split.length >= 1 && split.length <= 2 && split[0].equals(this.getIdentifier());
        return this.isValid;
    }

    @Override
    public Location getLocation(Entity entity) {
        if (entity instanceof Player) {
            this.knownBedLoc = ((Player) entity).getBedSpawnLocation();
            return this.knownBedLoc;
        }
        return null;
    }

    @Override
    public Vector getVelocity() {
        return new Vector();
    }

    @Override
    public void setDestination(JavaPlugin plugin, String destination) {
        // Not needed.
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public String getType() {
        return "Bed";
    }

    @Override
    public String getName() {
        return "Bed";
    }

    @Override
    public String getRequiredPermission() {
        if (knownBedLoc != null) {
            return "multiverse.access." + knownBedLoc.getWorld().getName();
        }
        return "";
    }

    @Override
    public boolean useSafeTeleporter() {
        // Bukkit should have already checked this.
        return false;
    }
    @Override
    public String toString() {
        return "b:playerbed";
    }
}
