package org.mvplugins.multiverse.core.inject

import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.anchor.AnchorManager
import org.mvplugins.multiverse.core.commands.CoreCommand
import org.mvplugins.multiverse.core.commandtools.MVCommandManager
import org.mvplugins.multiverse.core.config.MVCoreConfig
import org.mvplugins.multiverse.core.destination.Destination
import org.mvplugins.multiverse.core.economy.MVEconomist
import org.mvplugins.multiverse.core.listeners.CoreListener
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter
import org.mvplugins.multiverse.core.teleportation.BlockSafety
import org.mvplugins.multiverse.core.teleportation.LocationManipulation
import org.mvplugins.multiverse.core.teleportation.TeleportQueue
import org.mvplugins.multiverse.core.world.WorldManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
    fun `BlockSafety is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(BlockSafety::class.java))
        assertNotNull(serviceLocator.getActiveService(BlockSafety::class.java))
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
        assertNotNull(serviceLocator.getActiveService(LocationManipulation::class.java))
    }

    @Test
    fun `TeleportQueue is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(TeleportQueue::class.java))
    }

    @Test
    fun `MVWorldManager is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(WorldManager::class.java))
        assertNotNull(serviceLocator.getActiveService(WorldManager::class.java))
    }

    @Test
    fun `MVEntityListener is available as a service`() {
        val listeners = assertNotNull(serviceLocator.getAllServices(CoreListener::class.java))
        assertEquals(7, listeners.size)
    }

    @Test
    fun `MVCoreConfig is available as a service`() {
        assertNotNull(serviceLocator.getActiveService(MVCoreConfig::class.java))
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
}
