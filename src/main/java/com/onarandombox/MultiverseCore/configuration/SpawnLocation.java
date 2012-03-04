package com.onarandombox.MultiverseCore.configuration;

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

/**
 * Just like a regular {@link Location}, however {@code world} is usually {@code null}
 * or just a weak reference and it implements {@link ConfigurationSerializable}.
 */
@SerializableAs("MVSpawnLocation")
public class SpawnLocation extends Location implements ConfigurationSerializable {
    private Reference<World> worldRef;

    public SpawnLocation(double x, double y, double z) {
        super(null, x, y, z);
    }

    public SpawnLocation(double x, double y, double z, float yaw, float pitch) {
        super(null, x, y, z, yaw, pitch);
    }

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
    public Chunk getChunk() {
        if ((this.worldRef != null) && (this.worldRef.get() != null))
            return this.worldRef.get().getChunkAt(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block getBlock() {
        if ((this.worldRef != null) && (this.worldRef.get() != null))
            return this.worldRef.get().getBlockAt(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<String, Object>(5); // SUPPRESS CHECKSTYLE: MagicNumberCheck
        serialized.put("x", this.getX());
        serialized.put("y", this.getY());
        serialized.put("z", this.getZ());
        serialized.put("pitch", this.getPitch());
        serialized.put("yaw", this.getYaw());
        return serialized;
    }

    /**
     * Let Bukkit be able to deserialize this.
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
}
