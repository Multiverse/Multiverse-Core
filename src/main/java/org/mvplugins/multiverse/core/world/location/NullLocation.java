package org.mvplugins.multiverse.core.world.location;

import java.util.Collections;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Null-location.
 */
@SerializableAs("MVNullLocation (It's a bug if you see this in your config file)")
public final class NullLocation extends SpawnLocation {
    private static final NullLocation INSTANCE = new NullLocation();

    /**
     * Get the default null location instance.
     *
     * @return The instance.
     */
    public static NullLocation get() {
        return INSTANCE;
    }

    private NullLocation() {
        super(0, -1, 0);
    }

    @Override
    public @NotNull Location clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Collections.emptyMap();
    }

    /**
     * Let Bukkit be able to deserialize this.
     *
     * @param args The map.
     * @return The deserialized object.
     */
    public static NullLocation deserialize(Map<String, Object> args) {
        return new NullLocation();
    }

    @Override
    public @NotNull Vector toVector() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullLocation;
    }

    @Override
    public String toString() {
        return "Location{null}";
    }
}
