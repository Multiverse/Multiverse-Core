package org.mvplugins.multiverse.core.inject

import com.onarandombox.MultiverseCore.MultiverseCore
import org.junit.jupiter.api.Test
import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class InjectionTest : TestWithMockBukkit() {

    @Test
    fun `MultiverseCore is available in its ServiceLocator`() {
        assertNotNull(multiverseCore.getService(MultiverseCore::class.java))
    }

    @Test
    fun `ServiceLocator provides same instance of MultiverseCore that the MockBukkit server creates`() {
        assertSame(multiverseCore, multiverseCore.getService(MultiverseCore::class.java));
    }
}
