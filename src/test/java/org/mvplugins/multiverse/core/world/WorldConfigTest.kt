package org.mvplugins.multiverse.core.world

import org.bukkit.Material
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.world.key.WorldKeyOrName
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.test.*

class WorldConfigTest : TestWithMockBukkit() {

    private lateinit var worldConfigManager : WorldsConfigManager
    private lateinit var worldConfig : WorldConfig
    private lateinit var worldNetherConfig : WorldConfig

    @BeforeTest
    fun setUp() {
        val defaultConfig = getResourceAsText("/worlds/default_worlds.yml")
        assertNotNull(defaultConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds.yml").absolutePathString()).writeText(defaultConfig)

        worldConfigManager = serviceLocator.getActiveService(WorldsConfigManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldsConfigManager is not available as a service") }

        assertTrue(worldConfigManager.load().isSuccess)
        worldConfig = worldConfigManager.getWorldConfig(key("world")).orNull.takeIf { it != null } ?: run {
            throw IllegalStateException("WorldConfig for world is not available") }
        assertNotNull(worldConfig)
        worldNetherConfig = worldConfigManager.getWorldConfig(key("world_nether")).orNull.takeIf { it != null } ?: run {
            throw IllegalStateException("WorldConfig for world is not available") }
        assertNotNull(worldNetherConfig)
    }

    @Test
    fun `Getting existing world property with getProperty returns expected value`() {
        assertEquals("my world", worldConfig.stringPropertyHandle.getProperty("alias").get())
        assertEquals(false, worldConfig.stringPropertyHandle.getProperty("hidden").get())
        assertEquals(1.0, worldConfig.stringPropertyHandle.getProperty("scale").get())
        assertEquals(8.0, worldNetherConfig.stringPropertyHandle.getProperty("scale").get())
    }

    @Test
    fun `Getting non-existing world property with getProperty returns null`() {
        assertTrue(worldConfig.stringPropertyHandle.getProperty("invalid-property").isFailure)
        assertTrue(worldConfig.stringPropertyHandle.getProperty("version").isFailure)
    }

    @Test
    fun `Getting existing world property by getter returns expected value`() {
        assertEquals("my world", worldConfig.alias)
        assertEquals(false, worldConfig.isHidden)
        assertEquals(emptyList(), worldConfig.worldBlacklist)
    }

    @Test
    fun `Updating an existing world property with setProperty reflects the changes in getProperty`() {
        assertTrue(worldConfig.stringPropertyHandle.setProperty("adjust-spawn", true).isSuccess)
        assertEquals(true, worldConfig.stringPropertyHandle.getProperty("adjust-spawn").get())

        assertTrue(worldConfig.stringPropertyHandle.setProperty("alias", "abc").isSuccess)
        assertEquals("abc", worldConfig.stringPropertyHandle.getProperty("alias").get())

        assertTrue(worldConfig.stringPropertyHandle.setProperty("scale", 2.0).isSuccess)
        assertEquals(2.0, worldConfig.stringPropertyHandle.getProperty("scale").get())

        val blacklists = listOf("a", "b", "c")
        assertTrue(worldConfig.stringPropertyHandle.setProperty("world-blacklist", blacklists).isSuccess)
        assertEquals(blacklists, worldConfig.stringPropertyHandle.getProperty("world-blacklist").get())

        assertTrue(worldConfig.stringPropertyHandle.setProperty("entryfee-currency", Material.JUNGLE_WOOD).isSuccess)
        assertEquals(Material.JUNGLE_WOOD, worldConfig.stringPropertyHandle.getProperty("entryfee-currency").get())
    }

    @Test
    fun `Updating an existing world property with setPropertyString reflects the changes in getProperty`() {
        assertTrue(worldConfig.stringPropertyHandle.setPropertyString("adjust-spawn", "true").isSuccess)
        assertEquals(true, worldConfig.getStringPropertyHandle().getProperty("adjust-spawn").get())

        assertTrue(worldConfig.stringPropertyHandle.setPropertyString("alias", "abc").isSuccess)
        assertEquals("abc", worldConfig.stringPropertyHandle.getProperty("alias").get())

        assertTrue(worldConfig.stringPropertyHandle.setPropertyString("scale", "2.0").isSuccess)
        assertEquals(2.0, worldConfig.stringPropertyHandle.getProperty("scale").get())

        assertTrue(worldConfig.stringPropertyHandle.setPropertyString("world-blacklist", "a,b,c").isSuccess)
        assertEquals(listOf("a", "b", "c"), worldConfig.stringPropertyHandle.getProperty("world-blacklist").get())

        assertTrue(worldConfig.stringPropertyHandle.setPropertyString("entryfee-currency", "jungle_wood").isSuccess)
        assertEquals(Material.JUNGLE_WOOD, worldConfig.stringPropertyHandle.getProperty("entryfee-currency").get())
    }

    @Test
    fun `Getting meta returns empty map initially`() {
        assertEquals(emptyMap<String, String>(), worldConfig.meta)
    }

    @Test
    fun `Setting meta with setMeta adds individual key-value pairs`() {
        assertTrue(worldConfig.setMeta("custom-key", "custom-value").isSuccess)
        val meta = worldConfig.meta
        assertEquals("custom-value", meta["custom-key"])
    }

    @Test
    fun `Setting multiple meta values accumulates in the map`() {
        assertTrue(worldConfig.setMeta("key1", "value1").isSuccess)
        assertTrue(worldConfig.setMeta("key2", "value2").isSuccess)
        assertTrue(worldConfig.setMeta("key3", "value3").isSuccess)

        val meta = worldConfig.meta
        assertEquals("value1", meta["key1"])
        assertEquals("value2", meta["key2"])
        assertEquals("value3", meta["key3"])
        assertEquals(3, meta.size)
    }

    @Test
    fun `Updating existing meta key overwrites the value`() {
        assertTrue(worldConfig.setMeta("key1", "original-value").isSuccess)
        assertTrue(worldConfig.setMeta("key1", "updated-value").isSuccess)

        val meta = worldConfig.meta
        assertEquals("updated-value", meta["key1"])
        assertEquals(1, meta.size)
    }

    @Test
    fun `Removing meta key with removeMeta deletes the key from map`() {
        assertTrue(worldConfig.setMeta("key1", "value1").isSuccess)
        assertTrue(worldConfig.setMeta("key2", "value2").isSuccess)

        assertTrue(worldConfig.removeMeta("key1").isSuccess)

        val meta = worldConfig.meta
        assertNull(meta["key1"])
        assertEquals("value2", meta["key2"])
        assertEquals(1, meta.size)
    }

    @Test
    fun `Removing non-existing meta key returns failure`() {
        assertTrue(worldConfig.removeMeta("non-existent-key").isFailure)
        assertEquals(emptyMap<String, String>(), worldConfig.meta)
    }

    @Test
    fun `Meta is independent across different world configs`() {
        assertTrue(worldConfig.setMeta("world-key", "world-value").isSuccess)
        assertTrue(worldNetherConfig.setMeta("nether-key", "nether-value").isSuccess)

        val worldMeta = worldConfig.meta
        val netherMeta = worldNetherConfig.meta

        assertEquals("world-value", worldMeta["world-key"])
        assertNull(worldMeta["nether-key"])
        assertEquals("nether-value", netherMeta["nether-key"])
        assertNull(netherMeta["world-key"])
    }

    @Test
    fun `Updating a non-existing property with setProperty returns false`() {
        assertTrue(worldConfig.stringPropertyHandle.setProperty("invalid-property", false).isFailure)
        assertTrue(worldConfig.stringPropertyHandle.setProperty("version", 1.1).isFailure)
    }

    private fun key(worldName: String): WorldKeyOrName {
        return WorldKeyOrName.parse(worldName).get()
    }
}
