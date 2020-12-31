/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Some helper class to alternate between 2 colours.
 */
public class ColourAlternator {

    private boolean switcher;
    private final ChatColor thisColour;
    private final ChatColor thatColour;

    public ColourAlternator() {
        this(ChatColor.WHITE, ChatColor.WHITE);
    }

    public ColourAlternator(@NotNull ChatColor colorThis,
                            @NotNull ChatColor colorThat) {

        this.thisColour = colorThis;
        this.thatColour = colorThat;
    }

    /**
     * Gives you {@link ColourAlternator#thisColour} or {@link ColourAlternator#thatColour}.
     *
     * @return Opposite of the previous colour.
     */
    public ChatColor get() {
        return (switcher ^= true) ? thisColour : thatColour;
    }

    /**
     * Set back to be {@link ColourAlternator#thisColour} when {@link ColourAlternator#get()} is called.
     */
    public void reset() {
        switcher = false;
    }

    /**
     *
     * @return {@link ColourAlternator#thisColour}.
     */
    @NotNull
    public ChatColor getThis() {
        return thisColour;
    }

    /**
     *
     * @return {@link ColourAlternator#thatColour}.
     */
    @NotNull
    public ChatColor getThat() {
        return thatColour;
    }
}
