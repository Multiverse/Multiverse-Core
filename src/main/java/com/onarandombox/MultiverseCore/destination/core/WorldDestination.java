package com.onarandombox.MultiverseCore.destination.core;

import java.util.Collection;
import java.util.Collections;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.Teleporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldDestination implements Destination<WorldDestinationInstance> {

    private final MultiverseCore plugin;

    public WorldDestination(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "w";
    }

    @Override
    public @Nullable WorldDestinationInstance getDestinationInstance(String destParams) {
        String[] items = destParams.split(":");
        if (items.length > 3) {
            return null;
        }

        String worldName = items[0];
        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(worldName);
        if (world == null) {
            return null;
        }

        String direction = (items.length == 2) ? items[1] : null;
        float yaw = direction != null ? this.plugin.getLocationManipulation().getYaw(direction) : -1;

        return new WorldDestinationInstance(world, direction, yaw);
    }

    @Override
    public @NotNull Collection<String> suggestDestinations(@Nullable String destParams) {
        return Collections.emptyList();
    }

    @Override
    public boolean checkTeleportSafety() {
        return true;
    }

    @Override
    public @Nullable Teleporter getTeleporter() {
        return null;
    }
}
