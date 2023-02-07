package com.onarandombox.MultiverseCore.destination.core;

import com.onarandombox.MultiverseCore.api.DestinationInstance;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnchorDestinationInstance implements DestinationInstance {
    private final String anchorName;
    private final Location anchorLocation;

    public AnchorDestinationInstance(String anchorName, Location anchorLocation) {
        this.anchorName = anchorName;
        this.anchorLocation = anchorLocation;
    }

    @Override
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        return anchorLocation;
    }

    @Override
    public @Nullable Vector getVelocity(@NotNull Entity teleportee) {
        return null;
    }

    @Override
    public @Nullable String getFinerPermissionSuffix() {
        return anchorName;
    }

    @Override
    public @NotNull String serialise() {
        return anchorName;
    }
}
