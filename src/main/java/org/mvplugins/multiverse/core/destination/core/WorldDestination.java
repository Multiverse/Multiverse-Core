package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;

import co.aikar.commands.BukkitCommandIssuer;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;

/**
 * {@link Destination} implementation for exact locations.
 */
@Service
public class WorldDestination implements Destination<WorldDestination, WorldDestinationInstance> {

    private final MVCoreConfig config;
    private final WorldManager worldManager;
    private final LocationManipulation locationManipulation;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;

    @Inject
    WorldDestination(
            @NotNull MVCoreConfig config,
            @NotNull WorldManager worldManager,
            @NotNull LocationManipulation locationManipulation,
            @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider) {
        this.config = config;
        this.worldManager = worldManager;
        this.locationManipulation = locationManipulation;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
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
        MultiverseWorld world = getLoadedMultiverseWorld(worldName);
        if (world == null) {
            return null;
        }

        String direction = (items.length == 2) ? items[1] : null;
        float yaw = direction != null ? this.locationManipulation.getYaw(direction) : -1;

        return new WorldDestinationInstance(this, world, direction, yaw);
    }

    @Nullable
    private MultiverseWorld getLoadedMultiverseWorld(String worldName) {
        return config.getResolveAliasName()
                ? worldManager.getLoadedWorldByNameOrAlias(worldName).getOrNull()
                : worldManager.getLoadedWorld(worldName).getOrNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        return worldManager.getLoadedWorlds().stream()
                .filter(world -> worldEntryCheckerProvider.forSender(sender)
                        .canAccessWorld(world)
                        .isSuccess())
                .map(world -> new DestinationSuggestionPacket(world.getName(), world.getName()))
                .toList();
    }
}
