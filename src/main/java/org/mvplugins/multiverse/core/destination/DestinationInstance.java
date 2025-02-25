package org.mvplugins.multiverse.core.destination;

import io.vavr.control.Option;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Instance of a specific {@link Destination}.
 *
 * @param <I>   The type of the instance.
 * @param <T>   The type of the destination.
 */
public abstract class DestinationInstance<I extends  DestinationInstance<I, T>, T extends Destination<T, I>> {

    protected final T destination;

    protected DestinationInstance(@NotNull T destination) {
        this.destination = destination;
    }

    /**
     * Gets the destination that created this instance.
     *
     * @return The destination.
     */
    public @NotNull T getDestination() {
        return this.destination;
    }

    /**
     * Gets the {@link Destination#getIdentifier()} for this instance.
     *
     * @return The identifier.
     */
    public @NotNull String getIdentifier() {
        return this.destination.getIdentifier();
    }

    /**
     * Gets the exact location to teleport an entity to.
     *
     * @param teleportee The entity to teleport.
     * @return The location to teleport to.
     */
    public abstract @NotNull Option<Location> getLocation(@NotNull Entity teleportee);

    /**
     * Gets the velocity to apply to an entity after teleporting.
     *
     * @param teleportee The entity to teleport.
     * @return A vector representing the speed/direction the player should travel when arriving at the destination.
     */
    public abstract @NotNull Option<Vector> getVelocity(@NotNull Entity teleportee);

    /**
     * Should the Multiverse SafeTeleporter be used?
     *
     * <p>If not, MV will blindly take people to the location specified.</p>
     *
     * @return True if the SafeTeleporter will be used, false if not.
     */
    public abstract boolean checkTeleportSafety();

    /**
     * Gets the permission suffix to check for when teleporting to this destination.
     * This is used for finer per world/player permissions, such as "multiverse.teleport.self.worldname".
     *
     * <p>For example, if the destination is "w:world", the permission suffix is "world".</p>
     *
     * @return The permission suffix.
     */
    public abstract @NotNull Option<String> getFinerPermissionSuffix();

    /**
     * Serialises the destination instance to a savable string.
     *
     * <p>This is used when plugins save destinations to configuration,
     * and when the destination is displayed to the user.</p>
     *
     * @return The serialised destination instance.
     */
    @NotNull
    protected abstract String serialise();

    /**
     * String representation of the destination instance that can be deserialised back into the destination instance.
     *
     * @return The string representation of the destination instance.
     */
    @Override
    public String toString() {
        return this.destination.getIdentifier() + ":" + this.serialise();
    }
}
