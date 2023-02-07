package com.onarandombox.MultiverseCore.destination.core;

import java.util.Collection;
import java.util.Collections;

import co.aikar.commands.BukkitCommandIssuer;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.Teleporter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CannonDestination implements Destination<CannonDestinationInstance> {
    private final MultiverseCore plugin;

    public CannonDestination(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ca";
    }

    @Override
    public @Nullable CannonDestinationInstance getDestinationInstance(String destParams) {
        String[] params = destParams.split(":");
        if (params.length != 5) {
            return null;
        }

        String worldName = params[0];
        String coordinates = params[1];
        String pitch = params[2];
        String yaw = params[3];
        String speed = params[4];

        String[] coordinatesParams = coordinates.split(",");
        if (coordinatesParams.length != 3) {
            return null;
        }

        MultiverseWorld world = this.plugin.getMVWorldManager().getMVWorld(worldName);
        if (world == null) {
            return null;
        }

        Location location;
        double dSpeed;
        try {
            location = new Location(
                    world.getCBWorld(),
                    Double.parseDouble(coordinatesParams[0]),
                    Double.parseDouble(coordinatesParams[1]),
                    Double.parseDouble(coordinatesParams[2]),
                    Float.parseFloat(yaw),
                    Float.parseFloat(pitch)
            );
            dSpeed = Double.parseDouble(speed);
        } catch (NumberFormatException e) {
            return null;
        }

        return new CannonDestinationInstance(location, dSpeed);
    }

    @Override
    public @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destParams) {
        return Collections.singleton("");
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
