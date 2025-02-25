package org.mvplugins.multiverse.core.world.location;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

/**
 * Just like a regular {@link Location}, however {@code world} is usually {@code null}
 * or just a weak reference and it implements {@link ConfigurationSerializable}.
 */
@SerializableAs("MVSpawnLocation")
public class SpawnLocation extends Location implements ConfigurationSerializable {
    private Reference<World> worldRef;

    /**
     * Constructs a new Location with the given coordinates.
     *
     * @param x The x-coordinate of this new location
     * @param y The y-coordinate of this new location
     * @param z The z-coordinate of this new location
     */
    public SpawnLocation(double x, double y, double z) {
        super(null, x, y, z);
    }

    /**
     * Constructs a new Location with the given coordinates and direction.
     *
     * @param x The x-coordinate of this new location
     * @param y The y-coordinate of this new location
     * @param z The z-coordinate of this new location
     * @param yaw The absolute rotation on the x-plane, in degrees
     * @param pitch The absolute rotation on the y-plane, in degrees
     */
    public SpawnLocation(double x, double y, double z, float yaw, float pitch) {
        super(null, x, y, z, yaw, pitch);
    }

    /**
     * Constructs a new Location from an existing Location.
     *
     * @param loc   The location to clone.
     */
    public SpawnLocation(Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World getWorld() {
        return (this.worldRef != null) ? this.worldRef.get() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorld(World world) {
        this.worldRef = new WeakReference<World>(world);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Chunk getChunk() {
        World world = this.worldRef != null ? this.worldRef.get() : null;
        if (world != null) {
            return world.getChunkAt(this);
        }
        throw new IllegalStateException("World is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Block getBlock() {
        World world = this.worldRef != null ? this.worldRef.get() : null;
        if (world != null) {
            return world.getBlockAt(this);
        }
        throw new IllegalStateException("World is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        var map = new HashMap<String, Object>();
        map.put("x", getX());
        map.put("y", getY());
        map.put("z", getZ());
        map.put("pitch", getPitch());
        map.put("yaw", getYaw());
        return map;
    }

    /**
     * Let Bukkit be able to deserialize this.
     *
     * @param args The map.
     * @return The deserialized object.
     */
    public static SpawnLocation deserialize(Map<String, Object> args) {
        double x = ((Number) args.get("x")).doubleValue();
        double y = ((Number) args.get("y")).doubleValue();
        double z = ((Number) args.get("z")).doubleValue();
        float pitch = ((Number) args.get("pitch")).floatValue();
        float yaw = ((Number) args.get("yaw")).floatValue();
        return new SpawnLocation(x, y, z, yaw, pitch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Location{world=" + getWorld()
                + ",x=" + this.getX()
                + ",y=" + this.getY()
                + ",z=" + this.getZ()
                + ",pitch=" + this.getPitch()
                + ",yaw=" + this.getYaw()
                + '}';
    }
}
