/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class MVMessaging {
    private MultiverseCore plugin;
    private Map<String, Date> sentList;
    private int cooldown = 0;

    public MVMessaging(MultiverseCore plugin) {
        this.plugin = plugin;
        this.sentList = new HashMap<String, Date>();
    }

    public void setCooldown(int milliseconds) {
        this.cooldown = milliseconds;
    }

    /**
     * Sends a message to the specified sender if the cooldown has passed.
     * @param sender The person/console to send the message to.
     * @param message The message to send.
     * @param ignoreCooldown If true this message will always be sent. Useful for things like menus
     * @return true if the message was sent, false if not.
     */
    public boolean sendMessage(CommandSender sender, String message, boolean ignoreCooldown) {
        if(!(sender instanceof Player) || ignoreCooldown) {
            sender.sendMessage(message);
            return true;
        }
        if(!this.sentList.containsKey(sender.getName())) {
            sender.sendMessage(message);
            this.sentList.put(sender.getName(), new Date());
            return true;
        } else {
            if(this.sentList.get(sender.getName()).after(new Date((new Date()).getTime() + this.cooldown))){
                sender.sendMessage(message);
                this.sentList.put(sender.getName(), new Date());
                return true;
            }
        }
        return false;
    }

    public boolean sendMessage(CommandSender sender, String message) {
        return this.sendMessage(sender, message, true);
    }
}
