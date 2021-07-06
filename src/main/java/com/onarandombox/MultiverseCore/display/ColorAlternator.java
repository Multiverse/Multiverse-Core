package com.onarandombox.MultiverseCore.display;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Helper class to switch between 2 {@link ChatColor}.
 */
public class ColorAlternator implements ColorTool {

    /**
     * Creates a new {@link ColorAlternator} with 2 {@link ChatColor}s.
     *
     * @param colorThis The first color.
     * @param colorThat The second color.
     * @return The {@link ColorAlternator} created for you.
     */
    public static ColorAlternator with(@NotNull ChatColor colorThis,
                                       @NotNull ChatColor colorThat) {

        return new ColorAlternator(colorThis, colorThat);
    }

    private boolean switcher;
    private final ChatColor thisColor;
    private final ChatColor thatColor;

    /**
     * @param colorThis The first color.
     * @param colorThat The second color.
     */
    public ColorAlternator(@NotNull ChatColor colorThis,
                           @NotNull ChatColor colorThat) {

        this.thisColor = colorThis;
        this.thatColor = colorThat;
    }

    /**
     * Gets the color. Everytime this method is called, it swaps the color that it returns.
     *
     * @return The color.
     */
    @Override
    public ChatColor get() {
        return (this.switcher ^= true) ? this.thisColor : this.thatColor;
    }

    /**
     * @return The first color.
     */
    public ChatColor getThisColor() {
        return thisColor;
    }

    /**
     * @return The second color.
     */
    public ChatColor getThatColor() {
        return thatColor;
    }
}
