package org.mvplugins.multiverse.core.utils.text;

import org.bukkit.command.CommandSender;

interface TextFormatter {
    void sendFormattedMessage(CommandSender sender, String message);

    String removeColor(String message);

    String removeAmpColor(String message);

    String removeSectionColor(String message);

    String colorize(String message);
}
