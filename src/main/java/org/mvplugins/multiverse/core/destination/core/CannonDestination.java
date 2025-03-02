package org.mvplugins.multiverse.core.destination.core;

import co.aikar.commands.BukkitCommandIssuer;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.Collection;
import java.util.List;

/**
 * {@link Destination} implementation for cannons.
 */
@Service
public class CannonDestination implements Destination<CannonDestination, CannonDestinationInstance> {

    private final WorldManager worldManager;

    @Inject
    CannonDestination(WorldManager worldManager) {
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

        World world = this.worldManager.getLoadedWorld(worldName).map(LoadedMultiverseWorld::getBukkitWorld).getOrNull().getOrNull();
        if (world == null) {
            return null;
        }

        Location location;
        double dSpeed;
        try {
            location = new Location(
                    world,
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

        return new CannonDestinationInstance(this, location, dSpeed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        return List.of();
    }
}
