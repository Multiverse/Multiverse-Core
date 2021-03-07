package com.onarandombox.MultiverseCore.displaytools;

import org.bukkit.ChatColor;

public class DisplaySettings {

    /**
     * The page to display.
     */
    public static final DisplaySetting<Integer> SHOW_PAGE = () -> 1;

    /**
     * The max number of lines per page. This excludes header.
     */
    public static final DisplaySetting<Integer> LINES_PER_PAGE = () -> 8;

    /**
     * Should add empty lines if content lines shown is less that {@link #LINES_PER_PAGE}.
     */
    public static final DisplaySetting<Boolean> DO_END_PADDING = () -> true;

    /**
     * Should display with paging when it's sent to console.
     */
    public static final DisplaySetting<Boolean> PAGE_IN_CONSOLE = () -> false;

    /**
     * Inline separator.
     */
    public static final DisplaySetting<String> SEPARATOR = () -> ChatColor.WHITE + ", ";

    /**
     * The thing between a key value pair. E.g. 'Me = Smart'
     */
    public static final DisplaySetting<String> OPERATOR = () -> ChatColor.WHITE + " = ";
}
