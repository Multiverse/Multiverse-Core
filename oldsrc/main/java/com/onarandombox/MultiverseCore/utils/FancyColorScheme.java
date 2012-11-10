/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import org.bukkit.ChatColor;

/**
 * A color-scheme.
 */
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

    /**
     * Gets the header's {@link ChatColor}.
     * @return The header's {@link ChatColor}.
     */
    public ChatColor getHeader() {
        return this.headerColor;
    }

    /**
     * Gets the main {@link ChatColor}.
     * @return The main {@link ChatColor}.
     */
    public ChatColor getMain() {
        return this.mainColor;
    }

    /**
     * Gets the alt {@link ChatColor}.
     * @return The alt {@link ChatColor}.
     */
    public ChatColor getAlt() {
        return this.altColor;
    }

    /**
     * Gets the default {@link ChatColor}.
     * @return The default {@link ChatColor}.
     */
    public ChatColor getDefault() {
        return this.defContentColor;
    }

    /**
     * Gets either the main or the alt {@link ChatColor}.
     * @param main True if the main-color is desired, false to get the alt color.
     * @return The desired {@link ChatColor}.
     */
    public ChatColor getMain(boolean main) {
        return main ? this.getMain() : this.getAlt();
    }
}
