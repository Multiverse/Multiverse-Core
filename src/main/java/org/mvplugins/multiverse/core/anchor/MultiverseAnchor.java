package org.mvplugins.multiverse.core.anchor;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a single anchor location.
 */
public final class MultiverseAnchor {

    private final String name;
    private Location location;

    MultiverseAnchor(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Gets the name of the anchor.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the location of the anchor.
     *
     * @return The location.
     */
    public Location getLocation() {
        return location;
    }

    void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Gets the world of the anchor's location.
     *
     * @return The world.
     */
    @Nullable World getLocationWorld() {
        if (location == null) {
            return null;
        }
        return location.getWorld();
    }
}
