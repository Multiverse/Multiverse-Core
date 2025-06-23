package org.mvplugins.multiverse.core.utils;

import io.vavr.control.Try;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting Minecraft time to real-world time format.
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public final class MinecraftTimeFormatter {

    private static final double TIME_MULTIPLIER = 3.6;
    private static final long DAY_SECONDS = 24 * 60 * 60;
    private static final long START_OFFSET = 6 * 60 * 60;

    /**
     * Formats Minecraft time to 12-hour format.
     *
     * @param time The Minecraft time to format.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static String format12h(long time) {
        return formatTime(time, "hh:mm a");
    }

    /**
     * Formats Minecraft time to 24-hour format.
     *
     * @param time The Minecraft time to format.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static String format24h(long time) {
        return formatTime(time, "HH:mm");
    }

    /**
     * Formats Minecraft time to the specified format.
     * See <a href="https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html">DateTimeFormatter documentation</a>
     * for available patterns.
     *
     * @param time   The Minecraft time to format.
     * @param format The format string for the time.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static String formatTime(long time, String format) {
        // Convert Minecraft time to real-world time
        long realTime = (long) ((time * TIME_MULTIPLIER) + START_OFFSET) % DAY_SECONDS;  // Minecraft ticks to seconds

        // Convert seconds to LocalTime
        LocalTime localTime = LocalTime.ofSecondOfDay(realTime);

        return Try.of(() -> DateTimeFormatter.ofPattern(format))
                .map(localTime::format)
                .getOrElse("invalid time format: " + format);
    }

    private MinecraftTimeFormatter() {
        // No instantiation
        throw new UnsupportedOperationException();
    }
}
