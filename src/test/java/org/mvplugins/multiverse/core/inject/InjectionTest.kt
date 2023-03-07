package org.mvplugins.multiverse.core.inject

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.api.MVCore
import com.onarandombox.MultiverseCore.api.MVPlugin
import org.junit.jupiter.api.Test
import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class InjectionTest : TestWithMockBukkit() {

    @Test
    fun `MultiverseCore is available in its ServiceLocator as Multiverse`() {
        assertNotNull(multiverseCore.getService(MultiverseCore::class.java))
    }

    @Test
    fun `MultiverseCore is available in its ServiceLocator as MVCore`() {
        assertNotNull(multiverseCore.getService(MVCore::class.java))
    }

    @Test
    fun `MultiverseCore is available in its ServiceLocator as MVPlugin`() {
        assertNotNull(multiverseCore.getService(MVPlugin::class.java))
    }

    @Test
    fun `ServiceLocator provides same instance of MultiverseCore that the MockBukkit server creates`() {
        assertSame(multiverseCore, multiverseCore.getService(MultiverseCore::class.java));
    }
}
