package org.mvplugins.multiverse.core

import com.dumptruckman.minecraft.util.Logging
import org.bukkit.Location
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.YamlConfiguration
import org.mockbukkit.mockbukkit.MockBukkit
import org.mvplugins.multiverse.core.inject.PluginServiceLocator
import org.mvplugins.multiverse.core.mock.MVServerMock
import kotlin.test.*

/**
 * Basic abstract test class that sets up MockBukkit and MultiverseCore.
 */
abstract class TestWithMockBukkit {

    protected lateinit var server: MVServerMock
    protected lateinit var multiverseCore: MultiverseCore
    protected lateinit var serviceLocator : PluginServiceLocator

    @BeforeTest
    fun setUpMockBukkit() {
        TestingMode.enable()
        server = MockBukkit.mock(MVServerMock())
        multiverseCore = MockBukkit.load(MultiverseCore::class.java)
        Logging.setDebugLevel(3)
        serviceLocator = multiverseCore.serviceLocator
        assertNotNull(server.commandMap)
    }

    @AfterTest
    fun tearDownMockBukkit() {
        MockBukkit.unmock()
    }

    fun getResourceAsText(path: String): String? = object {}.javaClass.getResource(path)?.readText()

    fun assertConfigEquals(expectedPath: String, actualPath: String) {
        val actualString = multiverseCore.dataFolder.toPath().resolve(actualPath).toFile().readText()
        val expectedString = getResourceAsText(expectedPath)
        assertNotNull(expectedString)

        val actualYaml = YamlConfiguration()
        actualYaml.loadFromString(actualString)
        val actualYamlKeys = HashSet(actualYaml.getKeys(true))

        val expectedYaml = YamlConfiguration()
        expectedYaml.loadFromString(expectedString)
        val expectedYamlKeys = HashSet(expectedYaml.getKeys(true))

        for (key in expectedYamlKeys) {
            assertNotNull(actualYamlKeys.remove(key), "Key $key is missing in actual config")
            val actualValue = actualYaml.get(key)
            if (actualValue is MemorySection) {
                continue
            }
            assertEquals(expectedYaml.get(key), actualYaml.get(key), "Value for $key is different.")
        }
        for (key in actualYamlKeys) {
            assertNull(actualYaml.get(key), "Key $key is present in actual config when it should be empty.")
        }

        assertEquals(0, actualYamlKeys.size,
            "Actual config has more keys than expected config. The following keys are missing: $actualYamlKeys")
    }

    fun assertLocationEquals(expected: Location?, actual: Location?) {
        assertEquals(expected?.world, actual?.world, "Worlds don't match for location comparison ($expected, $actual)")
        assertEquals(expected?.x, actual?.x, "X values don't match for location comparison ($expected, $actual)")
        assertEquals(expected?.y, actual?.y, "Y values don't match for location comparison ($expected, $actual)")
        assertEquals(expected?.z, actual?.z, "Z values don't match for location comparison ($expected, $actual)")
        assertEquals(expected?.yaw, actual?.yaw, "Yaw values don't match for location comparison ($expected, $actual)")
        assertEquals(expected?.pitch, actual?.pitch, "Pitch values don't match for location comparison ($expected, $actual)")
    }
}
