package org.mvplugins.multiverse.core.utils.position;

import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.exceptions.utils.position.PositionParseException;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.utils.REPatterns;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

/**
 * Represents a direction to face, defined by pitch and yaw.
 *
 * @since 5.3
 */
@ApiStatus.AvailableSince("5.3")
public class FaceDirection {

    /**
     * Creates a FaceDirection with absolute pitch and yaw.
     *
     * @param pitch The absolute pitch (vertical angle).
     * @param yaw   The absolute yaw (horizontal angle).
     * @return A new FaceDirection instance with the specified absolute pitch and yaw.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static FaceDirection ofAbsolute(double pitch, double yaw) {
        return new FaceDirection(
                PositionNumber.ofAbsolute(pitch),
                PositionNumber.ofAbsolute(yaw)
        );
    }

    /**
     * Gets the pitch and yaw from a Bukkit Location to create an FaceDirection with absolute values.
     *
     * @param location The Bukkit Location to convert.
     * @return A new FaceDirection instance representing the direction of the given location.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static FaceDirection ofLocation(org.bukkit.Location location) {
        return new FaceDirection(
                PositionNumber.ofAbsolute(location.getPitch()),
                PositionNumber.ofAbsolute(location.getYaw())
        );
    }

    /**
     * Parses a FaceDirection from a string representation.
     * The expected format is "&lt;pitch&gt;:&lt;yaw&gt;" for absolute pitch and yaw.
     * <br>
     * Relative pitch and yaw can be specified using the '~' prefix, e.g., "~:~5".
     *
     * @param directionStr The string representation of the direction.
     * @return A new FaceDirection instance parsed from the string.
     * @throws PositionParseException If the string format is invalid.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public static FaceDirection fromString(String directionStr) throws PositionParseException {
        String[] parts = REPatterns.COLON.split(directionStr, 2);
        if (parts.length != 2) {
            throw new PositionParseException(Message.of(MVCorei18n.EXCEPTION_POSITIONPARSE_INVALIDDIRECTION,
                    replace("{format}").with(directionStr)));
        }
        //TODO: Add support for compass directions (N, S, E, W, NE, NW, SE, SW) for yaw
        PositionNumber pitch = PositionNumber.fromString(parts[0]);
        PositionNumber yaw = PositionNumber.fromString(parts[1]);
        return new FaceDirection(pitch, yaw);
    }

    private final PositionNumber pitch;
    private final PositionNumber yaw;

    /**
     * Creates a new FaceDirection with the specified pitch and yaw.
     *
     * @param pitch The pitch (vertical angle).
     * @param yaw   The yaw (horizontal angle).
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public FaceDirection(PositionNumber pitch, PositionNumber yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    /**
     * Gets the pitch component of this FaceDirection.
     *
     * @return The pitch number representation.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public PositionNumber getPitch() {
        return pitch;
    }

    /**
     * Gets the yaw component of this FaceDirection.
     *
     * @return The yaw number representation.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public PositionNumber getYaw() {
        return yaw;
    }

    /**
     * Applies this FaceDirection to a given Bukkit Location, modifying its pitch and yaw.
     * <br>
     * Relative pitch and yaw will be offset based on the current values in the Location.
     *
     * @param base The Bukkit Location to modify.
     *
     * @since 5.3
     */
    @ApiStatus.AvailableSince("5.3")
    public void augmentBukkitLocation(Location base) {
        base.setPitch((float) pitch.getValue(base.getPitch()));
        base.setYaw((float) yaw.getValue(base.getYaw()));
    }

    @Override
    public String toString() {
        return pitch + ":" + yaw;
    }
}
