package org.mvplugins.multiverse.core.world.helper

import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.world.helpers.DimensionFinder.DimensionFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class DimensionFormatTest : TestWithMockBukkit() {

    @Test
    fun `Test invalid DimensionFormat`() {
        assertFailsWith(IllegalArgumentException::class) {
            DimensionFormat("%idk%_nether")
        }
    }

    @Test
    fun `Test DimensionFormat replace`() {
        val dimensionFormat = DimensionFormat("%overworld%_nether")
        assertEquals("cool_nether", dimensionFormat.replaceOverworld("cool"))
    }

    @Test
    fun `Test DimensionFormat get overworld`() {
        val dimensionFormat = DimensionFormat("%overworld%_n")
        assertEquals("cool", dimensionFormat.getOverworldFromName("cool_n").get())
    }

    @Test
    fun `Test DimensionFormat does not match`() {
        val dimensionFormat = DimensionFormat("%overworld%_nether")
        assertFalse(dimensionFormat.getOverworldFromName("cool_end").isDefined)
    }
}
