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
 */
public enum EnglishChatColor {
    /*
     * I know. this is quite ugly.
     */
    /** AQUA. */
    AQUA("AQUA", ChatColor.AQUA),
    /** BLACK. */
    BLACK("BLACK", ChatColor.BLACK),
    /** BLUE. */
    BLUE("BLUE", ChatColor.BLUE),
    /** DARKAQUA. */
    DARKAQUA("DARKAQUA", ChatColor.DARK_AQUA),
    /** DARKBLUE. */
    DARKBLUE("DARKBLUE", ChatColor.DARK_BLUE),
    /** DARKGRAY. */
    DARKGRAY("DARKGRAY", ChatColor.DARK_GRAY),
    /** DARKGREEN. */
    DARKGREEN("DARKGREEN", ChatColor.DARK_GREEN),
    /** DARKPURPLE. */
    DARKPURPLE("DARKPURPLE", ChatColor.DARK_PURPLE),
    /** DARKRED. */
    DARKRED("DARKRED", ChatColor.DARK_RED),
    /** GOLD. */
    GOLD("GOLD", ChatColor.GOLD),
    /** GRAY. */
    GRAY("GRAY", ChatColor.GRAY),
    /** GREEN. */
    GREEN("GREEN", ChatColor.GREEN),
    /** LIGHTPURPLE. */
    LIGHTPURPLE("LIGHTPURPLE", ChatColor.LIGHT_PURPLE),
    /** RED. */
    RED("RED", ChatColor.RED),
    /** YELLOW. */
    YELLOW("YELLOW", ChatColor.YELLOW),
    /** WHITE. */
    WHITE("WHITE", ChatColor.WHITE);
    private ChatColor color;
    private String text;

    EnglishChatColor(String name, ChatColor color) {
        this.color = color;
        this.text = name;
    }

    /**
     * Gets the text.
     * @return The text.
     */
    public String getText() {
        return this.text;
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
                if (text.equalsIgnoreCase(c.text)) {
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
