package org.mvplugins.multiverse.core.utils.position;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.exceptions.utils.position.PositionParseException;
import org.mvplugins.multiverse.core.utils.REPatterns;

/**
 * Represents an x, y, z position in 3D space, with support for absolute and relative coordinates.
 *
 * @since 5.3
 */
@ApiStatus.AvailableSince("5.3")
public class VectorPosition {

    /**
     * Creates a VectorPosition with absolute coordinates.
     * @param x The absolute x coordinate.
     * @param y The absolute y coordinate.
     * @param z The absolute z coordinate.
     * @return A new VectorPosition instance with the specified absolute coordinates.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static VectorPosition ofAbsolute(double x, double y, double z) {
        return new VectorPosition(
                PositionNumber.ofAbsolute(x),
                PositionNumber.ofAbsolute(y),
                PositionNumber.ofAbsolute(z)
        );
    }

    /**
     * Creates a VectorPosition from a Bukkit Vector, using absolute coordinates.
     *
     * @param vector The Bukkit Vector to convert.
     * @return A new VectorPosition instance representing the given vector.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static VectorPosition ofVector(Vector vector) {
        return new VectorPosition(
                PositionNumber.ofAbsolute(vector.getX()),
                PositionNumber.ofAbsolute(vector.getY()),
                PositionNumber.ofAbsolute(vector.getZ())
        );
    }

    /**
     * Creates a VectorPosition from a Bukkit Location, using absolute coordinates.
     *
     * @param location The Bukkit Location to convert.
     * @return A new VectorPosition instance representing the given location.
     *
     * @since 5.3
     */
    public static VectorPosition ofLocation(Location location) {
        return new VectorPosition(
                PositionNumber.ofAbsolute(location.getX()),
                PositionNumber.ofAbsolute(location.getY()),
                PositionNumber.ofAbsolute(location.getZ())
        );
    }

    /**
     * Parses a VectorPosition from a string representation.
     * The expected format is "&lt;x&gt;,&lt;y&gt;,&lt;z&gt;" for absolute or relative coordinates.
     * <br>
     * Relative coordinates can be specified using the '~' prefix, e.g., "~10,~,~-10".
     *
     * @param coordStr The string representation of the coordinates.
     * @return A new VectorPosition instance parsed from the string.
     * @throws PositionParseException If the string format is invalid.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static VectorPosition fromString(String coordStr) throws PositionParseException {
        String[] parts = REPatterns.COMMA.split(coordStr);
        if (parts.length != 3) {
            throw new PositionParseException("Invalid coordinates format: " + coordStr + ". Expected format: <x>,<y>,<z>");
        }
        return new VectorPosition(
                PositionNumber.fromString(parts[0]),
                PositionNumber.fromString(parts[1]),
                PositionNumber.fromString(parts[2])
        );
    }

    private final PositionNumber x;
    private final PositionNumber y;
    private final PositionNumber z;

    /**
     * Creates a new VectorPosition with the specified PositionNumbers for x, y, and z.
     *
     * @param x The PositionNumber for the x coordinate.
     * @param y The PositionNumber for the y coordinate.
     * @param z The PositionNumber for the z coordinate.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public VectorPosition(PositionNumber x, PositionNumber y, PositionNumber z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the PositionNumber for the x coordinate.
     *
     * @return The PositionNumber representing the x coordinate.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public PositionNumber getX() {
        return x;
    }

    /**
     * Gets the PositionNumber for the y coordinate.
     *
     * @return The PositionNumber representing the y coordinate.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public PositionNumber getY() {
        return y;
    }

    /**
     * Gets the PositionNumber for the z coordinate.
     *
     * @return The PositionNumber representing the z coordinate.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public PositionNumber getZ() {
        return z;
    }

    /**
     * Augments the given Bukkit Vector in place by applying this VectorPosition's coordinates.
     * Relative coordinates will adjust the existing values, while absolute coordinates will set them directly.
     *
     * @param base The Bukkit Vector to augment.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public void augmentBukkitVector(Vector base) {
        base.setX(x.getValue(base.getX()));
        base.setY(y.getValue(base.getY()));
        base.setZ(z.getValue(base.getZ()));
    }

    /**
     * Augments the given Bukkit Location in place by applying this VectorPosition's coordinates.
     * Relative coordinates will adjust the existing values, while absolute coordinates will set them directly.
     *
     * @param location The Bukkit Location to augment.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public void augmentBukkitLocation(Location location) {
        location.setX(x.getValue(location.getX()));
        location.setY(y.getValue(location.getY()));
        location.setZ(z.getValue(location.getZ()));
    }

    /**
     * Converts this VectorPosition to a new Bukkit Vector based on a given base Vector.
     * This does not modify the base Vector, but returns a new Vector instance.
     *
     * @param base The base Bukkit Vector to use as a reference for relative positioning as required.
     * @return A new Bukkit Vector representing this VectorPosition.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public Vector toBukkitVector(Vector base) {
        return new Vector(
                x.getValue(base.getX()),
                y.getValue(base.getY()),
                z.getValue(base.getZ())
        );
    }

    /**
     * Converts this VectorPosition to a new Bukkit Location based on a given base Location.
     * This does not modify the base Location, but returns a new Location instance.
     *
     * @param base The base Bukkit Location to use as a reference for relative positioning as required.
     * @return A new Bukkit Location representing this VectorPosition.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public Location toBukkitLocation(Location base) {
        return new Location(
                base.getWorld(),
                x.getValue(base.getX()),
                y.getValue(base.getY()),
                z.getValue(base.getZ()),
                base.getYaw(),
                base.getPitch()
        );
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
