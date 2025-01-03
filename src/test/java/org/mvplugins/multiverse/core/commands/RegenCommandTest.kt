package org.mvplugins.multiverse.core.commands

import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.*

class RegenCommandTest : AbstractWorldCommandTest() {

    private lateinit var testWorld : LoadedMultiverseWorld

    @BeforeTest
    fun setUp() {
        testWorld = worldManager.createWorld(CreateWorldOptions.worldName("test")).get()
    }

    @Ignore //todo: Waiting for mockbukkit getGamerules bug to be fixed
    @Test
    fun `Regen world`() {
        testWorld.scale = 69.5
        testWorld.bukkitWorld.map {
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        }
        val previousSeed = testWorld.seed

        assertTrue(Bukkit.dispatchCommand(console, "mv regen test"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))
        val newWorld = worldManager.getLoadedWorld("test").orNull
        assertNotNull(newWorld)
        assertEquals(previousSeed, newWorld.seed)
        assertEquals(69.5, newWorld.scale)
        assertEquals(false, newWorld.bukkitWorld.map { it.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) }.orNull)
    }

    @Test
    fun `Regen world and reset world config`() {
        testWorld.scale = 69.5

        assertTrue(Bukkit.dispatchCommand(console, "mv regen test --reset-world-config"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))
        val newWorld = worldManager.getLoadedWorld("test").orNull
        assertNotNull(newWorld)
        assertEquals(1.0, newWorld.scale)
    }

    @Test
    fun `Regen world and reset gamerules`() {
        testWorld.bukkitWorld.map {
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        }

        assertTrue(Bukkit.dispatchCommand(console, "mv regen test --reset-gamerules"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))
        val newWorld = worldManager.getLoadedWorld("test").orNull
        assertNotNull(newWorld)
        assertEquals(true, newWorld.bukkitWorld.map { it.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) }.orNull)
    }
}
