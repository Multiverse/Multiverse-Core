package org.mvplugins.multiverse.core.commands

import co.aikar.commands.MessageKeys
import org.bukkit.Bukkit
import org.junit.jupiter.api.Test
import org.mvplugins.multiverse.core.config.MVCoreConfig
import org.mvplugins.multiverse.core.locale.MVCorei18n
import org.mvplugins.multiverse.core.locale.message.Message
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfigCommandTest : AbstractCommandTest() {

    @Test
    fun `Modify config global-debug`() {
        assertTrue(Bukkit.dispatchCommand(console, "mv config global-debug 2"))
        val coreConfig = serviceLocator.getActiveService(MVCoreConfig::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("MVCoreConfig is not available as a service") }
        assertEquals(2, coreConfig.globalDebug)
    }

    @Test
    fun `Modify config global-debug player no permission`() {
        assertTrue(player.performCommand("mv config global-debug 2"))
        assertCommandOutput(Message.of(MessageKeys.PERMISSION_DENIED, ""))

        val coreConfig = serviceLocator.getActiveService(MVCoreConfig::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("MVCoreConfig is not available as a service") }
        assertEquals(0, coreConfig.globalDebug)
    }

    @Test
    fun `Modify non-existing config property`() {
        addPermission("multiverse.core.config")
        assertTrue(player.performCommand("mv config invalid-property test"))
        player.nextMessage() // ignore the first line
        assertCommandOutput(
            Message.of(
            MVCorei18n.CONFIG_NODE_NOTFOUND,
            "",
            replace("{node}").with("invalid-property")))
    }

    @Test
    fun `Modify config global-debug invalid type`() {
        addPermission("multiverse.core.config")
        assertTrue(player.performCommand("mv config global-debug what"))
        player.nextMessage() // ignore the first line
        assertCommandOutput(Message.of("Unable to convert 'what' to number. (integer)"))
    }
}
