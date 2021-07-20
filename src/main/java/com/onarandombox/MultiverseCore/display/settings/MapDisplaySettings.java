package com.onarandombox.MultiverseCore.display.settings;

import com.onarandombox.MultiverseCore.display.DisplayHandler;
import org.bukkit.ChatColor;

/**
 * Collection of {@link DisplaySetting} that are used by various {@link DisplayHandler}.
 */
public class MapDisplaySettings {

    /**
     * The thing between a key value pair. E.g. 'Me = Smart'
     */
    public static final DisplaySetting<String> OPERATOR = () -> ChatColor.WHITE + " = ";
}
