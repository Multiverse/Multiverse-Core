package com.onarandombox.MultiverseCore.display;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Various implementations of {@link DisplayHandler}.
 */
public class DisplayHandlers {

    /**
     * Standard list display.
     *
     * Supported settings: none.
     */
    public static final DisplayHandler<Collection<String>> LIST = display -> display.getContents().stream()
            .filter(display.getFilter()::checkMatch)
            .map(s -> (ContentDisplay.LINE_BREAK.equals(s)) ? "" : display.getColorTool().get() + s)
            .collect(Collectors.toList());

    /**
     * List display with paging.
     *
     * Supported settings: {@link DisplaySettings#SHOW_PAGE}, {@link DisplaySettings#LINES_PER_PAGE},
     * {@link DisplaySettings#PAGE_IN_CONSOLE}, {@link DisplaySettings#DO_END_PADDING}.
     */
    public static final DisplayHandler<Collection<String>> PAGE_LIST = new DisplayHandler<Collection<String>>() {
        @Override
        public Collection<String> format(@NotNull ContentDisplay<Collection<String>> display) throws DisplayFormatException {
            if (dontNeedPaging(display)) {
                return LIST.format(display);
            }

            int pages = 1;
            int currentLength = 0;
            int targetPage = display.getSetting(DisplaySettings.SHOW_PAGE);
            int linesPerPage = display.getSetting(DisplaySettings.LINES_PER_PAGE);
            List<String> content = new ArrayList<>(linesPerPage);

            // Calculate the paging.
            for (String line : display.getContents()) {
                if (!display.getFilter().checkMatch(line)) {
                    continue;
                }
                // When it's the next page.
                boolean isLineBreak = ContentDisplay.LINE_BREAK.equals(line);
                if (isLineBreak || ++currentLength > linesPerPage) {
                    pages++;
                    currentLength = 0;
                    if (isLineBreak) {
                        continue;
                    }
                }
                if (pages == targetPage) {
                    // Let first line be the header when no header is defined.
                    if (display.getHeader() == null) {
                        display.setHeader(line);
                        currentLength--;
                        continue;
                    }
                    content.add(display.getColorTool().get() + line);
                }
            }

            // Page out of range.
            if (targetPage < 1 || targetPage > pages) {
                if (pages == 1) {
                    throw new DisplayFormatException("There is only 1 page!");
                }
                throw new DisplayFormatException("Please enter a page from 1 to " + pages + ".");
            }

            // No content
            if (content.size() == 0) {
                content.add(display.getEmptyMessage());
            }

            // Add empty lines to make output length consistent.
            if (display.getSetting(DisplaySettings.DO_END_PADDING)) {
                IntStream.range(0, linesPerPage - content.size()).forEach(i -> content.add(""));
            }
            display.setSetting(DisplaySettings.TOTAL_PAGE, pages);

            return content;
        }

        @Override
        public void sendSubHeader(@NotNull ContentDisplay<Collection<String>> display) {
            if (dontNeedPaging(display)) {
                LIST.sendSubHeader(display);
                return;
            }

            if (display.getFilter().hasFilter()) {
                display.getSender().sendMessage(String.format("%s[ Page %s of %s, %s ]",
                        ChatColor.GRAY,
                        display.getSetting(DisplaySettings.SHOW_PAGE),
                        display.getSetting(DisplaySettings.TOTAL_PAGE),
                        display.getFilter().getFormattedString())
                );
                return;
            }
            display.getSender().sendMessage(String.format("%s[ Page %s of %s ]",
                    ChatColor.GRAY,
                    display.getSetting(DisplaySettings.SHOW_PAGE),
                    display.getSetting(DisplaySettings.TOTAL_PAGE))
            );
        }

        private boolean dontNeedPaging(ContentDisplay<Collection<String>> display) {
            return display.getSender() instanceof ConsoleCommandSender
                    && !display.getSetting(DisplaySettings.PAGE_IN_CONSOLE);
        }
    };

    /**
     * Display a list inline.
     *
     * Supported settings: {@link DisplaySettings#SEPARATOR}.
     */
    public static final DisplayHandler<Collection<String>> INLINE_LIST = display -> {
        StringBuilder builder = new StringBuilder();
        String separator = display.getSetting(DisplaySettings.SEPARATOR);

        for (Iterator<String> iterator = display.getContents().iterator(); iterator.hasNext(); ) {
            String content = iterator.next();
            if (!display.getFilter().checkMatch(content)) {
                continue;
            }
            builder.append(display.getColorTool().get()).append(content);
            if (iterator.hasNext()) {
                builder.append(separator);
            }
        }
        return (builder.length() == 0)
                ? Collections.singletonList(display.getEmptyMessage())
                : Collections.singleton(builder.toString());
    };

    /**
     * Display key value pair inline.
     *
     * Supported settings: {@link DisplaySettings#SEPARATOR}, {@link DisplaySettings#OPERATOR}.
     */
    public static final DisplayHandler<Map<String, Object>> INLINE_MAP = display -> {
        StringBuilder builder = new StringBuilder();
        String separator = display.getSetting(DisplaySettings.SEPARATOR);
        String operator = display.getSetting(DisplaySettings.OPERATOR);

        for (Iterator<Entry<String, Object>> iterator = display.getContents().entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, Object> entry = iterator.next();
            if (!display.getFilter().checkMatch(entry.getKey()) && !display.getFilter().checkMatch(entry.getValue())) {
                continue;
            }
            builder.append(display.getColorTool().get())
                    .append(entry.getKey())
                    .append(operator)
                    .append(display.getColorTool().get())
                    .append(entry.getValue());
            if (iterator.hasNext()) {
                builder.append(separator);
            }
        }
        return (builder.length() == 0)
                ? Collections.singletonList(display.getEmptyMessage())
                : Collections.singleton(builder.toString());
    };
}
