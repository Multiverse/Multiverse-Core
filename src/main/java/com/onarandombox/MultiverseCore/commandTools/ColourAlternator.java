/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import org.bukkit.ChatColor;

public class ColourAlternator {

    private boolean switcher;
    private final ChatColor colorThis;
    private final ChatColor colorThat;

    public ColourAlternator(ChatColor colorThis, ChatColor colorThat) {
        this.colorThis = colorThis;
        this.colorThat = colorThat;
    }

    public ChatColor get() {
        return (switcher ^= true) ? colorThis : colorThat;
    }

    public void reset() {
        switcher = false;
    }

    public ChatColor getColorThis() {
        return colorThis;
    }

    public ChatColor getColorThat() {
        return colorThat;
    }
}
