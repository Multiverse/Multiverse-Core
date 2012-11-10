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
 * An anchor-{@link MVDestination}.
 */
public class AnchorDestination implements MVDestination {
    private boolean isValid;
    private Location location;
    private MultiverseCore plugin;
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return "a";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getVelocity() {
        return new Vector(0, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        if (!(plugin instanceof MultiverseCore)) {
            return false;
        }
        this.plugin = (MultiverseCore) plugin;
        List<String> parsed = Arrays.asList(destination.split(":"));
        // Need at least: a:name
        if (!(parsed.size() == 2)) {
            return false;
        }
        // If it's not an Anchor type
        return parsed.get(0).equalsIgnoreCase("a");
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
        this.plugin = (MultiverseCore) plugin;
        List<String> parsed = Arrays.asList(destination.split(":"));
        // Need at least: e:world:x,y,z
        // OR e:world:x,y,z:pitch:yaw
        // so basically 3 or 5
        if (!(parsed.size() == 2)) {
            this.isValid = false;
            return;
        }
        this.name = parsed.get(1);
        this.location = this.plugin.getAnchorManager().getAnchorLocation(parsed.get(1));
        if (this.location == null) {
            this.isValid = false;
            return;
        }
        if (!parsed.get(0).equalsIgnoreCase(this.getIdentifier())) {
            this.isValid = false;
        }
        this.isValid = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "Anchor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Anchor: " + this.name;
    }

    @Override
    public String toString() {
        if (isValid) {
            return "a:" + this.name;
        }
        return "i:Invalid Destination";
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
        // This is an ANCHOR destination, don't safely teleport here.
        return false;
    }
}
