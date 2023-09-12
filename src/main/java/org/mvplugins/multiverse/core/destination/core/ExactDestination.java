package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;
import java.util.Collections;

import co.aikar.commands.BukkitCommandIssuer;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.Destination;
import org.mvplugins.multiverse.core.api.Teleporter;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.WorldManager;

/**
 * {@link Destination} implementation for exact locations.
 */
@Service
public class ExactDestination implements Destination<ExactDestinationInstance> {

    private final WorldManager worldManager;

    @Inject
    public ExactDestination(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "e";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ExactDestinationInstance getDestinationInstance(@Nullable String destinationParams) {
        String[] items = destinationParams.split(":");
        if (items.length < 2) {
            return null;
        }

        String worldName = items[0];
        String coordinates = items[1];
        String[] coordinatesParams = coordinates.split(",");
        if (coordinatesParams.length != 3) {
            return null;
        }

        World world = this.worldManager.getLoadedWorld(worldName).map(LoadedMultiverseWorld::getBukkitWorld).getOrNull().getOrNull();
        if (world == null) {
            return null;
        }

        Location location;
        try {
            location = new Location(
                    world,
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
