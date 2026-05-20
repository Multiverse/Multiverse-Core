package org.mvplugins.multiverse.core.utils.tick

import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TickDurationTest {

    @Test
    fun `Parse raw tick, second, and game day strings`() {
        val ticks = TickDuration.parseString("123")
        val seconds = TickDuration.parseString("10s")
        val gameDays = TickDuration.parseString("5d")

        assertTrue(ticks.isSuccess)
        assertTrue(seconds.isSuccess)
        assertTrue(gameDays.isSuccess)

        assertEquals(123L, ticks.get().toTicks())
        assertEquals(200L, seconds.get().toTicks())
        assertEquals(120_000L, gameDays.get().toTicks())
    }

    @Test
    fun `Convert durations across units`() {
        val duration = TickDuration.ofDuration(Duration.ofMillis(75))
        val gameDays = TickDuration.ofGameDays(2)

        assertEquals(1L, duration.toTicks())
        assertEquals(0.075, duration.toSeconds(), absoluteTolerance = 1e-9)
        assertFalse(duration.isExactTo(TimeUnit.SECONDS))
        assertTrue(duration.isExactTo(TimeUnit.MILLISECONDS))

        assertEquals(48_000L, gameDays.toTicks())
        assertEquals(2.0, gameDays.toGameDays())
        assertEquals(2_400.0, gameDays.toSeconds())
    }

    @Test
    fun `Convert duration to Minecraft local time`() {
        assertEquals(LocalTime.of(6, 0), TickDuration.ofTicks(0).toLocalTime())
        assertEquals(LocalTime.of(7, 0), TickDuration.ofTicks(1_000).toLocalTime())
        assertEquals(LocalTime.of(6, 0), TickDuration.ofTicks(24_000).toLocalTime())
    }
}
