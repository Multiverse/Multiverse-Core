package com.onarandombox.MultiverseCore.displaytools;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Handles the formatting and sending of all content by the {@link ContentDisplay}.
 *
 * @param <T>   Type of content to display.
 */
public interface DisplayHandler<T> {

    /**
     * Formats the raw content into a {@link Collection<String>} for displaying to sender.
     *
     * @param display   The responsible {@link ContentDisplay}.
     * @return The formatted content.
     * @throws DisplayFormatException Issue occurred while formatting content. E.g. invalid page.
     */
    Collection<String> format(@NotNull ContentDisplay<T> display) throws DisplayFormatException;

    /**
     * Sends the header.
     *
     * @param display   The responsible {@link ContentDisplay}.
     */
    default void sendHeader(@NotNull ContentDisplay<T> display) {
        if (!Strings.isNullOrEmpty(display.getHeader())) {
            display.getSender().sendMessage(display.getHeader());
        }
    }

    /**
     * Sends info such as filter and page.
     *
     * @param display   The responsible {@link ContentDisplay}.
     */
    default void sendSubHeader(@NotNull ContentDisplay<T> display) {
        if (display.getFilter().hasFilter()) {
            display.getSender().sendMessage(String.format("%s[ %s ]",
                    ChatColor.GRAY, display.getFilter().getFormattedString()));
        }
    }

    /**
     * Sends the content.
     *
     * @param display           The responsible {@link ContentDisplay}.
     * @param formattedContent  The content after being formatted by {@link #format(ContentDisplay)}
     */
    default void sendBody(@NotNull ContentDisplay<T> display, Collection<String> formattedContent) {
        if (formattedContent == null || formattedContent.size() == 0) {
            display.getSender().sendMessage(display.getEmptyMessage());
            return;
        }
        display.getSender().sendMessage(formattedContent.toArray(new String[0]));
    }
}
