package com.onarandombox.MultiverseCore.displaytools;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class ColorAlternator implements ColorTool {

    public static ColorAlternator with(@NotNull ChatColor colorThis,
                                       @NotNull ChatColor colorThat) {

        return new ColorAlternator(colorThis, colorThat);
    }

    private boolean switcher;
    private final ChatColor thisColor;
    private final ChatColor thatColor;

    public ColorAlternator() {
        this(ChatColor.WHITE, ChatColor.WHITE);
    }

    public ColorAlternator(@NotNull ChatColor colorThis,
                           @NotNull ChatColor colorThat) {

        this.thisColor = colorThis;
        this.thatColor = colorThat;
    }

    @Override
    public ChatColor get() {
        return (this.switcher ^= true) ? this.thisColor : this.thatColor;
    }

    public ChatColor getThisColor() {
        return thisColor;
    }

    public ChatColor getThatColor() {
        return thatColor;
    }
}
