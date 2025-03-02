package org.mvplugins.multiverse.core.config

import org.bukkit.Location
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter
import org.mvplugins.multiverse.core.world.WorldManager
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import java.lang.AssertionError
import kotlin.test.*

class ConfigTeleportInterceptTest : TestWithMockBukkit() {

    private lateinit var config: MVCoreConfig
    private lateinit var safetyTeleporter: AsyncSafetyTeleporter
    private lateinit var player: PlayerMock
    private lateinit var location: Location

    @BeforeTest
    fun setUp() {
        config = serviceLocator.getActiveService(MVCoreConfig::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("MVCoreConfig is not available as a service") }

        safetyTeleporter = serviceLocator.getActiveService(AsyncSafetyTeleporter::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("AsyncSafetyTeleporter is not available as a service") }

        val worldManager = serviceLocator.getActiveService(WorldManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldManager is not available as a service") }

        player = server.addPlayer()
        worldManager.createWorld(CreateWorldOptions.worldName("world2")).get()
        location = Location(server.getWorld("world2"), 0.0, 5.0, 0.0)
        config.enforceAccess = true
    }

    @Test
    fun `Generic teleport with teleport intercept enabled`() {
        config.teleportIntercept = true
        assertFalse(player.teleport(location))
        assertEquals("world", player.world.name)
    }

    @Test
    fun `Generic teleport with teleport intercept disabled - no access checking`() {
        config.teleportIntercept = false
        assertTrue(player.teleport(location))
        assertEquals("world2", player.world.name)
    }

    @Test
    fun `Multiverse API teleport with teleport intercept disabled`() {
        config.teleportIntercept = false
        safetyTeleporter.to(location).teleport(player).toAttempt()
            .onSuccess (Runnable { throw AssertionError("Teleport should have failed") })
            .onFailure(Runnable { assertEquals("world", player.world.name) })
    }
}
