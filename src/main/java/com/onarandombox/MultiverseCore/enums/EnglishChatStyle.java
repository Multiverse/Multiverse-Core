package com.onarandombox.MultiverseCore.enums;

import org.bukkit.ChatColor;

/**
 * A regular {@link ChatColor} represented by an english string.
 * @see ChatColor
 */
public enum EnglishChatStyle {
    // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
    /** No style. */
    NORMAL(null),
    MAGIC(ChatColor.MAGIC),
    BOLD(ChatColor.BOLD),
    STRIKETHROUGH(ChatColor.STRIKETHROUGH),
    UNDERLINE(ChatColor.UNDERLINE),
    ITALIC(ChatColor.ITALIC);
    // END CHECKSTYLE-SUPPRESSION: JavadocVariable

    private final ChatColor color;

    EnglishChatStyle(ChatColor color) {
        this.color = color;
    }

    /**
     * Gets the color.
     * @return The color as {@link ChatColor}.
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     * Constructs an {@link EnglishChatStyle} from a {@link String}.
     * @param text The {@link String}.
     * @return The {@link EnglishChatStyle}.
     */
    public static EnglishChatStyle fromString(String text) {
        if (text != null) {
            for (EnglishChatStyle c : EnglishChatStyle.values()) {
                if (text.equalsIgnoreCase(c.name())) {
                    return c;
                }
            }
        }
        return null;
    }
}
