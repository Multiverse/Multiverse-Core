package com.onarandombox.MultiverseCore.api;

import java.util.Collection;

import org.bukkit.command.CommandSender;

/**
 * Multiverse-messaging.
 */
public interface MultiverseMessaging {
    /**
     * Sets the message-cooldown.
     * @param milliseconds The new message-cooldown in milliseconds.
     */
    void setCooldown(int milliseconds);

    /**
     * Sends a message to the specified sender if the cooldown has passed.
     *
     * @param sender         The person/console to send the message to.
     * @param message        The message to send.
     * @param ignoreCooldown If true this message will always be sent. Useful for things like menus
     * @return true if the message was sent, false if not.
     */
    boolean sendMessage(CommandSender sender, String message, boolean ignoreCooldown);

    /**
     * Sends a group of messages to the specified sender if the cooldown has passed.
     * This method is needed, since sending many messages in quick succession would violate
     * the cooldown.
     *
     * @param sender         The person/console to send the message to.
     * @param messages       The messages to send.
     * @param ignoreCooldown If true these messages will always be sent. Useful for things like menus
     * @return true if the message was sent, false if not.
     */
    boolean sendMessages(CommandSender sender, String[] messages, boolean ignoreCooldown);

    /**
     * Sends a group of messages to the specified sender if the cooldown has passed.
     * This method is needed, since sending many messages in quick succession would violate
     * the cooldown.
     *
     * @param sender         The person/console to send the message to.
     * @param messages       The messages to send.
     * @param ignoreCooldown If true these messages will always be sent. Useful for things like menus
     * @return true if the message was sent, false if not.
     */
    boolean sendMessages(CommandSender sender, Collection<String> messages, boolean ignoreCooldown);

    /**
     * Gets the message-cooldown.
     * @return The message-cooldown.
     */
    int getCooldown();
}
