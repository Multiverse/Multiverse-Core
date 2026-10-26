package org.mvplugins.multiverse.core.listeners

import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.config.CoreConfig
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.WorldManager
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RespawnRadiusTest : TestWithMockBukkit() {

    private lateinit var config: CoreConfig
    private lateinit var mvWorld: LoadedMultiverseWorld

    @BeforeTest
    fun setUpRespawnRadiusTest() {
        config = requireNotNull(serviceLocator.getActiveService(CoreConfig::class.java))
        val worldManager = requireNotNull(serviceLocator.getActiveService(WorldManager::class.java))
        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName("respawn_world")).isSuccess)
        mvWorld = requireNotNull(worldManager.getLoadedWorld("respawn_world").getOrNull())
        @Suppress("DEPRECATION")
        assertTrue(mvWorld.bukkitWorld.get().setGameRuleValue("respawn_radius", "5"))
    }

    @Test
    fun `Player respawns exactly at world spawn when respawn radius is disabled`() {
        assertTrue(config.setUseRespawnRadius(false).isSuccess)

        repeat(10) {
            assertEquals(mvWorld.spawnLocation, respawnPlayer().respawnLocation)
        }
    }

    @Test
    fun `Player respawns around world spawn when respawn radius is enabled`() {
        assertTrue(config.setUseRespawnRadius(true).isSuccess)
        val spawnLocation = mvWorld.spawnLocation

        val results = (1..20).map { respawnPlayer().respawnLocation }

        results.forEach { result ->
            assertTrue(result.blockX - spawnLocation.blockX in -5..5)
            assertTrue(result.blockZ - spawnLocation.blockZ in -5..5)
        }
        assertTrue(results.any { it.blockX != spawnLocation.blockX || it.blockZ != spawnLocation.blockZ })
    }

    private fun respawnPlayer() = server.addPlayer().run {
        teleport(mvWorld.spawnLocation)
        respawn()
    }
}
