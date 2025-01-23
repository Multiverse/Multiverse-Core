package org.mvplugins.multiverse.core.commands

import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.mvplugins.multiverse.core.commandtools.ConfirmMode
import org.mvplugins.multiverse.core.config.MVCoreConfig
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.*

class RegenCommandTest : AbstractCommandTest() {

    private lateinit var testWorld : LoadedMultiverseWorld

    @BeforeTest
    fun setUp() {
        // Disable confirmation to make tests easier
        val config = serviceLocator.getActiveService(MVCoreConfig::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("MVCoreConfig is not available as a service") }
        assertTrue(config.setConfirmMode(ConfirmMode.DISABLE).isSuccess)

        testWorld = worldManager.createWorld(CreateWorldOptions.worldName("test")).get()
    }

    @Test
    fun `Regen world`() {
        val previousUID = testWorld.uid
        val previousSeed = testWorld.seed
        testWorld.scale = 69.5
        testWorld.bukkitWorld.map {
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        }
        testWorld.bukkitWorld.map {
            it.worldBorder.size = 123.4
        }
        assertTrue(Bukkit.dispatchCommand(console, "mv regen test"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))

        val newWorld = worldManager.getLoadedWorld("test").orNull

        assertNotNull(newWorld)
        assertNotEquals(previousUID, newWorld.uid)
        assertEquals(previousSeed, newWorld.seed)
        assertEquals(69.5, newWorld.scale)
        assertEquals(false, newWorld.bukkitWorld.map { it.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) }.orNull)
        assertEquals(123.4, newWorld.bukkitWorld.map { it.worldBorder.size }.orNull)
    }

    @Test
    fun `Regen world with specific seed`() {
        val previousUID = testWorld.uid

        assertTrue(Bukkit.dispatchCommand(console, "mv regen test --seed 456"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))

        val newWorld = worldManager.getLoadedWorld("test").orNull

        assertNotNull(newWorld)
        assertNotEquals(previousUID, newWorld.uid)
        assertEquals(456, newWorld.seed)
    }

    @Test
    fun `Regen world with random seed`() {
        val previousUID = testWorld.uid
        val previousSeed = testWorld.seed

        assertTrue(Bukkit.dispatchCommand(console, "mv regen test --seed"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))

        val newWorld = worldManager.getLoadedWorld("test").orNull

        assertNotNull(newWorld)
        assertNotEquals(previousUID, newWorld.uid)
        assertNotEquals(previousSeed, newWorld.seed)
    }

    @Test
    fun `Regen world and reset world config`() {
        val previousUID = testWorld.uid
        testWorld.scale = 69.5

        assertTrue(Bukkit.dispatchCommand(console, "mv regen test --reset-world-config"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))

        val newWorld = worldManager.getLoadedWorld("test").orNull

        assertNotNull(newWorld)
        assertNotEquals(previousUID, newWorld.uid)
        assertEquals(1.0, newWorld.scale)
    }

    @Test
    fun `Regen world and reset gamerules`() {
        val previousUID = testWorld.uid
        testWorld.bukkitWorld.map {
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        }

        assertTrue(Bukkit.dispatchCommand(console, "mv regen test --reset-gamerules"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))

        val newWorld = worldManager.getLoadedWorld("test").orNull

        assertNotNull(newWorld)
        assertNotEquals(previousUID, newWorld.uid)
        assertEquals(true, newWorld.bukkitWorld.map { it.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) }.orNull)
    }

    @Test
    fun `Regen world and reset world border`() {
        val previousUID = testWorld.uid
        testWorld.bukkitWorld.map {
            it.worldBorder.size = 123.4
        }

        assertTrue(Bukkit.dispatchCommand(console, "mv regen test --reset-world-border"))
        assertTrue(Bukkit.dispatchCommand(console, "mv confirm"))

        val newWorld = worldManager.getLoadedWorld("test").orNull

        assertNotNull(newWorld)
        assertNotEquals(previousUID, newWorld.uid)
        assertEquals(6.0E7, newWorld.bukkitWorld.map { it.worldBorder.size }.orNull)
    }
}
