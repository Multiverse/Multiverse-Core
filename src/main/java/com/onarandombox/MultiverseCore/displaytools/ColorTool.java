package com.onarandombox.MultiverseCore.displaytools;

import org.bukkit.ChatColor;

public interface ColorTool {

    ChatColor get();

    ColorTool DEFAULT = () -> ChatColor.WHITE;
}
