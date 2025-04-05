package org.mvplugins.multiverse.core.world.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Utility;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("UnloadedWorldLocation")
public final class UnloadedWorldLocation extends Location {

    public static UnloadedWorldLocation fromLocation(@NotNull Location location) {
        return new UnloadedWorldLocation(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    private String worldName;

    public UnloadedWorldLocation(@Nullable String worldName, double x, double y, double z) {
        super(null, x, y, z);
        setWorldName(worldName);
    }

    public UnloadedWorldLocation(@Nullable String worldName, double x, double y, double z, float yaw, float pitch) {
        super(null, x, y, z, yaw, pitch);
        setWorldName(worldName);
    }

    public UnloadedWorldLocation(@Nullable World world, double x, double y, double z) {
        super(null, x, y, z);
        setWorldName(world == null ? null : world.getName());
    }

    public UnloadedWorldLocation(@Nullable World world, double x, double y, double z, float yaw, float pitch) {
        super(null, x, y, z, yaw, pitch);
        setWorldName(world == null ? null : world.getName());
    }

    public void setWorldName(@Nullable String worldName) {
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }

    @Override
    public void setWorld(@Nullable World world) {
        this.worldName = (world == null) ? null : world.getName();
    }

    @Override
    public @Nullable World getWorld() {
        if (worldName == null) {
            return null;
        }
        return Bukkit.getWorld(worldName);
    }

    @Override
    @Utility
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        if (this.worldName != null) {
            data.put("world", this.worldName);
        }

        data.put("x", getX());
        data.put("y", getY());
        data.put("z", getZ());

        data.put("yaw", getYaw());
        data.put("pitch", getPitch());

        return data;
    }

    /**
     * Required method for deserialization
     *
     * @param args map to deserialize
     * @return deserialized location
     * @throws IllegalArgumentException if the world don't exists
     * @see ConfigurationSerializable
     */
    @NotNull
    public static Location deserialize(@NotNull Map<String, Object> args) {
        return new UnloadedWorldLocation(
                args.containsKey("world") ? args.get("world").toString() : null,
                NumberConversions.toDouble(args.get("x")),
                NumberConversions.toDouble(args.get("y")),
                NumberConversions.toDouble(args.get("z")),
                NumberConversions.toFloat(args.get("yaw")),
                NumberConversions.toFloat(args.get("pitch"))
        );
    }
}
