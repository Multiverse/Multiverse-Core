package org.mvplugins.multiverse.core.utils.tick;

import io.vavr.control.Try;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * Represents a duration in Minecraft game ticks.
 * <p>
 * This class wraps a {@link Duration} and provides convenient conversion methods
 * between game ticks (50ms per tick), game days (24,000 ticks), and other time units.
 * Game time is mapped to real-world seconds via: realSeconds = (ticks * 3.6) + 6*3600 (mod 24h),
 * which means 00:00 maps to 06:00 in-game time.
 * </p>
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public final class TickDuration {

    /**
     * Parses a time string into a {@link TickDuration}.
     * <br />
     * Formats supported:
     * <ul>
     *   <li>{@code "123"} - raw tick count</li>
     *   <li>{@code "10s"} - seconds</li>
     *   <li>{@code "5d"} - game days</li>
     * </ul>
     *
     * @param timeString the time string to parse
     * @return a {@link Try} containing the parsed {@link TickDuration}, or a failure if parsing fails
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static Try<TickDuration> parseString(@NotNull String timeString) {
        return Try.of(() -> {
            if (timeString.endsWith("s")) {
                String subString = timeString.substring(0, timeString.length() - 1);
                return of(Long.parseLong(subString), TimeUnit.SECONDS);
            } else if (timeString.endsWith("d")) {
                String subString = timeString.substring(0, timeString.length() - 1);
                return ofGameDays(Long.parseLong(subString));
            }
            return ofTicks(Long.parseLong(timeString));
        });
    }

    /**
     * Creates a {@link TickDuration} from a {@link Duration}.
     *
     * @param duration the duration
     * @return a new {@link TickDuration}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TickDuration ofDuration(@NotNull Duration duration) {
        return new TickDuration(duration);
    }

    /**
     * Creates a {@link TickDuration} from seconds (as a {@link Duration}).
     *
     * @param duration the duration representing seconds
     * @return a new {@link TickDuration}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TickDuration ofSeconds(@NotNull Duration duration) {
        return new TickDuration(duration);
    }

    /**
     * Creates a {@link TickDuration} from a tick count.
     *
     * @param ticks the number of game ticks
     * @return a new {@link TickDuration}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TickDuration ofTicks(long ticks) {
        return of(ticks, TickUnit.TICK);
    }

    /**
     * Creates a {@link TickDuration} from a game day count.
     *
     * @param gameDays the number of game days (24,000 ticks each)
     * @return a new {@link TickDuration}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TickDuration ofGameDays(long gameDays) {
        return of(gameDays, TickUnit.GAME_DAY);
    }

    /**
     * Creates a {@link TickDuration} from a time value and {@link TimeUnit}.
     *
     * @param time the time value
     * @param unit the time unit
     * @return a new {@link TickDuration}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull TickDuration of(long time, @NotNull TimeUnit unit) {
        return of(time, unit.toChronoUnit());
    }

    /**
     * Creates a {@link TickDuration} from a time value and {@link TemporalUnit}.
     *
     * @param time the time value
     * @param unit the temporal unit
     * @return a new {@link TickDuration}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull TickDuration of(long time, @NotNull TemporalUnit unit) {
        return new TickDuration(Duration.of(time, unit));
    }

    private static final double TIME_MULTIPLIER = 3.6;
    private static final long DAY_SECONDS = 24 * 60 * 60;
    private static final long START_OFFSET = 6 * 60 * 60;

    private final Duration duration;

    /**
     * Creates a {@link TickDuration} from a {@link Duration}.
     *
     * @param duration the underlying duration
     */
    private TickDuration(@NotNull Duration duration) {
        this.duration = duration;
    }

    /**
     * Converts this duration to Minecraft local time.
     * <p>
     * Maps the duration to real-world seconds using the formula:
     * realSeconds = (ticks * 3.6) + 6*3600 (mod 24h)
     * </p>
     *
     * @return the {@link LocalTime} in Minecraft time (where 00:00 = 06:00 in-game)
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public @NotNull LocalTime toLocalTime() {
        // Minecraft maps ticks -> seconds by: realSeconds = (ticks * 3.6) + 6*3600 (mod 24h)
        long realSeconds = (long) ((toTicks() * TIME_MULTIPLIER) + START_OFFSET) % DAY_SECONDS;
        return LocalTime.ofSecondOfDay(realSeconds);
    }

    /**
     * Converts this duration to game ticks.
     *
     * @return the number of game ticks (1 tick = 50ms)
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public long toTicks() {
        return duration.toMillis() / TickUnit.TICK.inMillis();
    }

    /**
     * Converts this duration to game days.
     *
     * @return the number of game days (1 day = 24,000 ticks)
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public double toGameDays() {
        return (double) duration.toMillis() / (double) TickUnit.GAME_DAY.inMillis();
    }

    /**
     * Converts this duration to seconds.
     *
     * @return the duration in seconds
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public double toSeconds() {
        return to(TimeUnit.SECONDS);
    }

    /**
     * Converts this duration to a specified {@link TimeUnit}.
     *
     * @param unit the target time unit
     * @return the duration in the specified unit
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public double to(@NotNull TimeUnit unit) {
        return to(unit.toChronoUnit());
    }

    /**
     * Converts this duration to a specified {@link TemporalUnit}.
     *
     * @param unit the target temporal unit
     * @return the duration in the specified unit
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public double to(@NotNull TemporalUnit unit) {
        return (double) duration.toMillis() / (double) unit.getDuration().toMillis();
    }

    /**
     * Checks whether this duration is an exact multiple of the given {@link TimeUnit}.
     *
     * @param unit the time unit to test against
     * @return {@code true} if this duration divides evenly by the unit, otherwise {@code false}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public boolean isExactTo(@NotNull TimeUnit unit) {
        return isExactTo(unit.toChronoUnit());
    }

    /**
     * Checks whether this duration is an exact multiple of the given {@link TemporalUnit}.
     *
     * @param unit the temporal unit to test against
     * @return {@code true} if this duration divides evenly by the unit, otherwise {@code false}
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public boolean isExactTo(@NotNull TemporalUnit unit) {
        return duration.toMillis() % unit.getDuration().toMillis() == 0;
    }

    /**
     * Returns the underlying {@link Duration}.
     *
     * @return the duration
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public Duration getDuration() {
        return duration;
    }
}
