package org.mvplugins.multiverse.core.commands

import org.bukkit.Bukkit
import org.mvplugins.multiverse.core.command.queue.ConfirmMode
import org.mvplugins.multiverse.core.config.CoreConfig
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModifyCommandTest : AbstractCommandTest() {

    private lateinit var testWorld : LoadedMultiverseWorld

    @BeforeTest
    fun setUp() {
        // Disable confirmation to make tests easier
        val config = serviceLocator.getActiveService(CoreConfig::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("CoreConfig is not available as a service") }
        config.confirmMode = ConfirmMode.DISABLE

        testWorld = worldManager.createWorld(CreateWorldOptions.worldName("test")).get()
    }

    @Test
    fun `Modify alias name with space`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv modify test set alias \"Test World\""))
        assertEquals("Test World", testWorld.alias)
    }
}
