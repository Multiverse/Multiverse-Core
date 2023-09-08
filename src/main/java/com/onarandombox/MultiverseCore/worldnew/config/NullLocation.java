package com.onarandombox.MultiverseCore.worldnew.config;

import com.onarandombox.MultiverseCore.world.SimpleMVWorld;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Map;

/**
 * Null-location.
 */
@SerializableAs("MVNullLocation (It's a bug if you see this in your config file)")
public final class NullLocation extends SpawnLocation {
    public NullLocation() {
        super(0, -1, 0);
    }

    @Override
    public Location clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> serialize() {
        return Collections.emptyMap();
    }

    /**
     * Let Bukkit be able to deserialize this.
     * @param args The map.
     * @return The deserialized object.
     */
    public static SimpleMVWorld.NullLocation deserialize(Map<String, Object> args) {
        return new SimpleMVWorld.NullLocation();
    }

    @Override
    public Vector toVector() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return -1;
    }

    @Override
    public String toString() {
        return "Location{null}";
    }
}
