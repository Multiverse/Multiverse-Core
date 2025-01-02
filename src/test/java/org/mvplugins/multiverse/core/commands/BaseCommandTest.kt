package org.mvplugins.multiverse.core.commands

import org.bukkit.ChatColor
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.permissions.PermissionAttachment
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.commandtools.MVCommandManager
import org.mvplugins.multiverse.core.commandtools.PluginLocales
import org.mvplugins.multiverse.core.utils.message.Message
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

abstract class BaseCommandTest : TestWithMockBukkit() {

    protected lateinit var player: PlayerMock
    protected lateinit var console: ConsoleCommandSender

    private lateinit var locales : PluginLocales
    private lateinit var attachment : PermissionAttachment

    @BeforeTest
    fun setUpCommand() {
        val commandManager = serviceLocator.getActiveService(MVCommandManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("MVCommandManager is not available as a service") }
        locales = commandManager.locales

        console = server.consoleSender
        player = server.addPlayer("benwoo1110");
        assertEquals(player, server.getPlayer("benwoo1110"))
        attachment = player.addAttachment(multiverseCore)
        assertNotNull(attachment)
    }

    fun addPermission(permission: String) {
        attachment.setPermission(permission, true)
    }

    fun assertCommandOutput(message : Message) {
        assertEquals(
            ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',message.formatted(locales))),
            ChatColor.stripColor(player.nextMessage())
        )
    }
}
