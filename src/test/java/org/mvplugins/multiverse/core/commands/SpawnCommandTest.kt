package org.mvplugins.multiverse.core.commands

import org.bukkit.Bukkit
import org.bukkit.Location
import org.mvplugins.multiverse.core.world.location.SpawnLocation
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SpawnCommandTest : AbstractCommandTest() {

    @BeforeTest
    fun setUp() {
        server.addPlayer("Player1")
        server.addPlayer("Player2")
        server.addPlayer("Player3")
        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName("otherworld")).isSuccess)
        server.getWorld("otherworld")?.spawnLocation?.let { Bukkit.getPlayer("Player1")?.teleport(it) }
        worldManager.getLoadedWorld("world").get().setSpawnLocation(SpawnLocation(20.0, 20.0, 20.0))
        worldManager.getLoadedWorld("otherworld").get().setSpawnLocation(SpawnLocation(20.0, 20.0, 20.0))
    }

    @Test
    fun `Teleport player to spawn location`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv spawn Player1 --unsafe"))
        Thread.sleep(10) // wait for the player to teleport asynchronously
        assertLocationEquals(server.getWorld("otherworld")?.spawnLocation, server.getPlayer("Player1")?.location)
    }

    @Test
    fun `Teleport multiple players to spawn location`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv spawn Player1,Player2,Player3 --unsafe"))
        Thread.sleep(10) // wait for the player to teleport asynchronously
        assertLocationEquals(server.getWorld("otherworld")?.spawnLocation, server.getPlayer("Player1")?.location)
        assertLocationEquals(server.getWorld("world")?.spawnLocation, server.getPlayer("Player2")?.location)
        assertLocationEquals(server.getWorld("world")?.spawnLocation, server.getPlayer("Player3")?.location)
    }

    @Test
    fun `No permission to teleport to spawn`() {
        assertTrue(player.performCommand("mv spawn"))
        Thread.sleep(10) // wait for the player to teleport asynchronously
        assertLocationEquals(Location(server.getWorld("world"), 0.0, 5.0, 0.0), player.location)
    }

    @Test
    fun `Self permission to teleport to spawn`() {
        addPermission("multiverse.core.spawn.self.world")
        assertTrue(player.performCommand("mv spawn --unsafe"))
        Thread.sleep(10) // wait for the player to teleport asynchronously
        assertLocationEquals(server.getWorld("world")?.spawnLocation, player.location)
    }

    @Test
    fun `Other permission to teleport to spawn`() {
        addPermission("multiverse.core.spawn.other.world")
        assertTrue(player.performCommand("mv spawn Player1,Player2 --unsafe"))
        Thread.sleep(10) // wait for the player to teleport asynchronously
        assertLocationEquals(Location(server.getWorld("otherworld"), 0.0, 5.0, 0.0), server.getPlayer("Player1")?.location)
        assertLocationEquals(server.getWorld("world")?.spawnLocation, server.getPlayer("Player2")?.location)
    }
    @Test
    fun `Teleport others to spawn but not self`() {
        addPermission("multiverse.core.spawn.other.otherworld")
        assertTrue(player.performCommand("mv spawn benwoo1110,Player1 --unsafe"))
        Thread.sleep(10) // wait for the player to teleport asynchronously
        assertLocationEquals(Location(server.getWorld("world"), 0.0, 5.0, 0.0), server.getPlayer("benwoo1110")?.location)
        assertLocationEquals(server.getWorld("otherworld")?.spawnLocation, server.getPlayer("Player1")?.location)
    }
}
