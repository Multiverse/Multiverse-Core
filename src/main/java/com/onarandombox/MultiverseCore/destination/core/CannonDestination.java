package com.onarandombox.MultiverseCore.destination.core;

import java.util.Collection;
import java.util.Collections;

import co.aikar.commands.BukkitCommandIssuer;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.Teleporter;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

@Service
public class CannonDestination implements Destination<CannonDestinationInstance> {

    private final MVWorldManager worldManager;

    /**
     * Constructor.
     *
     * @param plugin The MultiverseCore plugin.
     */
    @Inject
    public CannonDestination(MVWorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "ca";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable CannonDestinationInstance getDestinationInstance(@Nullable String destinationParams) {
        String[] params = destinationParams.split(":");
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

        MVWorld world = this.worldManager.getMVWorld(worldName);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams) {
        return Collections.singleton("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkTeleportSafety() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Teleporter getTeleporter() {
        return null;
    }
}
