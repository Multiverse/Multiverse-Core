/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility-class for messaging.
 */
public class MVMessaging {
    private Map<String, Long> sentList;
    private int cooldown;

    public MVMessaging() {
        this.sentList = new HashMap<String, Long>();
        this.cooldown = 5000; // SUPPRESS CHECKSTYLE: MagicNumberCheck
    }

    /**
     * Sets the message-cooldown.
     * @param milliseconds The new message-cooldown in milliseconds.
     */
    public void setCooldown(int milliseconds) {
        this.cooldown = milliseconds;
    }

    /**
     * Sends a message to the specified sender if the cooldown has passed.
     *
     * @param sender         The person/console to send the message to.
     * @param message        The message to send.
     * @param ignoreCooldown If true this message will always be sent. Useful for things like menus
     * @return true if the message was sent, false if not.
     */
    public boolean sendMessage(CommandSender sender, String message, boolean ignoreCooldown) {
        return this.sendMessages(sender, new String[]{ message }, ignoreCooldown);
    }

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
    public boolean sendMessages(CommandSender sender, String[] messages, boolean ignoreCooldown) {
        if (!(sender instanceof Player) || ignoreCooldown) {

            this.sendMessages(sender, messages);
            return true;
        }
        if (!this.sentList.containsKey(sender.getName())) {
            this.sendMessages(sender, messages);
            this.sentList.put(sender.getName(), System.currentTimeMillis());
            return true;
        } else {
            long time = System.currentTimeMillis();
            if (time >= this.sentList.get(sender.getName()) + this.cooldown) {
                this.sendMessages(sender, messages);
                this.sentList.put(sender.getName(), System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

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
    public boolean sendMessages(CommandSender sender, Collection<String> messages, boolean ignoreCooldown) {
        return this.sendMessages(sender, messages.toArray(new String[0]), ignoreCooldown);
    }

    private void sendMessages(CommandSender sender, String[] messages) {
        for (String s : messages) {
            sender.sendMessage(s);
        }
    }

    /**
     * Gets the message-cooldown.
     * @return The message-cooldown.
     */
    public int getCooldown() {
        return cooldown;
    }
}
