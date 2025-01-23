package org.mvplugins.multiverse.core.commandtools

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.endsWith
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.config.MVCoreConfig
import org.mvplugins.multiverse.core.locale.MVCorei18n
import org.mvplugins.multiverse.core.locale.PluginLocales
import org.mvplugins.multiverse.core.locale.message.Message
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace
import java.util.Locale
import kotlin.test.*

class LocalizationTest : TestWithMockBukkit() {

    private lateinit var locales: PluginLocales
    private lateinit var commandManager: MVCommandManager

    @BeforeTest
    fun setUpLocale() {
        commandManager = assertNotNull(serviceLocator.getActiveService(MVCommandManager::class.java))
        locales = commandManager.locales
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

        private val message = MVCorei18n.CLONE_SUCCESS
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

    @Nested
    inner class WithMessagesAsReplacement {
        private val replacementKey = "{world}"
        private val replacementValue = Message.of(
            MVCorei18n.GENERIC_SUCCESS, "success")
        private val messageString = "Hello $replacementKey!"
        private val replacedMessageString = "Hello success!"
        private val replacedMessageStringLocale = "World cloned to 'Success!'!"

        private val message = MVCorei18n.CLONE_SUCCESS
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
            assertThat(message.formatted(locales), endsWith(replacedMessageStringLocale))
        }

        @Test
        fun `The formatted message with PluginLocales should have performed replacement`() {
            assertThat(message.formatted(locales), !containsSubstring(replacementKey))
            assertThat(message.formatted(locales), containsSubstring("Success!"))
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
                assertThat(sentMessage, containsSubstring(replacedMessageStringLocale))
            }
        }
    }

    @Nested
    inner class LocaleConfiguration {
        private lateinit var config: MVCoreConfig
        private lateinit var player: PlayerMock
        private lateinit var issuer: MVCommandIssuer

        @BeforeTest
        fun setUp() {
            config = assertNotNull(serviceLocator.getActiveService(MVCoreConfig::class.java))
            player = server.addPlayer("benji_0224")
            issuer = commandManager.getCommandIssuer(player)
        }

        @Test
        fun `Change default locale to chinese without per player locale should get chinese message`() {
            config.perPlayerLocale = false
            config.defaultLocale = Locale.CHINESE
            assertEquals("ab!", Message.of(
                MVCorei18n.GENERIC_SUCCESS, "").formatted(locales, issuer))
        }

        @Test
        fun `Change default locale to chinese with per player locale should get default english message`() {
            config.perPlayerLocale = true
            config.defaultLocale = Locale.CHINESE
            assertEquals("Success!", Message.of(
                MVCorei18n.GENERIC_SUCCESS, "").formatted(locales, issuer))
        }

        @Test
        fun `PerPlayerLocale enabled - Player with chinese locale should get chinese message`() {
            config.perPlayerLocale = true
            player.setLocale(Locale.CHINESE)
            assertEquals("ab!", Message.of(
                MVCorei18n.GENERIC_SUCCESS, "").formatted(locales, issuer))
        }

        @Test
        fun `PerPlayerLocale disabled - Player with chinese locale should get default english message`() {
            config.perPlayerLocale = false
            player.setLocale(Locale.CHINESE)
            assertEquals("Success!", Message.of(
                MVCorei18n.GENERIC_SUCCESS, "").formatted(locales, issuer))
        }
    }
}
