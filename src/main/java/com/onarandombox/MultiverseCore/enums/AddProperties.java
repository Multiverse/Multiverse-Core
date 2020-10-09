/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.enums;

import org.bukkit.ChatColor;

/**
 * An enum containing all list-properties.
 */
public enum AddProperties {
    /**
     * Worlds that people cannot go to from a world.
     */
    worldblacklist,
    /**
     * Animal-exceptions.
     */
    animals,
    /**
     * Monster-exceptions.
     */
    monsters;

    public static String getAllPropertyNames() {
        ChatColor myColor = ChatColor.AQUA;
        StringBuilder result = new StringBuilder();
        AddProperties[] properties = AddProperties.values();
        for (AddProperties property : properties) {
            result.append(myColor).append(property.toString()).append(' ');
            myColor = (myColor == ChatColor.AQUA) ? ChatColor.GOLD : ChatColor.AQUA;
        }
        return result.toString();
    }
}
