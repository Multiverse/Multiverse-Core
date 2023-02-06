package com.onarandombox.MultiverseCore.destination;

import java.util.Collection;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.Teleporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NewWorldDestination implements Destination<NewWorldDestinationInstance> {

    private final MultiverseCore plugin;

    public NewWorldDestination(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "w";
    }

    @Override
    public @Nullable NewWorldDestinationInstance getDestinationInstance(String destParams) {
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

        return new NewWorldDestinationInstance(world, direction, yaw);
    }

    @Override
    public @NotNull Collection<String> suggestDestinations(@Nullable String destParams) {
        return null;
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
