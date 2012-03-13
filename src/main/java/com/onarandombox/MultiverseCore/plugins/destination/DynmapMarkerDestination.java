package com.onarandombox.MultiverseCore.plugins.destination;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.plugins.DynmapConnector;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class DynmapMarkerDestination implements MVDestination{
    private MultiverseCore plugin;
    private Location location;
    private DynmapConnector dynmap;
    private boolean isValid;
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return "dm";
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
        if (!this.plugin.isDynmapLoaded()) {
            return false;
        }
        List<String> parsed = Arrays.asList(destination.split(":"));
        // Need at least: a:set:name
        if (!(parsed.size() == 3)) {
            return false;
        }
        // If it's not a Dynmap type
        return parsed.get(0).equalsIgnoreCase("dm");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getLocation(Entity entity) {
        return this.location;
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
    public void setDestination(JavaPlugin plugin, String destination) {
        List<String> parsed = Arrays.asList(destination.split(":"));
        if (!parsed.get(0).equalsIgnoreCase(this.getIdentifier())) {
            this.isValid = false;
            return;
        }
        if (!(plugin instanceof MultiverseCore)) {
            return;
        }
        this.plugin = (MultiverseCore) plugin;
        if (!this.plugin.isDynmapLoaded()) {
            return;
        }
        this.dynmap = this.plugin.getDynmap();

        // Need at least: e:world:x,y,z
        // OR e:world:x,y,z:pitch:yaw
        // so basically 3 or 5
        if (!(parsed.size() == 3)) {
            this.isValid = false;
            return;
        }
        this.dynmap.getLocation(parsed.get(1), parsed.get(2));
        if (this.location == null) {
            this.isValid = false;
            return;
        }

        this.isValid = true;

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
        return "Dynmap Marker";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return String.format("%s: %s", this.getType(), this.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequiredPermission() {
        return String.format("multiverse.access.", this.location.getWorld().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean useSafeTeleporter() {
        return false;
    }
}
