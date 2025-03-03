package org.mvplugins.multiverse.core.destination.core;

import io.vavr.control.Option;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.destination.DestinationInstance;

/**
 * Destination instance implementation for the {@link AnchorDestination}.
 */
public class AnchorDestinationInstance extends DestinationInstance<AnchorDestinationInstance, AnchorDestination> {
    private final String anchorName;
    private final Location anchorLocation;

    /**
     * Constructor.
     *
     * @param anchorName        The name of the anchor.
     * @param anchorLocation    The location of the anchor.
     */
    AnchorDestinationInstance(
            @NotNull AnchorDestination destination,
            @NotNull String anchorName,
            @NotNull Location anchorLocation
    ) {
        super(destination);
        this.anchorName = anchorName;
        this.anchorLocation = anchorLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<Location> getLocation(@NotNull Entity teleportee) {
        return Option.of(anchorLocation);
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
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<String> getFinerPermissionSuffix() {
        return Option.of(anchorName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String serialise() {
        return anchorName;
    }
}
