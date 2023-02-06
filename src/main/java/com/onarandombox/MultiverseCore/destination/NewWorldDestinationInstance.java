package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.api.DestinationInstance;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NewWorldDestinationInstance implements DestinationInstance {
    private final MultiverseWorld world;
    private final String direction;
    private final float yaw;

    public NewWorldDestinationInstance(@NotNull MultiverseWorld world, @Nullable String direction, float yaw) {
        this.world = world;
        this.direction = direction;
        this.yaw = yaw;
    }

    @Override
    public Location getLocation(Entity teleportee) {
        Location worldLoc = world.getSpawnLocation();
        if (this.yaw >= 0) {
            // Only modify the yaw if its set.
            worldLoc.setYaw(this.yaw);
        }
        return worldLoc;
    }

    @Override
    public Vector getVelocity(Entity teleportee) {
        return null;
    }

    @Override
    public String getFinerPermissionSuffix() {
        return world.getName();
    }

    @Override
    public String serialise() {
        if (this.direction != null) {
            return this.world.getCBWorld().getName() + ":" + this.direction;
        }
        return this.world.getCBWorld().getName();
    }
}
