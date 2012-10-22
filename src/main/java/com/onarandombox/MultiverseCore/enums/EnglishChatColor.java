/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.enums;

import org.bukkit.ChatColor;

/**
 * A regular {@link ChatColor} represented by an english string.
 * @see ChatColor
 */
public enum EnglishChatColor {
    // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
    AQUA(ChatColor.AQUA),
    BLACK(ChatColor.BLACK),
    BLUE(ChatColor.BLUE),
    DARKAQUA(ChatColor.DARK_AQUA),
    DARKBLUE(ChatColor.DARK_BLUE),
    DARKGRAY(ChatColor.DARK_GRAY),
    DARKGREEN(ChatColor.DARK_GREEN),
    DARKPURPLE(ChatColor.DARK_PURPLE),
    DARKRED(ChatColor.DARK_RED),
    GOLD(ChatColor.GOLD),
    GRAY(ChatColor.GRAY),
    GREEN(ChatColor.GREEN),
    LIGHTPURPLE(ChatColor.LIGHT_PURPLE),
    RED(ChatColor.RED),
    YELLOW(ChatColor.YELLOW),
    WHITE(ChatColor.WHITE);
    // END CHECKSTYLE-SUPPRESSION: JavadocVariable

    private final ChatColor color;
    //private final String text;

    EnglishChatColor(ChatColor color) {
        this.color = color;
    }

    /**
     * Gets the text.
     * @return The text.
     */
    public String getText() {
        return this.name();
    }

    /**
     * Gets the color.
     * @return The color as {@link ChatColor}.
     */
    public ChatColor getColor() {
        return this.color;
    }

    /**
     * Constructs a string containing all available colors.
     * @return That {@link String}.
     */
    public static String getAllColors() {
        String buffer = "";
        for (EnglishChatColor c : EnglishChatColor.values()) {
            buffer += c.getColor() + c.getText() + " ";
        }
        return buffer;
    }

    /**
     * Constructs an {@link EnglishChatColor} from a {@link String}.
     * @param text The {@link String}.
     * @return The {@link EnglishChatColor}.
     */
    public static EnglishChatColor fromString(String text) {
        if (text != null) {
            for (EnglishChatColor c : EnglishChatColor.values()) {
                if (text.equalsIgnoreCase(c.name())) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Looks if the given-color name is a valid color.
     * @param aliasColor A color-name.
     * @return True if the name is a valid color, false if it isn't.
     */
    public static boolean isValidAliasColor(String aliasColor) {
        return (EnglishChatColor.fromString(aliasColor) != null);
    }
}
