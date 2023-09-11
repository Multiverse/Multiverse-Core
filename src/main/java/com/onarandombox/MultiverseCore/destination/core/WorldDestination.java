package com.onarandombox.MultiverseCore.destination.core;

import java.util.Collection;
import java.util.Collections;

import co.aikar.commands.BukkitCommandIssuer;
import com.onarandombox.MultiverseCore.api.Destination;
import com.onarandombox.MultiverseCore.api.LocationManipulation;
import com.onarandombox.MultiverseCore.api.Teleporter;
import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

/**
 * {@link Destination} implementation for exact locations.
 */
@Service
public class WorldDestination implements Destination<WorldDestinationInstance> {

    private final WorldManager worldManager;
    private final LocationManipulation locationManipulation;

    @Inject
    WorldDestination(WorldManager worldManager, LocationManipulation locationManipulation) {
        this.worldManager = worldManager;
        this.locationManipulation = locationManipulation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "w";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable WorldDestinationInstance getDestinationInstance(@Nullable String destinationParams) {
        String[] items = destinationParams.split(":");
        if (items.length > 3) {
            return null;
        }

        String worldName = items[0];
        LoadedMultiverseWorld world = this.worldManager.getLoadedWorld(worldName).getOrNull();
        if (world == null) {
            return null;
        }

        String direction = (items.length == 2) ? items[1] : null;
        float yaw = direction != null ? this.locationManipulation.getYaw(direction) : -1;

        return new WorldDestinationInstance(world, direction, yaw);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<String> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams) {
        // Autocomplete of worlds is done by MVCommandCompletion without prefix
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkTeleportSafety() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Teleporter getTeleporter() {
        return null;
    }
}
