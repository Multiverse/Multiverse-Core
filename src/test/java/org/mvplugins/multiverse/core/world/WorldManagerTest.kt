package org.mvplugins.multiverse.core.world

import com.onarandombox.MultiverseCore.worldnew.MVWorld
import com.onarandombox.MultiverseCore.worldnew.WorldManager
import com.onarandombox.MultiverseCore.worldnew.options.CreateWorldOptions
import org.bukkit.World
import org.bukkit.WorldType
import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class WorldManagerTest : TestWithMockBukkit() {

    private lateinit var worldManager: WorldManager
    private lateinit var world: MVWorld

    @BeforeTest
    fun setUp() {
        worldManager = multiverseCore.getService(WorldManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldManager is not available as a service") }

        worldManager.createWorld(CreateWorldOptions.worldName("world"))
        world = worldManager.getMVWorld("world").get()
        assertNotNull(world)
    }

    @Test
    fun `Creates a new world`() {
        worldManager.createWorld(CreateWorldOptions.worldName("world_nether")
            .environment(World.Environment.NETHER)
            .generateStructures(false)
            .seed(1234L)
            .useSpawnAdjust(true)
            .worldType(WorldType.FLAT)
        )

        val world = worldManager.getMVWorld("world_nether").get()
        assertNotNull(world)
        assertEquals("world_nether", world.name)
        assertEquals(World.Environment.NETHER, world.getProperty("environment").get())
        assertEquals("", world.getProperty("generator").get())
        assertEquals(1234L, world.getProperty("seed").get())
    }

    @Test
    fun `Delete world`() {
        worldManager.deleteWorld(world)
        assertFalse(worldManager.getMVWorld("world").isDefined)
    }
}
