package org.mvplugins.multiverse.core

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.utils.TestingMode
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class MockBukkitTest {

    lateinit var server: ServerMock
    lateinit var plugin: MultiverseCore

    @BeforeTest
    fun setUp() {
        TestingMode.enable()
        server = MockBukkit.mock()
        plugin = MockBukkit.load(MultiverseCore::class.java)
    }

    @AfterTest
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `MockBukkit loads the plugin`() {
        assertNotNull(plugin)
    }
}
