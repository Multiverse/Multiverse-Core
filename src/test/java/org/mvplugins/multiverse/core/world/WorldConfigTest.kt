package org.mvplugins.multiverse.core.world

import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig
import com.onarandombox.MultiverseCore.worldnew.config.WorldsConfigFile
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

    private lateinit var worldConfigFile : WorldsConfigFile

    @BeforeTest
    fun setUp() {
        val defaultConfig = getResourceAsText("/default_worlds.yml")
        assertNotNull(defaultConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "worlds2.yml").absolutePathString()).writeText(defaultConfig)

        worldConfigFile = WorldsConfigFile(multiverseCore)
        worldConfigFile.load()
    }

    @Test
    fun `World config is loaded`() {
        assertTrue(worldConfigFile.isLoaded)
    }

    @Test
    fun `Old world config is migrated`() {
        // TODO
    }

    @Test
    fun `Add a new world to config`() {
        val worldConfig = WorldConfig(worldConfigFile.getWorldConfigSection("newworld"))
        worldConfigFile.save()

        compareConfigFile("worlds2.yml", "/newworld_worlds.yml")
    }

    @Test
    fun `Updating existing world properties`() {
        val worldConfig = WorldConfig(worldConfigFile.getWorldConfigSection("world"))
        worldConfig.setProperty("adjust-spawn", true)
        worldConfig.setProperty("alias", "newalias")

        worldConfigFile.save()

        assertEquals(true, worldConfig.getProperty("adjust-spawn").get())
        assertEquals("newalias", worldConfig.getProperty("alias").get())

        compareConfigFile("worlds2.yml", "/properties_worlds.yml")
    }

    @Test
    fun `Delete world section from config`() {
        worldConfigFile.deleteWorldConfigSection("world")
        worldConfigFile.save()

        compareConfigFile("worlds2.yml", "/delete_worlds.yml")
    }

    private fun compareConfigFile(configPath: String, comparePath: String) {
        val config = multiverseCore.dataFolder.toPath().resolve(configPath).toFile().readText()
        val configCompare = getResourceAsText(comparePath)
        assertNotNull(configCompare)
        assertEquals(configCompare, config)
    }
}
