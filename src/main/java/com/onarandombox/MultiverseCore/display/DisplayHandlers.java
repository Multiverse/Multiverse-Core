package com.onarandombox.MultiverseCore.display;

import com.onarandombox.MultiverseCore.display.handlers.InlineListDisplayHandler;
import com.onarandombox.MultiverseCore.display.handlers.InlineMapDisplayHandler;
import com.onarandombox.MultiverseCore.display.handlers.ListDisplayHandler;
import com.onarandombox.MultiverseCore.display.handlers.PagedListDisplayHandler;
import com.onarandombox.MultiverseCore.display.settings.InlineDisplaySettings;
import com.onarandombox.MultiverseCore.display.settings.PagedDisplaySettings;
import com.onarandombox.MultiverseCore.display.settings.MapDisplaySettings;

import java.util.Collection;
import java.util.Map;

/**
 * Various implementations of {@link DisplayHandler}.
 */
public class DisplayHandlers {

    /**
     * Standard list display.
     *
     * Supported settings: none.
     */
    public static final DisplayHandler<Collection<String>> LIST = new ListDisplayHandler();

    /**
     * List display with paging.
     *
     * Supported settings: {@link PagedDisplaySettings#SHOW_PAGE}, {@link PagedDisplaySettings#LINES_PER_PAGE},
     * {@link PagedDisplaySettings#PAGE_IN_CONSOLE}, {@link PagedDisplaySettings#DO_END_PADDING}.
     */
    public static final DisplayHandler<Collection<String>> PAGE_LIST = new PagedListDisplayHandler();

    /**
     * Display a list inline.
     *
     * Supported settings: {@link InlineDisplaySettings#SEPARATOR}.
     */
    public static final DisplayHandler<Collection<String>> INLINE_LIST = new InlineListDisplayHandler();

    /**
     * Display key value pair inline.
     *
     * Supported settings: {@link InlineDisplaySettings#SEPARATOR}, {@link MapDisplaySettings#OPERATOR}.
     */
    public static final DisplayHandler<Map<String, Object>> INLINE_MAP = new InlineMapDisplayHandler();
}
