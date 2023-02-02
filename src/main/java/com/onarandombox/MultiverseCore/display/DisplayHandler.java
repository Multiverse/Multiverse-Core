package com.onarandombox.MultiverseCore.display;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Handles the formatting and sending of all content by the {@link ContentDisplay}.
 *
 * @param <T>   Type of content to display.
 */
@FunctionalInterface
public interface DisplayHandler<T> {

    /**
     * Formats the raw content into a {@link Collection} for displaying to the given sender.
     *
     * @param sender The {@link CommandSender} who will the content will be displayed to.
     * @param display The responsible {@link ContentDisplay}.
     * @return The formatted content.
     * @throws DisplayFormatException Issue occurred while formatting content. E.g. invalid page.
     */
    Collection<String> format(@NotNull CommandSender sender, @NotNull ContentDisplay<T> display)
            throws DisplayFormatException;

    /**
     * Sends the header.
     *
     * @param sender The {@link CommandSender} who will the header will be displayed to.
     * @param display The responsible {@link ContentDisplay}.
     */
    default void sendHeader(@NotNull CommandSender sender, @NotNull ContentDisplay<T> display) {
        if (!Strings.isNullOrEmpty(display.getHeader())) {
            sender.sendMessage(display.getHeader());
        }
    }

    /**
     * Sends info such as filter and page.
     *
     * @param sender The {@link CommandSender} who will the sub header will be displayed to.
     * @param display The responsible {@link ContentDisplay}.
     */
    default void sendSubHeader(@NotNull CommandSender sender, @NotNull ContentDisplay<T> display) {
        if (display.getFilter().hasFilter()) {
            sender.sendMessage(String.format("%s[ %s ]", ChatColor.GRAY, display.getFilter().getFormattedString()));
        }
    }

    /**
     * Sends the content.
     *
     * @param sender The {@link CommandSender} who will the body will be displayed to.
     * @param display The responsible {@link ContentDisplay}.
     * @param formattedContent The content after being formatted by {@link #format(CommandSender, ContentDisplay)}
     */
    default void sendBody(@NotNull CommandSender sender, @NotNull ContentDisplay<T> display,
                          Collection<String> formattedContent) {
        if (formattedContent == null || formattedContent.size() == 0) {
            sender.sendMessage(display.getEmptyMessage());
            return;
        }
        sender.sendMessage(formattedContent.toArray(new String[0]));
    }
}
