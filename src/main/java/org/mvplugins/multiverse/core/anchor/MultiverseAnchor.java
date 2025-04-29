package org.mvplugins.multiverse.core.anchor;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.world.location.UnloadedWorldLocation;

/**
 * Represents a single anchor location.
 */
public final class MultiverseAnchor {

    private final String name;
    private UnloadedWorldLocation location;

    MultiverseAnchor(String name, UnloadedWorldLocation location) {
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
     * Gets a copy of the anchor's location.
     *
     * @return The location.
     */
    public Location getLocation() {
        return location.toBukkitLocation();
    }

    void setLocation(Location location) {
        this.location = new UnloadedWorldLocation(location);
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
