package org.mvplugins.multiverse.core.utils.position;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.exceptions.utils.position.PositionParseException;
import org.mvplugins.multiverse.core.utils.REPatterns;

/**
 * Represents a position for an entity in 3D space, including both coordinates and facing direction.
 *
 * @since 5.3
 */
@ApiStatus.AvailableSince("5.3")
public class EntityPosition {

    /**
     * Creates an EntityPosition with absolute coordinates and direction.
     *
     * @param x     The absolute X coordinate.
     * @param y     The absolute Y coordinate.
     * @param z     The absolute Z coordinate.
     * @param pitch The absolute pitch (vertical angle).
     * @param yaw   The absolute yaw (horizontal angle).
     * @return A new EntityPosition instance with the specified absolute coordinates and direction.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static EntityPosition ofAbsolute(double x, double y, double z, double pitch, double yaw) {
        return new EntityPosition(
                VectorPosition.ofAbsolute(x, y, z),
                FaceDirection.ofAbsolute(pitch, yaw)
        );
    }

    /**
     * Creates an EntityPosition from a Bukkit Location with absolute coordinates and direction.
     *
     * @param location The Bukkit Location to convert.
     * @return A new EntityPosition instance representing the given location.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static EntityPosition ofLocation(Location location) {
        return new EntityPosition(VectorPosition.ofLocation(location), FaceDirection.ofLocation(location));
    }

    /**
     * Parses an EntityPosition from a string representation.
     * The expected format is "&lt;x&gt;,&lt;y&gt;,&lt;z&gt;:&lt;pitch&gt;:&lt;yaw&gt;" for absolute coordinates and direction,
     * or "&lt;x&gt;,&lt;y&gt;,&lt;z&gt;" for absolute coordinates with default direction (0 pitch, 0 yaw).
     * <br>
     * Relative coordinates and direction can be specified using the '~' prefix, e.g., "~10,~,~-10:0:90".
     *
     * @param positionStr The string representation of the position.
     * @return A new EntityPosition instance parsed from the string.
     * @throws PositionParseException If the string format is invalid.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static EntityPosition fromString(String positionStr) throws PositionParseException {
        String[] parts = REPatterns.COLON.split(positionStr, 2);
        return parts.length == 2
                ? new EntityPosition(VectorPosition.fromString(parts[0]), FaceDirection.fromString(parts[1]))
                : new EntityPosition(VectorPosition.fromString(parts[0]), FaceDirection.ofAbsolute(0, 0));
    }

    private final VectorPosition vector;
    private final FaceDirection direction;

    /**
     * Creates a new EntityPosition with the specified vector and direction.
     *
     * @param vector    The vector position (coordinates).
     * @param direction The facing direction (pitch and yaw).
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public EntityPosition(VectorPosition vector, FaceDirection direction) {
        this.vector = vector;
        this.direction = direction;
    }

    /**
     * Gets the vector position (coordinates).
     *
     * @return The vector position.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public VectorPosition getVector() {
        return vector;
    }

    /**
     * Gets the facing direction (pitch and yaw).
     *
     * @return The facing direction.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public FaceDirection getDirection() {
        return direction;
    }

    /**
     * Augments a given Bukkit Location by applying the relative components of this EntityPosition.
     * This modifies the provided Location in place.
     *
     * @param base The base Bukkit Location to augment and offset for relative positioning as required.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public void augmentBukkitLocation(Location base) {
        vector.augmentBukkitLocation(base);
        direction.augmentBukkitLocation(base);
    }

    /**
     * Converts this EntityPosition to a new Bukkit Location based on a given base Location.
     * This does not modify the base Location, but returns a new Location instance.
     *
     * @param base The base Bukkit Location to use as a reference for relative positioning as required.
     * @return A new Bukkit Location representing this EntityPosition.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public Location toBukkitLocation(Location base) {
        return new Location(
                base.getWorld(),
                vector.getX().getValue(base.getX()),
                vector.getY().getValue(base.getY()),
                vector.getZ().getValue(base.getZ()),
                (float) direction.getYaw().getValue(base.getYaw()),
                (float) direction.getPitch().getValue(base.getPitch())
        );
    }

    @Override
    public String toString() {
        return vector + ":" + direction;
    }
}
