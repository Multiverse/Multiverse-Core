package org.mvplugins.multiverse.core.world

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World.Environment
import org.bukkit.entity.EntityType
import org.bukkit.entity.SpawnCategory
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.economy.MVEconomist
import org.mvplugins.multiverse.core.world.key.WorldKeyOrName
import org.mvplugins.multiverse.core.world.location.SpawnLocation
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.test.*

class WorldConfigMangerTest : TestWithMockBukkit() {

    private lateinit var worldConfigManager : WorldsConfigManager

    @BeforeTest
    fun setUp() {
        val defaultWorldsData = getResourceAsText("/worlds/default_worlds.yml")
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
        val oldConfig = getResourceAsText("/worlds/old_worlds.yml")
        assertNotNull(oldConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds.yml").absolutePathString()).writeText(oldConfig)

        assertTrue(worldConfigManager.load().isSuccess)
        assertTrue(worldConfigManager.save().isSuccess)

        val endWorldConfig = worldConfigManager.getWorldConfig(key("world_the_end")).orNull
        assertNotNull(endWorldConfig)

        assertEquals("&aworld the end", endWorldConfig.alias)
        assertEquals(Environment.THE_END, endWorldConfig.environment)
        assertFalse(endWorldConfig.isEntryFeeEnabled)
        assertEquals(MVEconomist.VAULT_ECONOMY_MATERIAL, endWorldConfig.entryFeeCurrency)
        assertEquals(0.0, endWorldConfig.entryFeeAmount)

        val worldConfig = worldConfigManager.getWorldConfig(key("world.a.b")).orNull
        assertNotNull(worldConfig)

        assertEquals(-5176596003035866649, worldConfig.seed)
        assertEquals(listOf("test"), worldConfig.worldBlacklist)
        assertTrue(worldConfig.isEntryFeeEnabled)
        assertEquals(Material.DIRT, worldConfig.entryFeeCurrency)
        assertEquals(5.0, worldConfig.entryFeeAmount)

        val world2Config = worldConfigManager.getWorldConfig(key("world.a.c")).orNull
        assertNotNull(world2Config)

        assertConfigEquals("/worlds/migrated_worlds.yml", "worlds.yml")
    }

    @Test
    fun `Add a new world to config`() {
        val newWorldKey = NamespacedKey.minecraft("new.world")
        val worldConfig = worldConfigManager.addWorldConfig(newWorldKey)
        assertNotNull(worldConfig)
        assertEquals(WorldKeyOrName.parseKey(newWorldKey), worldConfig.worldKeyOrName)
        assertTrue(worldConfigManager.save().isSuccess)
        assertConfigEquals("/worlds/newworld_worlds.yml", "worlds.yml")

        // Make sure the world still can be loaded after save
        assertTrue(worldConfigManager.load().isSuccess)
        assertEquals(WorldKeyOrName.parseKey(newWorldKey), worldConfigManager.getWorldConfig(key("minecraft:new.world")).orNull?.worldKeyOrName)
        assertConfigEquals("/worlds/newworld_worlds.yml", "worlds.yml")
    }

    @Test
    fun `Updating existing world properties`() {
        val worldConfig = worldConfigManager.getWorldConfig(key("world")).orNull
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
        worldConfig.entitySpawnConfig.getSpawnCategoryConfig(SpawnCategory.MISC).isSpawn = false
        worldConfig.entitySpawnConfig.getSpawnCategoryConfig(SpawnCategory.ANIMAL).tickRate = 111
        worldConfig.entitySpawnConfig.getSpawnCategoryConfig(SpawnCategory.ANIMAL).exceptions = listOf(EntityType.COW, EntityType.PIG)
        assertTrue(worldConfigManager.save().isSuccess)
        assertConfigEquals("/worlds/updated_worlds.yml", "worlds.yml")
    }

    @Test
    fun `Delete world section from config`() {
        worldConfigManager.deleteWorldConfig(key("world"))
        assertTrue(worldConfigManager.save().isSuccess)
        assertConfigEquals("/worlds/delete_worlds.yml", "worlds.yml")
    }

    @Test
    fun `Edge case parsing tests for world config`() {
        val edgecaseConfig = getResourceAsText("/worlds/edgecase_worlds.yml")
        assertNotNull(edgecaseConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds.yml").absolutePathString()).writeText(edgecaseConfig)

        assertTrue(worldConfigManager.load().isSuccess)
        assertTrue(worldConfigManager.save().isSuccess)

        val worldConfig = assertNotNull(worldConfigManager.getWorldConfig(key("world")).orNull)

        assertEquals("1234", worldConfig.alias)
        assertTrue(worldConfig.bedRespawn)
        assertEquals(GameMode.SURVIVAL, worldConfig.gameMode)
        assertEquals(4.0, worldConfig.scale)
        assertEquals(listOf("a", "1", "2"), worldConfig.worldBlacklist)
        assertEquals(listOf(EntityType.COW), worldConfig.entitySpawnConfig.getSpawnCategoryConfig(SpawnCategory.ANIMAL).exceptions)
    }

    private fun key(worldName: String): WorldKeyOrName {
        return WorldKeyOrName.parse(worldName).get()
    }
}
