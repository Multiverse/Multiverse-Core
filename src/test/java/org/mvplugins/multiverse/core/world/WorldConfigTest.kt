package org.mvplugins.multiverse.core.world

import org.bukkit.Material
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.world.config.WorldConfig
import org.mvplugins.multiverse.core.world.config.WorldsConfigManager
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.test.*

class WorldConfigTest : TestWithMockBukkit() {

    private lateinit var worldConfigManager : WorldsConfigManager
    private lateinit var worldConfig : WorldConfig

    @BeforeTest
    fun setUp() {
        val defaultConfig = getResourceAsText("/default_worlds.yml")
        assertNotNull(defaultConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds2.yml").absolutePathString()).writeText(defaultConfig)

        worldConfigManager = serviceLocator.getActiveService(WorldsConfigManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldsConfigManager is not available as a service") }

        assertTrue(worldConfigManager.load().isSuccess)
        worldConfig = worldConfigManager.getWorldConfig("world").orNull.takeIf { it != null } ?: run {
            throw IllegalStateException("WorldConfig for world is not available") }
        assertNotNull(worldConfig);
    }

    @Test
    fun `Getting existing world property with getProperty returns expected value`() {
        assertEquals("my world", worldConfig.stringPropertyHandle.getProperty("alias").get())
        assertEquals(false, worldConfig.stringPropertyHandle.getProperty("hidden").get())
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
    fun `Updating a non-existing property with setProperty returns false`() {
        assertTrue(worldConfig.stringPropertyHandle.setProperty("invalid-property", false).isFailure)
        assertTrue(worldConfig.stringPropertyHandle.setProperty("version", 1.1).isFailure)
    }
}
