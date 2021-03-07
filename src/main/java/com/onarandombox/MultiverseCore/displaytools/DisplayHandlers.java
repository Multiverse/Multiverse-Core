package com.onarandombox.MultiverseCore.displaytools;

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

    public static final DisplayHandler<Collection<String>> LIST = display -> display.getContents().stream()
            .filter(display.getFilter()::checkMatch)
            .collect(Collectors.toList());

    public static final DisplayHandler<Collection<String>> PAGE_LIST = display -> {
        if (display.getSender() instanceof ConsoleCommandSender && !display.getSetting(DisplaySettings.PAGE_IN_CONSOLE)) {
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
            if (++currentLength > linesPerPage) {
                pages++;
                currentLength = 0;
            }
        }

        if (display.getSetting(DisplaySettings.DO_END_PADDING)) {
            IntStream.range(0, linesPerPage - content.size()).forEach(i -> content.add(""));
        }

        return content;
    };
}
