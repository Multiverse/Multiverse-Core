/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * A bed-{@link MVDestination}.
 */
public class BedDestination implements MVDestination {

    private boolean isValid;
    private Location knownBedLoc;
    private MultiverseCore plugin;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return "b";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        String[] split = destination.split(":");
        this.isValid = split.length >= 1 && split.length <= 2 && split[0].equals(this.getIdentifier());
        return this.isValid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getLocation(Entity entity) {
        if (entity instanceof Player) {
            this.knownBedLoc = this.plugin.getBlockSafety().getSafeBedSpawn(((Player) entity).getBedSpawnLocation());
            if (this.knownBedLoc == null) {
                ((Player) entity).sendMessage("Your bed was " + ChatColor.RED + "invalid or blocked" + ChatColor.RESET + ". Sorry.");
            }
            return this.knownBedLoc;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getVelocity() {
        return new Vector();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDestination(JavaPlugin plugin, String destination) {
        this.plugin = (MultiverseCore) plugin;
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
    public String getType() {
        return "Bed";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Bed";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequiredPermission() {
        if (knownBedLoc != null) {
            return "multiverse.access." + knownBedLoc.getWorld().getName();
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
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
