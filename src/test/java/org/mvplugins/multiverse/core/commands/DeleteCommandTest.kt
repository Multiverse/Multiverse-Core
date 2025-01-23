package org.mvplugins.multiverse.core.commands

import org.bukkit.Bukkit
import org.mvplugins.multiverse.core.commandtools.ConfirmMode
import org.mvplugins.multiverse.core.config.MVCoreConfig
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import org.mvplugins.multiverse.core.world.options.UnloadWorldOptions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeleteCommandTest : AbstractCommandTest() {

    private lateinit var testWorld : LoadedMultiverseWorld

    @BeforeTest
    fun setUp() {
        // Disable confirmation to make tests easier
        val config = serviceLocator.getActiveService(MVCoreConfig::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("MVCoreConfig is not available as a service") }
        config.confirmMode = ConfirmMode.DISABLE

        testWorld = worldManager.createWorld(CreateWorldOptions.worldName("test")).get()
    }

    @Test
    fun `Delete loaded world`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv delete test"))
        assertFalse(worldManager.getWorld("test").isDefined)
    }

    @Test
    fun `Delete unloaded world`() {
        worldManager.unloadWorld(UnloadWorldOptions.world(testWorld))
        assertTrue(Bukkit.dispatchCommand(console, "mv delete test"))
        assertFalse(worldManager.getWorld("test").isDefined)
    }
}
