package org.mvplugins.multiverse.core

import be.seeseemelk.mockbukkit.MockBukkit
import com.dumptruckman.minecraft.util.Logging
import org.mvplugins.multiverse.core.inject.PluginServiceLocator
import org.mvplugins.multiverse.core.mock.MVServerMock
import org.mvplugins.multiverse.core.utils.TestingMode
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

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

    protected fun getResourceAsText(path: String): String? = object {}.javaClass.getResource(path)?.readText()
}
