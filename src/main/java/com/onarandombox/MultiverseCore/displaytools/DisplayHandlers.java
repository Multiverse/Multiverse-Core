package com.onarandombox.MultiverseCore.displaytools;

import co.aikar.commands.InvalidCommandArgument;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DisplayHandlers {

    /**
     * Standard list display.
     */
    public static final DisplayHandler<Collection<String>> LIST = display -> display.getContents().stream()
            .filter(display.getFilter()::checkMatch)
            .map(s -> display.getColorTool().get() + s)
            .collect(Collectors.toList());

    /**
     * List display with paging.
     */
    public static final DisplayHandler<Collection<String>> PAGE_LIST = new DisplayHandler<Collection<String>>() {
        @Override
        public Collection<String> format(ContentDisplay<Collection<String>> display) {
            if (dontNeedPaging(display)) {
                return LIST.format(display);
            }

            int pages = 1;
            int currentLength = 0;
            int targetPage = display.getSetting(DisplaySettings.SHOW_PAGE);
            int linesPerPage = display.getSetting(DisplaySettings.LINES_PER_PAGE);
            List<String> content = new ArrayList<>(linesPerPage);

            for (String line : display.getContents()) {
                if (!display.getFilter().checkMatch(line)) {
                    continue;
                }
                if (pages == targetPage) {
                    content.add(display.getColorTool().get() + line);
                }
                if (++currentLength >= linesPerPage) {
                    pages++;
                    currentLength = 0;
                }
            }

            if (targetPage < 1 || targetPage > pages) {
                if (pages == 1) {
                    throw new InvalidCommandArgument("There is only 1 page!");
                }
                throw new InvalidCommandArgument("Please enter a page from 1 to " + pages + ".");
            }

            if (display.getSetting(DisplaySettings.DO_END_PADDING)) {
                IntStream.range(0, linesPerPage - content.size()).forEach(i -> content.add(""));
            }
            display.setSetting(DisplaySettings.TOTAL_PAGE, pages);

            return content;
        }

        @Override
        public void sendSubHeader(ContentDisplay<Collection<String>> display) {
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
     * Display list inline.
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
