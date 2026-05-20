package org.mvplugins.multiverse.core.world.helper

import org.bukkit.Bukkit
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.WorldManager
import org.mvplugins.multiverse.core.world.helpers.PotentialWorldFinder
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PotentialWorldFinderTest : TestWithMockBukkit() {

    private lateinit var potentialWorldFinder: PotentialWorldFinder
    private lateinit var worldManager: WorldManager
    private lateinit var world: LoadedMultiverseWorld

    @BeforeTest
    fun setUp() {
        potentialWorldFinder = serviceLocator.getService(PotentialWorldFinder::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("PotentialWorldFinder is not available as a service") }
        worldManager = serviceLocator.getActiveService(WorldManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldManager is not available as a service") }

        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName("world")).isSuccess)
        world = worldManager.getLoadedWorld("world").get()
        assertNotNull(world)
    }

    @Test
    fun `Get potential worlds`() {
        File(Bukkit.getWorldContainer(), "newworld1").mkdir()
        File(Bukkit.getWorldContainer(), "newworld1/level.dat").createNewFile()
        File(Bukkit.getWorldContainer(), "newworld1/data").mkdir()
        File(Bukkit.getWorldContainer(), "newworld2").mkdir()
        File(Bukkit.getWorldContainer(), "newworld2/level.dat").createNewFile()
        File(Bukkit.getWorldContainer(), "newworld2/data").mkdir()
        assertEquals(setOf("newworld1", "newworld2"), potentialWorldFinder.findPotentialWorlds().toSet())
    }
}
