package org.mvplugins.multiverse.core

import kotlin.test.Test
import kotlin.test.assertNotNull

open class MockBukkitTest : TestWithMockBukkit() {

    @Test
    fun `MockBukkit loads the plugin`() {
        assertNotNull(multiverseCore)
    }
}
