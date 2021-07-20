package com.onarandombox.MultiverseCore.display.settings;

import com.onarandombox.MultiverseCore.display.DisplayHandler;
import org.bukkit.ChatColor;

/**
 * Collection of {@link DisplaySetting} that are used by various {@link DisplayHandler}.
 */
public class InlineDisplaySettings {

    /**
     * Inline separator. E.g. '1, 2, 3'
     */
    public static final DisplaySetting<String> SEPARATOR = () -> ChatColor.WHITE + ", ";
}
