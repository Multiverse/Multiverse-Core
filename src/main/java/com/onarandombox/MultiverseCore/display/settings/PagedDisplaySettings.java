package com.onarandombox.MultiverseCore.display.settings;

public class PagedDisplaySettings {

    /**
     * Page to display.
     */
    public static final DisplaySetting<Integer> SHOW_PAGE = () -> 1;

    /**
     * Total pages available to display.
     */
    public static final DisplaySetting<Integer> TOTAL_PAGE = () -> 1;

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

}
