package org.mvplugins.multiverse.core.destination

import org.bukkit.Location
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.destination.core.*
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
import org.mvplugins.multiverse.core.world.WorldManager
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DestinationTest : TestWithMockBukkit() {

    private lateinit var worldManager: WorldManager
    private lateinit var destinationsProvider: DestinationsProvider
    private lateinit var world: LoadedMultiverseWorld
    private lateinit var player: PlayerMock

    @BeforeTest
    fun setUp() {
        worldManager = serviceLocator.getActiveService(WorldManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldManager is not available as a service") }
        destinationsProvider = serviceLocator.getActiveService(DestinationsProvider::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("DestinationsProvider is not available as a service") }

        world = worldManager.createWorld(CreateWorldOptions.worldName("world")).get()
        player = server.addPlayer("benji_0224")
        player.bedSpawnLocation = Location(world.bukkitWorld.orNull, 5.0, 10.0, 5.0)
    }

    @Test
    fun `Bed destination instance`() {
        assertTrue(destinationsProvider.getDestinationById("b") is BedDestination)
        val destination = destinationsProvider.parseDestination("b:benji_0224").orNull
        assertTrue(destination is BedDestinationInstance)
        assertEquals(player.bedSpawnLocation, destination.getLocation(player).orNull)
        assertEquals("b:benji_0224", destination.toString())
    }

    @Test
    fun `Bed destination instance own player`() {
        val destination = destinationsProvider.parseDestination("b:playerbed").orNull
        assertTrue(destination is BedDestinationInstance)
        assertEquals(player.bedSpawnLocation, destination.getLocation(player).orNull)
        assertEquals("b:playerbed", destination.toString())
    }

    @Test
    fun `Cannon destination instance`() {
        assertTrue(destinationsProvider.getDestinationById("ca") is CannonDestination)
        val destination = destinationsProvider.parseDestination("ca:world:1.2,2,3:10.5:9.5:5").orNull
        assertTrue(destination is CannonDestinationInstance)
        val expectedLocation = Location(world.bukkitWorld.orNull, 1.2, 2.0, 3.0, 9.5F, 10.5F)
        assertEquals(expectedLocation, destination.getLocation(player).orNull)
        // todo: assert the Vector
        assertEquals("ca:world:1.2,2.0,3.0:10.5:9.5:5.0", destination.toString())
    }

    @Test
    fun `Exact destination instance`() {
        assertTrue(destinationsProvider.getDestinationById("e") is ExactDestination)
        val destination = destinationsProvider.parseDestination("e:world:1.2,2,3:10.5:9.5").orNull
        assertTrue(destination is ExactDestinationInstance)
        val expectedLocation = Location(world.bukkitWorld.orNull, 1.2, 2.0, 3.0, 9.5F, 10.5F)
        assertEquals(expectedLocation, destination.getLocation(player).orNull)
        assertEquals("e:world:1.2,2.0,3.0:10.5:9.5", destination.toString())
    }

    @Test
    fun `Exact destination instance from location`() {
        val exactDestination = serviceLocator.getActiveService(ExactDestination::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("ExactDestination is not available as a service") }

        val location = Location(world.bukkitWorld.orNull, 1.2, 2.0, 3.0, 9.5F, 10.5F)
        val destination = exactDestination.fromLocation(location)
        assertEquals(location, destination.getLocation(player).orNull)
        assertEquals("e:world:1.2,2.0,3.0:10.5:9.5", destination.toString())
    }

    @Test
    fun `Player destination instance`() {
        assertTrue(destinationsProvider.getDestinationById("pl") is PlayerDestination)
        val destination = destinationsProvider.parseDestination("pl:benji_0224").orNull
        assertTrue(destination is PlayerDestinationInstance)
        assertEquals(player.location, destination.getLocation(player).orNull)
        assertEquals("pl:benji_0224", destination.toString())
    }

    @Test
    fun `World destination instance`() {
        assertTrue(destinationsProvider.getDestinationById("w") is WorldDestination)
        val destination = destinationsProvider.parseDestination("w:world").orNull
        assertTrue(destination is WorldDestinationInstance)
        assertEquals(world.spawnLocation, destination.getLocation(player).orNull)
        assertEquals("w:world", destination.toString())
    }

    @Test
    fun `World destination instance without identifier`() {
        val destination = destinationsProvider.parseDestination("world").orNull
        assertTrue(destination is WorldDestinationInstance)
        assertEquals(world.spawnLocation, destination.getLocation(player).orNull)
        assertEquals("w:world", destination.toString())
    }

    @Test
    fun `World destination instance from alias name`() {
        world.setAlias("testalias")
        val destination = destinationsProvider.parseDestination("testalias").orNull
        assertTrue(destination is WorldDestinationInstance)
        assertEquals(world.spawnLocation, destination.getLocation(player).orNull)
        assertEquals("w:world", destination.toString())
    }

    @Test
    fun `Invalid destination instance`() {
        assertTrue(destinationsProvider.parseDestination("").isEmpty)
        assertTrue(destinationsProvider.parseDestination("idk:world").isEmpty)
        assertTrue(destinationsProvider.parseDestination("a:invalid-anchor").isEmpty)
        assertTrue(destinationsProvider.parseDestination("b:invalid-bed").isEmpty)
        assertTrue(destinationsProvider.parseDestination("ca:invalid-cannon").isEmpty)
        assertTrue(destinationsProvider.parseDestination("e:world:1,2,x").isEmpty)
        assertTrue(destinationsProvider.parseDestination("pl:invalid-player").isEmpty)
        assertTrue(destinationsProvider.parseDestination("w:invalid-world").isEmpty)
        // todo: should we make invalid yaw for WorldDestination fail?
        // assertTrue(destinationsProvider.parseDestination("w:world:f").isEmpty)
    }
}
