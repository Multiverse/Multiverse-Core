/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MultiverseMessaging;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The default-implementation of {@link MultiverseMessaging}.
 */
public class MVMessaging implements MultiverseMessaging {
    private Map<String, Long> sentList;
    private int cooldown;

    public MVMessaging() {
        this.sentList = new HashMap<String, Long>();
        this.cooldown = 5000; // SUPPRESS CHECKSTYLE: MagicNumberCheck
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCooldown(int milliseconds) {
        this.cooldown = milliseconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendMessage(CommandSender sender, String message, boolean ignoreCooldown) {
        return this.sendMessages(sender, new String[]{ message }, ignoreCooldown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendMessages(CommandSender sender, String[] messages, boolean ignoreCooldown) {
        if (!(sender instanceof Player) || ignoreCooldown) {

            sendMessages(sender, messages);
            return true;
        }
        if (!this.sentList.containsKey(sender.getName())) {
            sendMessages(sender, messages);
            this.sentList.put(sender.getName(), System.currentTimeMillis());
            return true;
        } else {
            long time = System.currentTimeMillis();
            if (time >= this.sentList.get(sender.getName()) + this.cooldown) {
                sendMessages(sender, messages);
                this.sentList.put(sender.getName(), System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendMessages(CommandSender sender, Collection<String> messages, boolean ignoreCooldown) {
        return this.sendMessages(sender, messages.toArray(new String[0]), ignoreCooldown);
    }

    private static void sendMessages(CommandSender sender, String[] messages) {
        for (String s : messages) {
            sender.sendMessage(s);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCooldown() {
        return cooldown;
    }
}
