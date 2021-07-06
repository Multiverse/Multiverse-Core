package com.onarandombox.MultiverseCore.display.handlers;

import com.onarandombox.MultiverseCore.display.ContentDisplay;
import com.onarandombox.MultiverseCore.display.DisplayFormatException;
import com.onarandombox.MultiverseCore.display.DisplaySettings;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class PagedListDisplayHandler extends ListDisplayHandler {

    @Override
    public Collection<String> format(@NotNull ContentDisplay<Collection<String>> display) throws DisplayFormatException {
        if (dontNeedPaging(display)) {
            return super.format(display);
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
            super.sendSubHeader(display);
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
}
