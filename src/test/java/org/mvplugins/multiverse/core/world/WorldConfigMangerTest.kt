package org.mvplugins.multiverse.core.world

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.World.Environment
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.economy.MVEconomist
import org.mvplugins.multiverse.core.world.location.SpawnLocation
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.test.*

class WorldConfigMangerTest : TestWithMockBukkit() {

    private lateinit var worldConfigManager : WorldsConfigManager

    @BeforeTest
    fun setUp() {
        val defaultWorldsData = getResourceAsText("/default_worlds.yml")
        assertNotNull(defaultWorldsData)
        val worldsFile = File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds.yml").absolutePathString())
        if (worldsFile.exists()) worldsFile.delete()
        worldsFile.writeText(defaultWorldsData)

        worldConfigManager = serviceLocator.getActiveService(WorldsConfigManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldsConfigManager is not available as a service") }
        assertTrue(worldConfigManager.load().isSuccess)
        assertTrue(worldConfigManager.isLoaded)
    }

    @Test
    fun `Old world config is migrated`() {
        val oldConfig = getResourceAsText("/old_worlds.yml")
        assertNotNull(oldConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds.yml").absolutePathString()).writeText(oldConfig)

        assertTrue(worldConfigManager.load().isSuccess)
        assertTrue(worldConfigManager.save().isSuccess)

        val endWorldConfig = worldConfigManager.getWorldConfig("world_the_end").orNull
        assertNotNull(endWorldConfig)

        assertEquals("&aworld the end", endWorldConfig.alias)
        assertEquals(Environment.THE_END, endWorldConfig.environment)
        assertFalse(endWorldConfig.isEntryFeeEnabled)
        assertEquals(MVEconomist.VAULT_ECONOMY_MATERIAL, endWorldConfig.entryFeeCurrency)
        assertEquals(0.0, endWorldConfig.entryFeeAmount)

        val worldConfig = worldConfigManager.getWorldConfig("world").orNull
        assertNotNull(worldConfig)

        assertEquals(-5176596003035866649, worldConfig.seed)
        assertEquals(listOf("test"), worldConfig.worldBlacklist)
        assertTrue(worldConfig.isEntryFeeEnabled)
        assertEquals(Material.DIRT, worldConfig.entryFeeCurrency)
        assertEquals(5.0, worldConfig.entryFeeAmount)

        assertConfigEquals("/migrated_worlds.yml", "worlds.yml")
    }

    @Test
    fun `Add a new world to config`() {
        val worldConfig = worldConfigManager.addWorldConfig("newworld")
        assertNotNull(worldConfig)
        assertEquals("newworld", worldConfig.worldName)
        assertTrue(worldConfigManager.save().isSuccess)
        assertConfigEquals("/newworld_worlds.yml", "worlds.yml")
    }

    @Test
    fun `Updating existing world properties`() {
        val worldConfig = worldConfigManager.getWorldConfig("world").orNull
        assertNotNull(worldConfig)

        assertTrue(worldConfig.stringPropertyHandle.setProperty("adjust-spawn", true).isSuccess)
        assertTrue(worldConfig.stringPropertyHandle.setProperty("alias", "newalias").isSuccess)
        assertTrue(worldConfig.setSpawnLocation(
            SpawnLocation(
                -50.0,
                50.0,
                50.0
            )
        ).isSuccess)
        assertTrue(worldConfigManager.save().isSuccess)
        assertConfigEquals("/updated_worlds.yml", "worlds.yml")
    }

    @Test
    fun `Delete world section from config`() {
        worldConfigManager.deleteWorldConfig("world")
        assertTrue(worldConfigManager.save().isSuccess)
        assertConfigEquals("/delete_worlds.yml", "worlds.yml")
    }

    @Test
    fun `Edge case parsing tests for world config`() {
        val edgecaseConfig = getResourceAsText("/edgecase_worlds.yml")
        assertNotNull(edgecaseConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds.yml").absolutePathString()).writeText(edgecaseConfig)

        assertTrue(worldConfigManager.load().isSuccess)
        assertTrue(worldConfigManager.save().isSuccess)

        val worldConfig = assertNotNull(worldConfigManager.getWorldConfig("world").orNull)

        assertEquals("1234", worldConfig.alias)
        assertTrue(worldConfig.bedRespawn)
        assertEquals(GameMode.SURVIVAL, worldConfig.gameMode)
        assertEquals(4.0, worldConfig.scale)
        assertEquals(listOf("a", "1", "2"), worldConfig.worldBlacklist)
    }
}
