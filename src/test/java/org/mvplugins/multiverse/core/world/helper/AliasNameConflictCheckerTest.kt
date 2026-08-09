package org.mvplugins.multiverse.core.world.helper

import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.WorldManager
import org.mvplugins.multiverse.core.world.helpers.AliasNameConflictChecker
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AliasNameConflictCheckerTest : TestWithMockBukkit() {

    private lateinit var conflictChecker: AliasNameConflictChecker
    private lateinit var firstWorld: LoadedMultiverseWorld
    private lateinit var secondWorld: LoadedMultiverseWorld

    @BeforeTest
    fun setUp() {
        val worldManager = requireNotNull(serviceLocator.getActiveService(WorldManager::class.java))
        conflictChecker = requireNotNull(serviceLocator.getService(AliasNameConflictChecker::class.java))

        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName("FirstWorld")).isSuccess)
        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName("SecondWorld")).isSuccess)
        firstWorld = worldManager.getLoadedWorld("FirstWorld").get()
        secondWorld = worldManager.getLoadedWorld("SecondWorld").get()
    }

    @Test
    fun `Aliases differing only by case conflict`() {
        assertTrue(firstWorld.setAlias("SharedAlias").isSuccess)
        assertTrue(secondWorld.setAlias("sharedalias").isSuccess)

        val result = conflictChecker.checkDuplicateFor(secondWorld)

        assertEquals(listOf(firstWorld), result.duplicateAliases)
        assertTrue(result.duplicateWorldNames.isEmpty())
    }

    @Test
    fun `Alias and world name differing only by case conflict`() {
        assertTrue(firstWorld.setAlias("SECONDWORLD").isSuccess)

        val result = conflictChecker.checkDuplicateFor(firstWorld)

        assertTrue(result.duplicateAliases.isEmpty())
        assertEquals(listOf(secondWorld), result.duplicateWorldNames)
    }
}
