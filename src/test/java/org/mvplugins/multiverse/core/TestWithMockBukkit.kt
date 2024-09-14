package org.mvplugins.multiverse.core

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import org.mvplugins.multiverse.core.inject.PluginServiceLocator
import org.mvplugins.multiverse.core.utils.TestingMode
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Basic abstract test class that sets up MockBukkit and MultiverseCore.
 */
abstract class TestWithMockBukkit {

    protected lateinit var server: ServerMock
    protected lateinit var multiverseCore: MultiverseCore
    protected lateinit var serviceLocator : PluginServiceLocator

    @BeforeTest
    fun setUpMockBukkit() {
        TestingMode.enable()
        server = MockBukkit.mock()
        multiverseCore = MockBukkit.load(MultiverseCore::class.java)
        serviceLocator = multiverseCore.serviceLocator
    }

    @AfterTest
    fun tearDownMockBukkit() {
        MockBukkit.unmock()
    }

    protected fun getResourceAsText(path: String): String? = object {}.javaClass.getResource(path)?.readText()
}
