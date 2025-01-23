package org.mvplugins.multiverse.core.commands

import co.aikar.commands.MessageKeys
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.mvplugins.multiverse.core.locale.MVCorei18n
import org.mvplugins.multiverse.core.locale.message.Message
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VersionCommandTest : AbstractCommandTest() {

    @Test
    fun `Run version command as console`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv version"))
        val output = ChatColor.stripColor(console.nextMessage())
        assertEquals("Multiverse Core Version v" + multiverseCore.getDescription().getVersion(), output)
    }

    @Test
    fun `Run version command as player`() {
        addPermission("multiverse.core.version")
        assertTrue(player.performCommand("mv version"))
        assertCommandOutput(
            Message.of(
            MVCorei18n.VERSION_MV,
            "",
            replace("{version}").with(multiverseCore.getDescription().getVersion())))
    }

    @Test
    fun `Run version command as player - no permission`() {
        assertTrue(player.performCommand("mv version"))
        assertCommandOutput(Message.of(MessageKeys.PERMISSION_DENIED, ""))
    }
}
