package org.mvplugins.multiverse.core.world

import com.onarandombox.MultiverseCore.worldnew.config.SpawnLocation
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mvplugins.multiverse.core.TestWithMockBukkit
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class WorldConfigMangerTest : TestWithMockBukkit() {

    private lateinit var worldConfigManager : WorldsConfigManager

    @BeforeTest
    fun setUp() {
        val defaultConfig = getResourceAsText("/default_worlds.yml")
        assertNotNull(defaultConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds2.yml").absolutePathString()).writeText(defaultConfig)

        worldConfigManager = multiverseCore.getService(WorldsConfigManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldsConfigManager is not available as a service") }
    }

    @Test
    fun `World config is loaded`() {
        assertTrue(worldConfigManager.isLoaded)
    }

    @Test
    fun `Old world config is migrated`() {
        val oldConfig = getResourceAsText("/old_worlds.yml")
        assertNotNull(oldConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds2.yml").absolutePathString()).writeText(oldConfig)

        assertTrue(worldConfigManager.load().isSuccess)
        assertTrue(worldConfigManager.save().isSuccess)
        //compareConfigFile("worlds2.yml", "/migrated_worlds.yml")
    }

    @Test
    fun `Add a new world to config`() {
        assertTrue(worldConfigManager.load().isSuccess)
        val worldConfig = worldConfigManager.addWorldConfig("newworld")
        assertTrue(worldConfigManager.save().isSuccess)
        compareConfigFile("worlds2.yml", "/newworld_worlds.yml")
    }

    @Test
    fun `Updating existing world properties`() {
        assertTrue(worldConfigManager.load().isSuccess)
        val worldConfig = worldConfigManager.getWorldConfig("world")
        assertNotNull(worldConfig)

        worldConfig.setProperty("adjust-spawn", true)
        worldConfig.setProperty("alias", "newalias")
        worldConfig.setProperty("spawn-location", SpawnLocation(-64.0, 64.0, 48.0))
        assertTrue(worldConfigManager.save().isSuccess)
    }

    @Test
    fun `Delete world section from config`() {
        assertTrue(worldConfigManager.load().isSuccess)
        worldConfigManager.deleteWorldConfig("world")
        assertTrue(worldConfigManager.save().isSuccess)
        compareConfigFile("worlds2.yml", "/delete_worlds.yml")
    }

    private fun compareConfigFile(configPath: String, comparePath: String) {
        // TODO: Map keys may not guaranteed order. Potentially look at Hamkrest and assertThat.
        val config = multiverseCore.dataFolder.toPath().resolve(configPath).toFile().readText()
        val configCompare = getResourceAsText(comparePath)
        assertNotNull(configCompare)
        assertEquals(configCompare, config)
    }
}
