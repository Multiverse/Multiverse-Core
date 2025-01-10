package org.mvplugins.multiverse.core.commands

import org.bukkit.Bukkit
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TeleportCommandTest : AbstractCommandTest() {

    @BeforeTest
    fun setUp() {
        server.setPlayers(3)
        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName("otherworld")).isSuccess)
    }

    @Test
    fun `Teleport a player to other world`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv tp Player1 otherworld --unsafe"))
        Thread.sleep(100) // wait for the player to teleport asynchronously
        assertLocation(server.getWorld("otherworld")?.spawnLocation, server.getPlayer("Player1")?.location)
    }

    @Test
    fun `Teleport multiple players to other world`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv tp Player1,Player2 otherworld --unsafe"))
        Thread.sleep(100) // wait for the player to teleport asynchronously
        assertLocation(server.getWorld("otherworld")?.spawnLocation, server.getPlayer("Player1")?.location)
        assertLocation(server.getWorld("otherworld")?.spawnLocation, server.getPlayer("Player2")?.location)
    }

    @Test
    fun `Teleport multiple players to invalid world`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv tp Player1,Player2 invalidworld"))
        Thread.sleep(100) // wait for the player to teleport asynchronously
        assertLocation(server.getWorld("world")?.spawnLocation, server.getPlayer("Player1")?.location)
        assertLocation(server.getWorld("world")?.spawnLocation, server.getPlayer("Player2")?.location)
    }

    @Test
    fun `Player no permission to teleport`() {
        player.performCommand("mv tp otherworld")
        Thread.sleep(100) // wait for the player to teleport asynchronously
        assertLocation(server.getWorld("world")?.spawnLocation, server.getPlayer("Player1")?.location)
    }

    private fun assertLocation(expected: org.bukkit.Location?, actual: org.bukkit.Location?) {
        assertEquals(expected?.world, actual?.world)
        assertEquals(expected?.x, actual?.x)
        assertEquals(expected?.y, actual?.y)
        assertEquals(expected?.z, actual?.z)
        assertEquals(expected?.yaw, actual?.yaw)
        assertEquals(expected?.pitch, actual?.pitch)
    }
}
