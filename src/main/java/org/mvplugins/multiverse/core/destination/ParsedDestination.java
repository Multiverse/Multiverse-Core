package org.mvplugins.multiverse.core.destination;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.api.Destination;
import org.mvplugins.multiverse.core.api.DestinationInstance;

/**
 * A parsed destination.
 *
 * @param <S> The destination instance type.
 */
public class ParsedDestination<S extends DestinationInstance> {
    private final Destination<S> destination;
    private final DestinationInstance destinationInstance;

    /**
     * Creates a new parsed destination.
     *
     * @param destination         The destination.
     * @param destinationInstance The destination instance.
     */
    public ParsedDestination(Destination<S> destination, DestinationInstance destinationInstance) {
        this.destination = destination;
        this.destinationInstance = destinationInstance;
    }

    /**
     * Shortcut for {@link Destination#getIdentifier()}.
     *
     * @return The destination identifier.
     */
    public @NotNull String getIdentifier() {
        return destination.getIdentifier();
    }

    /**
     * Shortcut for {@link DestinationInstance#getLocation(Entity)}.
     *
     * @param teleportee    The entity to teleport.
     * @return The location to teleport to.
     */
    public @Nullable Location getLocation(@NotNull Entity teleportee) {
        return destinationInstance.getLocation(teleportee);
    }

    /**
     * Shortcut for {@link DestinationInstance#getFinerPermissionSuffix()}.
     *
     * @return The finer permission suffix.
     */
    public @Nullable String getFinerPermissionSuffix() {
        return destinationInstance.getFinerPermissionSuffix();
    }

    /**
     * Gets the destination.
     *
     * @return The destination.
     */
    public Destination<S> getDestination() {
        return destination;
    }

    /**
     * Gets the destination instance.
     *
     * @return The destination instance.
     */
    public DestinationInstance getDestinationInstance() {
        return destinationInstance;
    }

    /**
     * Converts to saveable string representation of this destination.
     *
     * @return Serialized string.
     */
    @Override
    public String toString() {
        return getIdentifier() + ":" + destinationInstance.serialise();
    }
}
