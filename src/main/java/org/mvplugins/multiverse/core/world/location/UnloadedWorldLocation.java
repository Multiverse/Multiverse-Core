package org.mvplugins.multiverse.core.world.location;

import io.vavr.control.Try;
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
import java.util.Objects;

/**
 * A location that store a world name instead of a world object.
 * It only gets the world from bukkit during the {@link #getWorld()} call.
 * <br />
 * This is useful to store location with world that may not be loaded yet or have been unloaded at some point.
 */
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

    public UnloadedWorldLocation(@NotNull Location location) {
        this(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Makes a bukkit {@link Location} copy from this SpawnLocation.
     *
     * @return The bukkit location
     */
    public Location toBukkitLocation() {
        return new Location(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Location other)) {
            return false;
        }
        String otherWorldName = Try.of(() -> other instanceof UnloadedWorldLocation unloadedWorldLocation
                        ? unloadedWorldLocation.worldName
                        : other.getWorld().getName())
                .getOrNull();
        if (!Objects.equals(this.worldName, otherWorldName)) {
            return false;
        }
        if (Double.doubleToLongBits(this.getX()) != Double.doubleToLongBits(other.getX())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getY()) != Double.doubleToLongBits(other.getY())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getZ()) != Double.doubleToLongBits(other.getZ())) {
            return false;
        }
        if (Float.floatToIntBits(this.getPitch()) != Float.floatToIntBits(other.getPitch())) {
            return false;
        }
        if (Float.floatToIntBits(this.getYaw()) != Float.floatToIntBits(other.getYaw())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + worldName.hashCode();
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.getX()) ^ (Double.doubleToLongBits(this.getX()) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.getY()) ^ (Double.doubleToLongBits(this.getY()) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.getZ()) ^ (Double.doubleToLongBits(this.getZ()) >>> 32));
        hash = 19 * hash + Float.floatToIntBits(this.getPitch());
        hash = 19 * hash + Float.floatToIntBits(this.getYaw());
        return hash;
    }

    @Override
    public String toString() {
        return "Location{" +
                "world=" + worldName +
                ",x=" + getX() +
                ",y=" + getY() +
                ",z=" + getZ() +
                ",pitch=" + getPitch() +
                ",yaw=" + getYaw() +
                '}';
    }
}
