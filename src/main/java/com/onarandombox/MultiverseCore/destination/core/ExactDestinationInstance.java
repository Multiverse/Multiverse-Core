package com.onarandombox.MultiverseCore.destination.core;

import com.onarandombox.MultiverseCore.api.DestinationInstance;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExactDestinationInstance implements DestinationInstance {
    private final Location location;

    public ExactDestinationInstance(Location location) {
        this.location = location;
    }

    @Override
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        return location;
    }

    @Override
    public @Nullable Vector getVelocity(@NotNull Entity teleportee) {
        return null;
    }

    @Override
    public @Nullable String getFinerPermissionSuffix() {
        return location.getWorld().getName();
    }

    @Override
    public @NotNull String serialise() {
        return location.getWorld().getName() + ":" + location.getX() + "," + location.getY()
                + "," + location.getZ() + ":" + location.getPitch() + ":" + location.getYaw();
    }
}
