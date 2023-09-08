package org.mvplugins.multiverse.core.commandtools

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.onarandombox.MultiverseCore.commandtools.MVCommandIssuer
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager
import com.onarandombox.MultiverseCore.commandtools.PluginLocales
import com.onarandombox.MultiverseCore.utils.MVCorei18n
import com.onarandombox.MultiverseCore.utils.message.Message
import com.onarandombox.MultiverseCore.utils.message.MessageReplacement.replace
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class LocalizationTest : TestWithMockBukkit() {

    private lateinit var locales: PluginLocales
    private lateinit var commandManager: MVCommandManager

    @BeforeTest
    fun setUpLocale() {
        locales = assertNotNull(multiverseCore.getService(PluginLocales::class.java))
        commandManager = assertNotNull(multiverseCore.getService(MVCommandManager::class.java))
    }

    @Nested
    @DisplayName("Given a Message with only a non-localized message")
    inner class BasicMessage {

        private val messageString = "This is a test message"
        private val message = Message.of(messageString)

        @Test
        fun `The raw message should be the same as the original`() {
            assertEquals(messageString, message.raw())
        }

        @Test
        fun `The formatted message should be the same as the original`() {
            assertEquals(messageString, message.formatted())
        }

        @Test
        fun `The formatted message with PluginLocales should be the same as the original`() {
            assertEquals(messageString, message.formatted(locales))
        }

        @Test
        fun `The formatted message with PluginLocales for a CommandIssuer should be the same as the original`() {
            assertEquals(messageString, message.formatted(locales, commandManager.consoleCommandIssuer))
        }

        @Nested
        @DisplayName("And a command sender is provided")
        inner class WithCommandSender {

            private lateinit var sender: CommandSender
            private lateinit var issuer: MVCommandIssuer

            @BeforeTest
            fun setUp() {
                sender = spy(Bukkit.getConsoleSender())
                issuer = commandManager.getCommandIssuer(sender)
            }

            @Test
            fun `Sending the issuer the message should send the formatted message to the sender`() {
                issuer.sendInfo(message);

                verify(sender).sendMessage("§9§9$messageString")
            }
        }
    }

    @Nested
    @DisplayName("Given a Message with a non-localized message and one replacement")
    inner class MessageWithOneReplacement {

        private val replacementKey = "{count}"
        private val messageString = "This is a test message with $replacementKey replacement"
        private val replacedMessageString = messageString.replace(replacementKey, "one")

        private val message = Message.of(messageString, replace(replacementKey).with("one"))

        @Test
        fun `The raw message should be the same as the original`() {
            assertEquals(messageString, message.raw())
        }

        @Test
        fun `The formatted message should be the replaced message string`() {
            assertEquals(replacedMessageString, message.formatted())
        }

        @Test
        fun `The formatted message with PluginLocales should be the replaced message string`() {
            assertEquals(replacedMessageString, message.formatted(locales))
        }

        @Test
        fun `The formatted message with PluginLocales for a CommandIssuer should be the replaced message string`() {
            assertEquals(replacedMessageString, message.formatted(locales, commandManager.consoleCommandIssuer))
        }

        @Nested
        @DisplayName("And a command sender is provided")
        inner class WithCommandSender {

            private lateinit var sender: CommandSender
            private lateinit var issuer: MVCommandIssuer

            @BeforeTest
            fun setUp() {
                sender = spy(Bukkit.getConsoleSender())
                issuer = commandManager.getCommandIssuer(sender)
            }

            @Test
            fun `Sending the issuer the message should send the formatted message to the sender`() {
                issuer.sendInfo(message);

                verify(sender).sendMessage("§9§9$replacedMessageString")
            }
        }
    }

    @Nested
    @DisplayName("Given a Message with a non-localized message and two replacements")
    inner class MessageWithTwoReplacements {

        private val replacementKey1 = "{thing1}"
        private val replacementKey2 = "{thing2}"
        private val messageString = "$replacementKey1 $replacementKey2"
        private val replacedMessageString = messageString
            .replace(replacementKey1, "one")
            .replace(replacementKey2, "two")

        private val message = Message.of(
            messageString,
            replace(replacementKey1).with("one"),
            replace(replacementKey2).with("two"),
        )

        @Test
        fun `The raw message should be the same as the original`() {
            assertEquals(messageString, message.raw())
        }

        @Test
        fun `The formatted message should be the replaced message string`() {
            assertEquals(replacedMessageString, message.formatted())
        }

        @Test
        fun `The formatted message with PluginLocales should be the replaced message string`() {
            assertEquals(replacedMessageString, message.formatted(locales))
        }

        @Test
        fun `The formatted message with PluginLocales for a CommandIssuer should be the replaced message string`() {
            assertEquals(replacedMessageString, message.formatted(locales, commandManager.consoleCommandIssuer))
        }

        @Nested
        @DisplayName("And a command sender is provided")
        inner class WithCommandSender {

            private lateinit var sender: CommandSender
            private lateinit var issuer: MVCommandIssuer

            @BeforeTest
            fun setUp() {
                sender = spy(Bukkit.getConsoleSender())
                issuer = commandManager.getCommandIssuer(sender)
            }

            @Test
            fun `Sending the issuer the message should send the formatted message to the sender`() {
                issuer.sendInfo(message);

                verify(sender).sendMessage("§9§9$replacedMessageString")
            }
        }
    }

    @Nested
    @DisplayName("Given a Message with a localized message with one replacement")
    inner class LocalizedMessage {

        private val replacementKey = "{world}"
        private val replacementValue = "World"
        private val messageString = "Hello $replacementKey!"
        private val replacedMessageString = messageString.replace(replacementKey, replacementValue)

        private val message = MVCorei18n.CLONEWORLD_CLONED
            .bundle(messageString, replace(replacementKey).with(replacementValue))

        @Test
        fun `The raw message should be the same as the original`() {
            assertEquals(messageString, message.raw())
        }

        @Test
        fun `The formatted message should be the replaced original string`() {
            assertEquals(replacedMessageString, message.formatted())
        }

        @Test
        fun `The formatted message with PluginLocales should be different from the replaced original string`() {
            assertNotEquals(replacedMessageString, message.formatted(locales))
        }

        @Test
        fun `The formatted message with PluginLocales should have performed replacement`() {
            assertThat(message.formatted(locales), !containsSubstring(replacementKey))
            assertThat(message.formatted(locales), containsSubstring(replacementValue))
        }

        @Nested
        @DisplayName("And a command sender is provided")
        inner class WithCommandSender {

            private lateinit var sender: CommandSender
            private lateinit var issuer: MVCommandIssuer

            @BeforeTest
            fun setUp() {
                sender = spy(Bukkit.getConsoleSender())
                issuer = commandManager.getCommandIssuer(sender)
            }

            @Test
            fun `Sending the issuer the message should send the formatted message to the sender`() {
                issuer.sendInfo(message);

                val sentMessage = argumentCaptor<String> {
                    verify(sender).sendMessage(capture())
                }.firstValue

                assertNotEquals(replacedMessageString, sentMessage)
                assertThat(sentMessage, !containsSubstring(replacementKey))
                assertThat(sentMessage, containsSubstring(replacementValue))
            }
        }
    }
}