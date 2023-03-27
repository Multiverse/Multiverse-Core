package org.mvplugins.multiverse.core

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.utils.TestingMode
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Basic abstract test class that sets up MockBukkit and MultiverseCore.
 */
abstract class TestWithMockBukkit {

    protected lateinit var server: ServerMock
    protected lateinit var multiverseCore: MultiverseCore

    @BeforeTest
    open fun setUp() {
        TestingMode.enable()
        server = MockBukkit.mock()
        multiverseCore = MockBukkit.load(MultiverseCore::class.java)
    }

    @AfterTest
    fun tearDown() {
        MockBukkit.unmock()
    }

    protected fun getResourceAsText(path: String): String? = object {}.javaClass.getResource(path)?.readText()
}
