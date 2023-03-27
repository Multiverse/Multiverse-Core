package org.mvplugins.multiverse.core.config

import com.onarandombox.MultiverseCore.config.MVCoreConfig
import org.mvplugins.multiverse.core.TestWithMockBukkit
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConfigTest : TestWithMockBukkit() {

    private lateinit var config : MVCoreConfig

    @BeforeTest
    fun setUp() {
        config = multiverseCore.getService(MVCoreConfig::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("MVCoreConfig is not available as a service") }

        val defaultConfig = getResourceAsText("/default_config.yml")
        assertNotNull(defaultConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "config.yml").absolutePathString()).writeText(defaultConfig)

        assertTrue(config.load())
        assertTrue(config.save())
    }

    @Test
    fun `Config is loaded`() {
        assertTrue(config.isLoaded)
    }

    @Test
    fun `Old config is migrated`() {
        val oldConfig = getResourceAsText("/old_config.yml")
        assertNotNull(oldConfig)
        File(Path.of(multiverseCore.dataFolder.absolutePath, "config.yml").absolutePathString()).writeText(oldConfig)
        assertTrue(config.load())
        assertTrue(config.save())

        assertEquals(true, config.enforceAccess)
        assertEquals(false, config.isEnablePrefixChat)
        assertEquals("[%world%]>>%chat%", config.prefixChatFormat)
        assertEquals(false, config.teleportIntercept)
        assertEquals(true, config.firstSpawnOverride)
        assertEquals(2, config.globalDebug)
        assertEquals(false, config.silentStart)
        assertEquals("world", config.firstSpawnLocation)
        assertEquals(false, config.isUsingCustomPortalSearch)
        assertEquals(128, config.customPortalSearchRadius)
        assertEquals(true, config.isAutoPurgeEntities)
        assertEquals(false, config.isShowingDonateMessage)
    }

    @Test
    fun `Getting existing config property with getProperty returns expected value`() {
        assertEquals(false, config.getProperty("enforce-access"))
        assertEquals("world", config.getProperty("first-spawn-location"))
    }

    @Test
    fun `Getting non-existing config property with getProperty returns null`() {
        assertNull(config.getProperty("invalid-property"))
        assertNull(config.getProperty("version"))
    }

    @Test
    fun `Getting existing config property by getter returns expected value`() {
        assertEquals(false, config.enforceAccess)
        assertEquals("world", config.firstSpawnLocation)
    }

    @Test
    fun `Updating an existing config property with setProperty reflects the changes in getProperty`() {
        assertTrue(config.setProperty("enforce-access", true))
        assertEquals(true, config.getProperty("enforce-access"))

        assertTrue(config.setProperty("first-spawn-location", "world2"))
        assertEquals("world2", config.getProperty("first-spawn-location"))

        assertTrue(config.setProperty("global-debug", 1))
        assertEquals(1, config.getProperty("global-debug"))
    }

    @Test
    fun `Updating a non-existing property with setProperty returns false`() {
        assertFalse(config.setProperty("invalid-property", false))
        assertFalse(config.setProperty("version", 1.1))
    }
}
