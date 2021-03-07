package com.onarandombox.MultiverseCore.displaytools;

import org.bukkit.ChatColor;

/**
 * Tools to allow customisation.
 */
public interface ColorTool {

    /**
     * Gets a chat color.
     *
     * @return The color.
     */
    ChatColor get();

    /**
     * Default implementation of this interface. Returns a default white color.
     */
    ColorTool DEFAULT = () -> ChatColor.WHITE;
}
