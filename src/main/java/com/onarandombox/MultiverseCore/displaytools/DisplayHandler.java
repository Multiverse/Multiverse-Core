package com.onarandombox.MultiverseCore.displaytools;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;

import java.util.Collection;

public interface DisplayHandler<T> {

    Collection<String> format(ContentDisplay<T> display);

    default void sendHeader(ContentDisplay<T> display) {
        if (!Strings.isNullOrEmpty(display.getHeader())) {
            display.getSender().sendMessage(display.getHeader());
        }
    }

    default void sendSubHeader(ContentDisplay<T> display) {
        if (display.getFilter().hasFilter()) {
            display.getSender().sendMessage(String.format("%s[ %s ]",
                    ChatColor.GRAY, display.getFilter().getFormattedString()));
        }
    }

    default void sendBody(ContentDisplay<T> display, Collection<String> formattedContent) {
        if (formattedContent == null || formattedContent.size() == 0) {
            display.getSender().sendMessage(display.getEmptyMessage());
            return;
        }
        display.getSender().sendMessage(formattedContent.toArray(new String[0]));
    }
}
