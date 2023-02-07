package com.onarandombox.MultiverseCore.destination.core;

import com.onarandombox.MultiverseCore.api.DestinationInstance;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDestinationInstance implements DestinationInstance {
    private final Player player;

    public PlayerDestinationInstance(Player player) {
        this.player = player;
    }

    @Override
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        return player.getLocation();
    }

    @Override
    public @Nullable Vector getVelocity(@NotNull Entity teleportee) {
        return null;
    }

    @Override
    public @Nullable String getFinerPermissionSuffix() {
        return player.getName();
    }

    @Override
    public @NotNull String serialise() {
        return player.getName();
    }
}
