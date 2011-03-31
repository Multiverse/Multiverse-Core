package com.onarandombox.utils;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class Messaging {

    public void send(CommandSender player, String msg, String... params) {
        player.sendMessage(parameterizeMessage(msg, params));
    }

    public void broadcast(MultiverseCore plugin, String msg, String... params) {
        plugin.getServer().broadcastMessage(parameterizeMessage(msg, params));
    }

    private String parameterizeMessage(String msg, String... params) {
        msg = "§cHeroes: " + msg;
        for (int i = 0; i < params.length; i++) {
            msg = msg.replace("$" + (i + 1), "§f" + params[i] + "§c");
        }
        return msg;
    }

}
