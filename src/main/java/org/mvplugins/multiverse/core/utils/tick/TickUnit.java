package org.mvplugins.multiverse.core.utils.tick;

import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

/**
 * A {@link TemporalUnit} implementation for Minecraft game ticks.
 * <br />
 * Provides two standard units:
 * <ul>
 *   <li>{@link #TICK} - A single game tick (50 milliseconds)</li>
 *   <li>{@link #GAME_DAY} - A full game day (24,000 ticks)</li>
 * </ul>
 * This unit is not date-based and is precisely defined without estimation.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public final class TickUnit implements TemporalUnit {

    private static final long SINGLE_TICK_DURATION_MS = 50L;
    private static final long TICKS_PER_GAME_DAY = 24_000L;

    /**
     * A single game tick (50 milliseconds).
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static final TickUnit TICK = new TickUnit(SINGLE_TICK_DURATION_MS);

    /**
     * A full game day (24,000 ticks = 20 minutes in real time).
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static final TickUnit GAME_DAY = new TickUnit(SINGLE_TICK_DURATION_MS * TICKS_PER_GAME_DAY);

    private final long milliseconds;
    private final Duration duration;

    private TickUnit(long milliseconds) {
        this.milliseconds = milliseconds;
        this.duration = Duration.ofMillis(milliseconds);
    }

    /**
     * Gets the duration of this unit in milliseconds.
     *
     * @return the duration in milliseconds
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public long inMillis() {
        return milliseconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getDuration() {
        return duration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDurationEstimated() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDateBased() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTimeBased() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked") // following ChronoUnit#addTo
    @Override
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return (R) temporal.plus(duration.multipliedBy(amount));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long between(final Temporal start, final Temporal end) {
        return start.until(end, ChronoUnit.MILLIS) / milliseconds;
    }
}
