/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.utils;

import org.bukkit.ChatColor;

public class FancyColorScheme {
    private ChatColor headerColor;
    private ChatColor mainColor;
    private ChatColor altColor;
    private ChatColor defContentColor;

    public FancyColorScheme(ChatColor header, ChatColor main, ChatColor alt, ChatColor defaultColor) {
        this.headerColor = header;
        this.mainColor = main;
        this.altColor = alt;
        this.defContentColor = defaultColor;
    }

    public ChatColor getHeader() {
        return this.headerColor;
    }

    public ChatColor getMain() {
        return this.mainColor;
    }

    public ChatColor getAlt() {
        return this.altColor;
    }

    public ChatColor getDefault() {
        return this.defContentColor;
    }

    public ChatColor getMain(boolean main) {
        return main ? this.getMain() : this.getAlt();
    }
}
