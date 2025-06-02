package org.mvplugins.multiverse.core.utils.text;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

final class ChatColorTextFormatter implements TextFormatter {
    @Override
    public void sendFormattedMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    @Override
    public String removeColor(String message) {
        return ChatColor.stripColor(colorize(message));
    }

    @Override
    public String removeAmpColor(String message) {
        return removeColor(message);
    }

    @Override
    public String removeSectionColor(String message) {
        return ChatColor.stripColor(message);
    }

    @Override
    public String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
