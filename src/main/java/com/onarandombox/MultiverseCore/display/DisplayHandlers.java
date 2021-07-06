package com.onarandombox.MultiverseCore.display;

import com.onarandombox.MultiverseCore.display.handlers.InlineListDisplayHandler;
import com.onarandombox.MultiverseCore.display.handlers.InlineMapDisplayHandler;
import com.onarandombox.MultiverseCore.display.handlers.ListDisplayHandler;
import com.onarandombox.MultiverseCore.display.handlers.PagedListDisplayHandler;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

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
     * Supported settings: {@link DisplaySettings#SHOW_PAGE}, {@link DisplaySettings#LINES_PER_PAGE},
     * {@link DisplaySettings#PAGE_IN_CONSOLE}, {@link DisplaySettings#DO_END_PADDING}.
     */
    public static final DisplayHandler<Collection<String>> PAGE_LIST = new PagedListDisplayHandler();

    /**
     * Display a list inline.
     *
     * Supported settings: {@link DisplaySettings#SEPARATOR}.
     */
    public static final DisplayHandler<Collection<String>> INLINE_LIST = new InlineListDisplayHandler();

    /**
     * Display key value pair inline.
     *
     * Supported settings: {@link DisplaySettings#SEPARATOR}, {@link DisplaySettings#OPERATOR}.
     */
    public static final DisplayHandler<Map<String, Object>> INLINE_MAP = new InlineMapDisplayHandler();
}
