package org.mvplugins.multiverse.core.commands

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CreateCommandTest : AbstractCommandTest() {

    @Test
    fun `Create nether world with default options`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv create world1_nether nether"))
        val world = worldManager.getLoadedWorld("world1_nether")
        assertTrue(world.isDefined)
        assertEquals(World.Environment.NETHER, world.get().environment)
    }

    @Test
    fun `Create normal world with specific seed and flat world type without structures`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv create world1 normal --seed 1234 --world-type flat --no-structures"))
        val world = worldManager.getLoadedWorld("world1")
        assertTrue(world.isDefined)
        assertEquals(1234L, world.get().seed)
        assertEquals(WorldType.FLAT, world.get().worldType.get())
        assertFalse(world.get().canGenerateStructures().get())
    }

    //todo: Fix mockbukkit getBiomeProvider then added test on single biome world creation
}
