package org.mvplugins.multiverse.core.world

import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigManager
import org.bukkit.Location
import org.junit.jupiter.api.Assertions
import org.mvplugins.multiverse.core.TestWithMockBukkit
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WorldConfigTest : TestWithMockBukkit() {

    private lateinit var worldConfigManager : WorldsConfigManager
    private lateinit var worldConfig : WorldConfig

    @BeforeTest
    fun setUp() {
        val defaultConfig = getResourceAsText("/default_worlds.yml")
        assertNotNull(defaultConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds2.yml").absolutePathString()).writeText(defaultConfig)

        worldConfigManager = multiverseCore.getService(WorldsConfigManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldsConfigManager is not available as a service") }

        assertTrue(worldConfigManager.load().isSuccess)
        worldConfig = worldConfigManager.getWorldConfig("world").orNull.takeIf { it != null } ?: run {
            throw IllegalStateException("WorldConfig for world is not available") }
        assertNotNull(worldConfig);
    }

    @Test
    fun `Getting existing world property with getProperty returns expected value`() {
        assertEquals("my world", worldConfig.getProperty("alias").get())
        assertEquals(false, worldConfig.getProperty("hidden").get())
    }

    @Test
    fun `Getting non-existing world property with getProperty returns null`() {
        assertTrue(worldConfig.getProperty("invalid-property").isFailure)
        assertTrue(worldConfig.getProperty("version").isFailure)
    }

    @Test
    fun `Getting existing world property by getter returns expected value`() {
        assertEquals("my world", worldConfig.alias)
        assertEquals(false, worldConfig.isHidden)
    }

    @Test
    fun `Updating an existing world property with setProperty reflects the changes in getProperty`() {
        assertTrue(worldConfig.setProperty("adjust-spawn", true).isSuccess)
        assertEquals(true, worldConfig.getProperty("adjust-spawn").get())

        assertTrue(worldConfig.setProperty("alias", "abc").isSuccess)
        assertEquals("abc", worldConfig.getProperty("alias").get())

        assertTrue(worldConfig.setProperty("scale", 2.0).isSuccess)
        assertEquals(2.0, worldConfig.getProperty("scale").get())

        val blacklists = listOf("a", "b", "c")
        assertTrue(worldConfig.setProperty("world-blacklist", blacklists).isSuccess)
        assertEquals(blacklists, worldConfig.getProperty("world-blacklist").get())
    }

    @Test
    fun `Updating a non-existing property with setProperty returns false`() {
        assertTrue(worldConfig.setProperty("invalid-property", false).isFailure)
        assertTrue(worldConfig.setProperty("version", 1.1).isFailure)
    }
}
