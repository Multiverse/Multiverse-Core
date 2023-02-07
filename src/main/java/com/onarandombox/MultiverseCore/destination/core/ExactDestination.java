package com.onarandombox.MultiverseCore.destination.core;

import java.util.Collection;
import java.util.Collections;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.Teleporter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExactDestination implements Destination<ExactDestinationInstance> {
    private final MultiverseCore plugin;

    public ExactDestination(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "e";
    }

    @Override
    public @Nullable ExactDestinationInstance getDestinationInstance(String destParams) {
        String[] items = destParams.split(":");
        if (items.length < 2) {
            return null;
        }

        String worldName = items[0];
        String coordinates = items[1];
        String[] coordinatesParams = coordinates.split(",");
        if (coordinatesParams.length != 3) {
            return null;
        }

        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(worldName);
        if (world == null) {
            return null;
        }

        Location location;
        try {
            location = new Location(
                    world.getCBWorld(),
                    Double.parseDouble(coordinatesParams[0]),
                    Double.parseDouble(coordinatesParams[1]),
                    Double.parseDouble(coordinatesParams[2])
            );
        } catch (NumberFormatException e) {
            return null;
        }

        if (items.length == 4) {
            String pitch = items[2];
            String yaw = items[3];
            try {
                location.setPitch(Float.parseFloat(pitch));
                location.setYaw(Float.parseFloat(yaw));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return new ExactDestinationInstance(location);
    }

    @Override
    public @NotNull Collection<String> suggestDestinations(@Nullable String destParams) {
        return Collections.emptyList();
    }

    @Override
    public boolean checkTeleportSafety() {
        return false;
    }

    @Override
    public @Nullable Teleporter getTeleporter() {
        return null;
    }
}
