package org.mvplugins.multiverse.core.world

import com.onarandombox.MultiverseCore.worldnew.WorldManager
import com.onarandombox.MultiverseCore.worldnew.options.AddWorldOptions
import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.BeforeTest
import kotlin.test.Test

class WorldManagerTest : TestWithMockBukkit() {

    private lateinit var worldManager: WorldManager

    @BeforeTest
    fun setUp() {
        worldManager = multiverseCore.getService(WorldManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldManager is not available as a service") }
    }

    @Test
    fun `Add world`() {
        worldManager.addWorld(AddWorldOptions.worldName("world"))
        // TODO: When logic is implemented, check that the world is added
    }

    @Test
    fun `Delete world`() {
        worldManager.deleteWorld("world")
        // TODO: When logic is implemented, check that the world is removed
    }
}
