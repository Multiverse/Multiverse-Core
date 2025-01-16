package org.mvplugins.multiverse.core.inject

import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.anchor.AnchorManager
import org.mvplugins.multiverse.core.api.BlockSafety
import org.mvplugins.multiverse.core.api.LocationManipulation
import org.mvplugins.multiverse.core.commands.CoreCommand
import org.mvplugins.multiverse.core.commandtools.MVCommandManager
import org.mvplugins.multiverse.core.config.MVCoreConfig
import org.mvplugins.multiverse.core.destination.Destination
import org.mvplugins.multiverse.core.economy.MVEconomist
import org.mvplugins.multiverse.core.listeners.*
import org.mvplugins.multiverse.core.teleportation.AdvancedBlockSafety
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter
import org.mvplugins.multiverse.core.teleportation.SimpleBlockSafety
import org.mvplugins.multiverse.core.teleportation.SimpleLocationManipulation
import org.mvplugins.multiverse.core.teleportation.TeleportQueue
import org.mvplugins.multiverse.core.utils.metrics.MetricsConfigurator
import org.mvplugins.multiverse.core.world.WorldManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InjectionTest : TestWithMockBukkit() {

    @Test
    fun `AnchorManager is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(AnchorManager::class.java))
    }

    @Test
    fun `AsyncSafetyTeleporter is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(AsyncSafetyTeleporter::class.java))
    }

    @Test
    fun `AdvancedBlockSafety is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(AdvancedBlockSafety::class.java))
    }

    @Test
    fun `MVCommandManager is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVCommandManager::class.java))
    }

    @Test
    fun `MVEconomist is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVEconomist::class.java))
    }

    @Test
    fun `LocationManipulation is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(LocationManipulation::class.java))
        assertNotNull(serviceLocator.getActiveService(SimpleLocationManipulation::class.java))
    }

    @Test
    fun `TeleportQueue is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(TeleportQueue::class.java))
    }

    @Test
    fun `MVWorldManager is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(WorldManager::class.java))
    }

    @Test
    fun `MVEntityListener is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVEntityListener::class.java))
    }

    @Test
    fun `MVPlayerListener is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVPlayerListener::class.java))
    }

    @Test
    fun `MVChatListener is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVChatListener::class.java))
    }

    @Test
    fun `MVPortalListener is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVPortalListener::class.java))
    }

    @Test
    fun `MVWeatherListener is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVWeatherListener::class.java))
    }

    @Test
    fun `MVWorldListener is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVWorldListener::class.java))
    }

    @Test
    fun `MVCoreConfig is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVCoreConfig::class.java))
    }

    @Test
    fun `Commands are available as services`() {
        val commands = serviceLocator.getAllActiveServices(CoreCommand::class.java)
        assertEquals(30, commands.size)
    }

    @Test
    fun `Destinations are available as services`() {
        val destinations = serviceLocator.getAllActiveServices(Destination::class.java)
        assertEquals(6, destinations.size)
    }

    @Test
    fun `MetricsConfigurator is not available as a service`() {
        // Also making sure this is not loaded automatically since it's supposed to be disabled during tests
        assertNull(serviceLocator.getActiveService(MetricsConfigurator::class.java))
    }
}
