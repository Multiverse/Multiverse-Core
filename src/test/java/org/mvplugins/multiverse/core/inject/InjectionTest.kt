package org.mvplugins.multiverse.core.inject

import com.onarandombox.MultiverseCore.anchor.AnchorManager
import com.onarandombox.MultiverseCore.api.BlockSafety
import com.onarandombox.MultiverseCore.api.Destination
import com.onarandombox.MultiverseCore.api.LocationManipulation
import com.onarandombox.MultiverseCore.api.MVConfig
import com.onarandombox.MultiverseCore.api.MVWorldManager
import com.onarandombox.MultiverseCore.api.SafeTTeleporter
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand
import com.onarandombox.MultiverseCore.config.MVCoreConfigProvider
import com.onarandombox.MultiverseCore.economy.MVEconomist
import com.onarandombox.MultiverseCore.listeners.MVChatListener
import com.onarandombox.MultiverseCore.listeners.MVEntityListener
import com.onarandombox.MultiverseCore.listeners.MVPlayerListener
import com.onarandombox.MultiverseCore.listeners.MVPortalListener
import com.onarandombox.MultiverseCore.listeners.MVWeatherListener
import com.onarandombox.MultiverseCore.listeners.MVWorldInitListener
import com.onarandombox.MultiverseCore.listeners.MVWorldListener
import com.onarandombox.MultiverseCore.teleportation.SimpleBlockSafety
import com.onarandombox.MultiverseCore.teleportation.SimpleLocationManipulation
import com.onarandombox.MultiverseCore.teleportation.SimpleSafeTTeleporter
import com.onarandombox.MultiverseCore.utils.UnsafeCallWrapper
import com.onarandombox.MultiverseCore.utils.metrics.MetricsConfigurator
import com.onarandombox.MultiverseCore.world.SimpleMVWorldManager
import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.Test
import kotlin.test.assertNull

class InjectionTest : TestWithMockBukkit() {

    @Test
    fun `AnchorManager is available as a service`() {
        assertNotNull(multiverseCore.getService(AnchorManager::class.java))
    }

    @Test
    fun `BlockSafety is available as a service`() {
        assertNotNull(multiverseCore.getService(BlockSafety::class.java))
        assertNotNull(multiverseCore.getService(SimpleBlockSafety::class.java))
    }

    @Test
    fun `MVCommandManager is available as a service`() {
        assertNotNull(multiverseCore.getService(MVCommandManager::class.java))
    }

    @Test
    fun `MVEconomist is available as a service`() {
        assertNotNull(multiverseCore.getService(MVEconomist::class.java))
    }

    @Test
    fun `LocationManipulation is available as a service`() {
        assertNotNull(multiverseCore.getService(LocationManipulation::class.java))
        assertNotNull(multiverseCore.getService(SimpleLocationManipulation::class.java))
    }

    @Test
    fun `SafeTTeleporter is available as a service`() {
        assertNotNull(multiverseCore.getService(SafeTTeleporter::class.java))
        assertNotNull(multiverseCore.getService(SimpleSafeTTeleporter::class.java))
    }

    @Test
    fun `UnsafeCallWrapper is available as a service`() {
        assertNotNull(multiverseCore.getService(UnsafeCallWrapper::class.java))
    }

    @Test
    fun `MVWorldManager is available as a service`() {
        assertNotNull(multiverseCore.getService(MVWorldManager::class.java))
        assertNotNull(multiverseCore.getService(SimpleMVWorldManager::class.java))
    }

    @Test
    fun `MVEntityListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVEntityListener::class.java))
    }

    @Test
    fun `MVPlayerListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVPlayerListener::class.java))
    }

    @Test
    fun `MVChatListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVChatListener::class.java))
    }

    @Test
    fun `MVPortalListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVPortalListener::class.java))
    }

    @Test
    fun `MVWeatherListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVWeatherListener::class.java))
    }

    @Test
    fun `MVWorldListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVWorldListener::class.java))
    }

    @Test
    fun `MVWorldInitListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVWorldInitListener::class.java))
    }

    @Test
    fun `MVCoreConfigProvider is available as a service`() {
        assertNotNull(multiverseCore.getService(MVCoreConfigProvider::class.java))
    }

    @Test
    fun `Commands are available as services`() {
        val commands = multiverseCore.getAllServices(MultiverseCommand::class.java)
        // TODO come up with a better way to test this like via actually testing the effect of calling each command
        assertEquals(17, commands.size)
    }

    @Test
    fun `Destinations are available as services`() {
        val destinations = multiverseCore.getAllServices(Destination::class.java)
        // TODO come up with a better way to test this like via actually testing the effect of using each destination
        assertEquals(6, destinations.size)
    }

    @Test
    fun `MVConfig is not available as a service`() {
        // We need one test case for asking for non-services to make sure we don't accidentally make them available
        // and that the getService method doesn't throw an exception
        assertNull(multiverseCore.getService(MVConfig::class.java))
    }

    @Test
    fun `MetricsConfigurator is not available as a service`() {
        // Also making sure this is not loaded automatically since it's supposed to be disabled during tests
        assertNull(multiverseCore.getService(MetricsConfigurator::class.java))
    }
}
