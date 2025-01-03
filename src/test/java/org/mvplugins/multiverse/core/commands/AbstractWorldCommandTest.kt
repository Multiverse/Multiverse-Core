package org.mvplugins.multiverse.core.commands

import org.mvplugins.multiverse.core.world.WorldManager
import kotlin.test.BeforeTest

abstract class AbstractWorldCommandTest : AbstractCommandTest() {

    protected lateinit var worldManager: WorldManager

    @BeforeTest
    fun setUpWorldCommand() {
        worldManager = serviceLocator.getActiveService(WorldManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldManager is not available as a service") }
    }
}
