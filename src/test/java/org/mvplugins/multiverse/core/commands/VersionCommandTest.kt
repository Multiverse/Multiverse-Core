package org.mvplugins.multiverse.core.commands

import be.seeseemelk.mockbukkit.entity.PlayerMock
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VersionCommandTest : TestWithMockBukkit() {

    private lateinit var player: PlayerMock

    @BeforeTest
    fun setUp() {
        player = server.addPlayer("benwoo1110");
        assertEquals(player, server.getPlayer("benwoo1110"))
    }

    @Test
    fun `Run version command as console`() {
        val console = server.consoleSender;
        assertTrue(Bukkit.dispatchCommand(console, "mv version"))
        val output = ChatColor.stripColor(console.nextMessage())
        assertEquals("Multiverse Core Version vlocal", output)
    }

    @Test
    fun `Run version command as player`() {
        assertTrue(player.performCommand("mv version"))
        val output = ChatColor.stripColor(player.nextMessage())
        assertEquals("I'm sorry, but you do not have permission to perform this command.", output)
    }
}
