/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.utils;

import org.bukkit.ChatColor;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public enum EnglishChatColor {
    AQUA("AQUA", ChatColor.AQUA),
    BLACK("BLACK", ChatColor.BLACK),
    BLUE("BLUE", ChatColor.BLUE),
    DARKAQUA("DARKAQUA", ChatColor.DARK_AQUA),
    DARKBLUE("DARKBLUE", ChatColor.DARK_BLUE),
    DARKGRAY("DARKGRAY", ChatColor.DARK_GRAY),
    DARKGREEN("DARKGREEN", ChatColor.DARK_GREEN),
    DARKPURPLE("DARKPURPLE", ChatColor.DARK_PURPLE),
    DARKRED("DARKRED", ChatColor.DARK_RED),
    GOLD("GOLD", ChatColor.GOLD),
    GRAY("GRAY", ChatColor.GRAY),
    GREEN("GREEN", ChatColor.GREEN),
    LIGHTPURPLE("LIGHTPURPLE", ChatColor.LIGHT_PURPLE),
    RED("RED", ChatColor.RED),
    YELLOW("YELLOW", ChatColor.YELLOW),
    WHITE("WHITE", ChatColor.WHITE);
    private ChatColor color;
    private String text;

    EnglishChatColor(String name, ChatColor color) {
        this.color = color;
        this.text = name;
    }

    public String getText() {
        return this.text;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public static EnglishChatColor fromString(String text) {
        if (text != null) {
            for (EnglishChatColor c : EnglishChatColor.values()) {
                if (text.equalsIgnoreCase(c.text)) {
                    return c;
                }
            }
        }
        return EnglishChatColor.WHITE;
    }
}
