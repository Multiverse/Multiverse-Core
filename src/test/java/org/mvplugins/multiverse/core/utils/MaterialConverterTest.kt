package org.mvplugins.multiverse.core.utils

import org.bukkit.Material
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MaterialConverterTest {

    @Test
    fun `Convert dirt name to material`() {
        assertEquals(Material.DIRT, MaterialConverter.stringToMaterial("dirt"))
    }

    @Test
    fun `Convert Spruce Planks numerical id to material`() {
        assertEquals(Material.SPRUCE_PLANKS, MaterialConverter.stringToMaterial("5:1"))
    }

    @Test
    fun `Convert Oak Sapling item id to material`() {
        assertEquals(Material.OAK_SAPLING, MaterialConverter.stringToMaterial("minecraft:oak_sapling"))
    }

    @Test
    fun `Convert invalid string to material`() {
        assertNull(MaterialConverter.stringToMaterial("invalid"))
    }
}
