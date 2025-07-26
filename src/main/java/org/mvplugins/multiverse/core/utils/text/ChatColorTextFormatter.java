package org.mvplugins.multiverse.core.utils.text;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
final class ChatColorTextFormatter implements TextFormatter {
    @Override
    public void sendFormattedMessage(@NotNull CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    @Override
    public @Nullable String removeColor(@Nullable String message) {
        return ChatColor.stripColor(colorize(message));
    }

    @Override
    public @Nullable String removeAmpColor(@Nullable String message) {
        return removeColor(message);
    }

    @Override
    public @Nullable String removeSectionColor(@Nullable String message) {
        return ChatColor.stripColor(message);
    }

    @Override
    public @Nullable String colorize(@Nullable String message) {
        if (message == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
