package org.mvplugins.multiverse.core.listeners

import org.bukkit.GameMode
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.teleportation.BlockSafety
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.WorldManager
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MVPlayerListenerTest : TestWithMockBukkit() {

    private lateinit var listener: MVPlayerListener
    private lateinit var worldManager: WorldManager
    private lateinit var blockSafety: BlockSafety

    @BeforeTest
    fun setUpRespawnTest() {
        listener = serviceLocator.getAllActiveServices(CoreListener::class.java)
            .filterIsInstance<MVPlayerListener>()
            .first()
        worldManager = requireNotNull(serviceLocator.getActiveService(WorldManager::class.java))
        blockSafety = requireNotNull(serviceLocator.getActiveService(BlockSafety::class.java))
    }

    @Test
    fun `Respawn radius of zero keeps the spawn location`() {
        val mvWorld = createWorld("respawn_radius_zero", 0)
        val spawnLocation = mvWorld.spawnLocation

        assertEquals(spawnLocation, listener.getSpawnLocationWithRespawnRadius(mvWorld, spawnLocation))
    }

    @Test
    fun `Respawn radius is not applied in adventure mode`() {
        val mvWorld = createWorld("respawn_radius_adventure", 5)
        assertTrue(mvWorld.setGameMode(GameMode.ADVENTURE).isSuccess)
        val spawnLocation = mvWorld.spawnLocation

        assertEquals(spawnLocation, listener.getSpawnLocationWithRespawnRadius(mvWorld, spawnLocation))
    }

    @Test
    fun `Respawn location is spread to safe locations within the radius`() {
        val radius = 5
        val mvWorld = createWorld("respawn_radius_five", radius)
        val spawnLocation = mvWorld.spawnLocation

        val results = (1..20).map { listener.getSpawnLocationWithRespawnRadius(mvWorld, spawnLocation) }

        results.forEach { result ->
            assertEquals(spawnLocation.world, assertNotNull(result.world))
            assertTrue(result.blockX - spawnLocation.blockX in -radius..radius)
            assertTrue(result.blockZ - spawnLocation.blockZ in -radius..radius)
            assertTrue(blockSafety.canSpawnAtLocationSafely(result))
        }
        assertTrue(results.any { it.blockX != spawnLocation.blockX || it.blockZ != spawnLocation.blockZ })
    }

    @Test
    fun `Respawn radius is limited by the world border`() {
        val mvWorld = createWorld("respawn_radius_border", 50)
        val spawnLocation = mvWorld.spawnLocation
        val worldBorder = spawnLocation.world.worldBorder
        worldBorder.center = spawnLocation
        worldBorder.size = 7.0

        repeat(20) {
            val result = listener.getSpawnLocationWithRespawnRadius(mvWorld, spawnLocation)
            assertTrue(worldBorder.isInside(result))
        }
    }

    @Suppress("DEPRECATION")
    private fun createWorld(name: String, respawnRadius: Int): LoadedMultiverseWorld {
        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName(name)).isSuccess)
        val world = requireNotNull(worldManager.getLoadedWorld(name).getOrNull())
        assertTrue(world.bukkitWorld.get().setGameRuleValue("respawn_radius", respawnRadius.toString()))
        return world
    }
}
