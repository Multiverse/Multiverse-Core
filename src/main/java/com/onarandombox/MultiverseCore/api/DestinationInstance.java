package com.onarandombox.MultiverseCore.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DestinationInstance {
    @Nullable Location getLocation(@NotNull Entity teleportee);

    @Nullable Vector getVelocity(@NotNull Entity teleportee);

    @Nullable String getFinerPermissionSuffix();

    @NotNull String serialise();
}
