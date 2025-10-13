package org.mvplugins.multiverse.core.destination.core;

import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.utils.position.EntityPosition;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

/**
 * Destination instance implementation for the {@link ExactDestination}.
 */
public final class ExactDestinationInstance extends DestinationInstance<ExactDestinationInstance, ExactDestination> {
    private final WorldManager worldManager;
    private final String worldName;
    private final EntityPosition position;

    /**
     * Constructor.
     *
     * @param destination The parent destination.
     * @param worldName   The name of the world.
     * @param position    The position in the world.
     */
    ExactDestinationInstance(@NotNull ExactDestination destination,
                             @NotNull WorldManager worldManager,
                             @NotNull String worldName,
                             @NotNull EntityPosition position) {
        super(destination);
        this.worldManager = worldManager;
        this.worldName = worldName;
        this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Location> getLocation(@NotNull Entity teleportee) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return Option.none();
        }
        Location destinationLocation = position.toBukkitLocation(teleportee.getLocation());
        destinationLocation.setWorld(world);
        return Option.of(destinationLocation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Vector> getVelocity(@NotNull Entity teleportee) {
        return Option.none();
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
    public @NotNull Option<String> getFinerPermissionSuffix() {
        return Option.of(worldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Message getDisplayMessage() {
        String displayWorldName = worldManager.getWorld(worldName)
                .map(MultiverseWorld::getAliasOrName)
                .getOrElse(worldName);
        return Message.of(displayWorldName + " at " + position.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        return worldName + ":" + position.toString();
    }
}
