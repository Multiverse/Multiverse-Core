package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;

import co.aikar.commands.BukkitCommandIssuer;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.api.config.MVCoreConfig;
import org.mvplugins.multiverse.core.api.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.api.destination.Destination;
import org.mvplugins.multiverse.core.api.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.api.world.WorldManager;

/**
 * {@link Destination} implementation for exact locations.
 */
@Service
public class ExactDestination implements Destination<ExactDestination, ExactDestinationInstance> {

    private final MVCoreConfig config;
    private final WorldManager worldManager;

    @Inject
    public ExactDestination(MVCoreConfig config, WorldManager worldManager) {
        this.config = config;
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
     * Make a new {@link ExactDestinationInstance} from a {@link Location}.
     *
     * @param location  The target location
     * @return A new {@link ExactDestinationInstance}
     */
    public @NotNull ExactDestinationInstance fromLocation(@NotNull Location location) {
        return new ExactDestinationInstance(this, location);
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

        World world = getLoadedMultiverseWorld(worldName).flatMap(LoadedMultiverseWorld::getBukkitWorld).getOrNull();
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

        return new ExactDestinationInstance(this, location);
    }

    private Option<LoadedMultiverseWorld> getLoadedMultiverseWorld(String worldName) {
        return config.getResolveAliasName()
                ? worldManager.getLoadedWorldByNameOrAlias(worldName)
                : worldManager.getLoadedWorld(worldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(@NotNull BukkitCommandIssuer issuer, @Nullable String destinationParams) {
        return worldManager.getLoadedWorlds().stream()
                .map(world -> new DestinationSuggestionPacket(world.getName() + ":", world.getName()))
                .toList();
    }
}
