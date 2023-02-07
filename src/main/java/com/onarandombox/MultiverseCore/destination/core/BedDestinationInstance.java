package com.onarandombox.MultiverseCore.destination.core;

import com.onarandombox.MultiverseCore.api.DestinationInstance;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BedDestinationInstance implements DestinationInstance {
    private final Player player;

    public BedDestinationInstance(Player player) {
        this.player = player;
    }

    @Override
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        if (player != null) {
            return player.getBedSpawnLocation();
        }
        if (teleportee instanceof Player) {
            return ((Player) teleportee).getBedSpawnLocation();
        }
        return null;
    }

    @Override
    public @Nullable Vector getVelocity(@NotNull Entity teleportee) {
        return null;
    }

    @Override
    public @Nullable String getFinerPermissionSuffix() {
        return null;
    }

    @Override
    public @NotNull String serialise() {
        return player != null ? player.getName() : BedDestination.OWN_BED_STRING;
    }
}
