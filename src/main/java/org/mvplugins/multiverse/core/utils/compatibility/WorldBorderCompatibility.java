package org.mvplugins.multiverse.core.utils.compatibility;

import org.bukkit.WorldBorder;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.utils.ReflectHelper;
import org.mvplugins.multiverse.core.utils.tick.TickDuration;

import java.util.concurrent.TimeUnit;

/**
 * Compatibility class used to handle API changes in {@link WorldBorder} class.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
@SuppressWarnings("removal")
public final class WorldBorderCompatibility {

    private static final boolean HAS_GET_WARNING_TIME_TICKS_METHOD;
    private static final boolean HAS_SET_WARNING_TIME_TICKS_METHOD;
    private static final boolean HAS_SET_WARNING_TIME_METHOD;
    private static final boolean HAS_GET_WARNING_TIME_METHOD;

    private static final boolean HAS_CHANGE_SIZE_METHOD;
    private static final boolean HAS_SET_SIZE_METHOD;

    static {
        HAS_GET_WARNING_TIME_TICKS_METHOD = ReflectHelper.hasMethod(WorldBorder.class, "getWarningTimeTicks");
        HAS_SET_WARNING_TIME_TICKS_METHOD = ReflectHelper.hasMethod(WorldBorder.class, "setWarningTimeTicks", int.class);
        HAS_GET_WARNING_TIME_METHOD = ReflectHelper.hasMethod(WorldBorder.class, "getWarningTime");
        HAS_SET_WARNING_TIME_METHOD = ReflectHelper.hasMethod(WorldBorder.class, "setWarningTime", int.class);

        HAS_CHANGE_SIZE_METHOD = ReflectHelper.hasMethod(WorldBorder.class, "changeSize", double.class, long.class);
        HAS_SET_SIZE_METHOD = ReflectHelper.hasMethod(WorldBorder.class, "setSize", double.class, long.class);
    }

    /**
     * Gets the world border warning time in ticks, using the most precise API available.
     * <br />
     * If the server exposes {@code getWarningTimeTicks()}, that value is returned directly. Otherwise, the older
     * {@code getWarningTime()} API is used and converted from seconds to ticks.
     *
     * @param worldBorder The world border to query.
     * @return The warning time in ticks.
     *
     * @since 5.7
     * @throws IllegalStateException If neither warning-time getter is available.
     */
    @ApiStatus.AvailableSince("5.7")
    public static int getWarningTimeTicks(WorldBorder worldBorder) {
        if (HAS_GET_WARNING_TIME_TICKS_METHOD) {
            return worldBorder.getWarningTimeTicks();
        } else if (HAS_GET_WARNING_TIME_METHOD) {
            return worldBorder.getWarningTime() * 20;
        }
        throw new IllegalStateException("Neither getWarningTimeTicks nor getWarningTime method is available in WorldBorder class.");
    }

    /**
     * Gets the world border warning time in seconds, using the most appropriate API available.
     * <br />
     * If the server exposes {@code getWarningTimeTicks()}, that value is converted from ticks to seconds. Otherwise,
     * the older {@code getWarningTime()} API is used directly.
     *
     * @param worldBorder The world border to query.
     * @return The warning time in seconds.
     *
     * @since 5.7
     * @throws IllegalStateException If neither warning-time getter is available.
     */
    @ApiStatus.AvailableSince("5.7")
    public static double getWarningTime(WorldBorder worldBorder) {
        if (HAS_GET_WARNING_TIME_TICKS_METHOD) {
            return (double) worldBorder.getWarningTimeTicks() / 20.0;
        } else if (HAS_GET_WARNING_TIME_METHOD) {
            return worldBorder.getWarningTime();
        }
        throw new IllegalStateException("Neither getWarningTimeTicks nor getWarningTime method is available in WorldBorder class.");
    }

    /**
     * Checks whether the current server supports setting the world border warning time in ticks.
     *
     * @return True if {@code setWarningTimeTicks(int)} is available, else false.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static boolean supportsSetWarningTimeInTicks() {
        return HAS_SET_WARNING_TIME_TICKS_METHOD;
    }

    /**
     * Sets the world border warning time in ticks.
     * <br />
     * This is a convenience wrapper around {@link #setWarningTimeDuration(WorldBorder, TickDuration)} that converts
     * the given tick value into a {@link TickDuration} first.
     *
     * @param worldBorder   The world border to update.
     * @param ticks         The warning time in ticks.
     *
     * @since 5.7
     */
    public static void setWarningTimeTicks(WorldBorder worldBorder, int ticks) {
        setWarningTimeDuration(worldBorder, TickDuration.ofTicks(ticks));
    }

    /**
     * Sets the world border warning time, preferring tick-based APIs when available.
     * <br />
     * If the server supports {@code setWarningTimeTicks(int)}, the provided duration is applied in ticks. Otherwise,
     * the older {@code setWarningTime(int)} API is used and the duration is converted to seconds.
     *
     * @param worldBorder   The world border to update.
     * @param duration      The warning duration to apply.
     *
     * @since 5.7
     * @throws IllegalStateException If neither warning-time setter is available.
     */
    @ApiStatus.AvailableSince("5.7")
    public static void setWarningTimeDuration(WorldBorder worldBorder, TickDuration duration) {
        if (HAS_SET_WARNING_TIME_TICKS_METHOD) {
            worldBorder.setWarningTimeTicks((int) duration.toTicks());
        } else if (HAS_SET_WARNING_TIME_METHOD) {
            worldBorder.setWarningTime((int) Math.round(duration.to(TimeUnit.SECONDS)));
        } else {
            throw new IllegalStateException("Neither setWarningTimeTicks nor setWarningTime method is available in WorldBorder class.");
        }
    }

    /**
     * Checks whether the current server supports changing world border size over a duration in ticks.
     *
     * @return True if {@code changeSize(double, long)} is available, else false.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static boolean supportsChangeSizeInTicks() {
        return HAS_CHANGE_SIZE_METHOD;
    }

    /**
     * Changes the world border size, preferring the tick-based API when available.
     * <br />
     * If the server supports {@code changeSize(double, long)}, the size change is applied in ticks. Otherwise, the
     * older {@code setSize(double, long)} API is used and the duration is converted to seconds.
     *
     * @param worldBorder   The world border to update.
     * @param newSize       The new border size.
     * @param duration      The transition duration to apply.
     *
     * @since 5.7
     * @throws IllegalStateException If neither size-changing API is available.
     */
    @ApiStatus.AvailableSince("5.7")
    public static void changeSizeDuration(WorldBorder worldBorder, double newSize, TickDuration duration) {
        if (HAS_CHANGE_SIZE_METHOD) {
            worldBorder.changeSize(newSize, duration.toTicks());
        } else if (HAS_SET_SIZE_METHOD) {
            worldBorder.setSize(newSize, Math.round(duration.to(TimeUnit.SECONDS)));
        } else {
            throw new IllegalStateException("Neither changeSize nor setSize method is available in WorldBorder class.");
        }
    }

    private WorldBorderCompatibility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
