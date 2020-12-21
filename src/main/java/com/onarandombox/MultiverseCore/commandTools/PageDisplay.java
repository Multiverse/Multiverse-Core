/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class PageDisplay {

    private final CommandSender sender;
    private final String header;
    private final List<String> contents;
    private final int pageToShow;
    private int contentLinesPerPage; // excludes header
    private final String filterString;
    private final Pattern filterPattern;
    private final ColourAlternator colours;

    private static final int DEFAULT_LINES_PER_PAGE = 8;
    private static final String PAGE_PLACEHOLDER = "%page%";
    private static final String LINE_BREAK_PLACEHOLDER = "%lf%";
    private static final Pattern REGEX_SPECIAL_CHARS = Pattern.compile("[.+*?\\[^\\]$(){}=!<>|:-\\\\]");

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int pageToShow) {

        this(sender, header, contents, pageToShow, DEFAULT_LINES_PER_PAGE, null, null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int pageToShow,
                       int contentLinesPerPage) {

        this(sender, header, contents, pageToShow, contentLinesPerPage, null, null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int pageToShow,
                       int contentLinesPerPage,
                       @NotNull ColourAlternator colours) {

        this(sender, header, contents, pageToShow, contentLinesPerPage, null, colours);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       @NotNull PageFilter pageFilter) {

        this(sender, header, contents, pageFilter.getPage(), DEFAULT_LINES_PER_PAGE, pageFilter.getFilter(), null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       @NotNull PageFilter pageFilter,
                       @Nullable ColourAlternator colours) {

        this(sender, header, contents, pageFilter.getPage(), DEFAULT_LINES_PER_PAGE, pageFilter.getFilter(), colours);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int pageToShow,
                       int contentLinesPerPage,
                       @Nullable String filterString,
                       @Nullable ColourAlternator colours) {

        this.sender = sender;
        this.header = header;
        this.contents = contents;
        this.pageToShow = pageToShow;
        this.contentLinesPerPage = contentLinesPerPage;
        this.filterString = filterString;
        this.filterPattern = parseFilter(filterString);
        this.colours = colours;
    }

    private Pattern parseFilter(@Nullable String filter) {
        if (filter == null) {
            return null;
        }
        String cleanedFilter = REGEX_SPECIAL_CHARS.matcher(filter.toLowerCase()).replaceAll("\\\\$0");
        return Pattern.compile("(?i).*" + cleanedFilter + ".*");
    }

    public boolean matchFilter(@Nullable String value) {
        return value != null && (filterPattern == null || value.equals(LINE_BREAK_PLACEHOLDER) || filterPattern.matcher(value).matches());
    }

    public void showPage() {
        getShowPageRunnable().run();
    }

    public void showPageAsync(@NotNull Plugin plugin) {
        getShowPageRunnable().runTaskAsynchronously(plugin);
    }

    @NotNull
    public BukkitRunnable getShowPageRunnable() {
        return (sender instanceof Player) ? new ShowPageRunnable() : new ShowAllRunnable();
    }

    private abstract class ShowRunnable extends BukkitRunnable {

        protected final List<Integer> contentToShowIndex;

        public ShowRunnable() {
            this.contentToShowIndex = new ArrayList<>();
            if (colours != null) {
                colours.reset();
            }
        }

        @Override
        public void run() {
            calculateContent();
            display();
        }

        public abstract void display();

        public abstract void calculateContent();

        public abstract void showHeader();

        public void showContent() {
            contentToShowIndex.stream()
                    .map(contents::get)
                    .map(line -> line.equals(LINE_BREAK_PLACEHOLDER)
                            ? " "
                            : line.replace(PAGE_PLACEHOLDER, ""))
                    .map(line -> ((colours == null) ? "" : colours.get()) + line)
                    .forEach(sender::sendMessage);
        }

        public String parseFilter() {
             return String.format("%sFilter: '%s'%s", ChatColor.ITALIC, filterString, ChatColor.RESET);
        }
    }

    private class ShowAllRunnable extends ShowRunnable {

        public ShowAllRunnable() {
            super();
        }

        @Override
        public void display() {
            showHeader();
            showContent();
        }

        @Override
        public void calculateContent() {
            for (int index = 0, contentsSize = contents.size(); index < contentsSize; index++) {
                if (PageDisplay.this.matchFilter(contents.get(index))) {
                    contentToShowIndex.add(index);
                }
            }
        }

        @Override
        public void showHeader() {
            if (header == null) {
                return;
            }
            sender.sendMessage(header.replace(PAGE_PLACEHOLDER, ""));

            if (filterPattern != null) {
                sender.sendMessage(String.format("[ %s ]", parseFilter()));
            }
        }
    }

    private class ShowPageRunnable extends ShowRunnable {

        private int totalPages = 1;

        public ShowPageRunnable() {
            super();
        }

        @Override
        public void display() {
            if (pageOutOfRange()) {
                return;
            }
            if (contentToShowIndex.isEmpty()) {
                emptyContent();
                return;
            }
            showHeader();
            showContent();
            doEndPadding();
        }

        @Override
        public void calculateContent() {
            int lineCount = 0;
            for (int index = 0, contentsSize = contents.size(); index < contentsSize; index++) {
                String line = contents.get(index);
                if (!PageDisplay.this.matchFilter(line)) {
                    continue;
                }
                if (line.equals(LINE_BREAK_PLACEHOLDER)) {
                    lineCount = contentLinesPerPage;
                    continue;
                }
                if (++lineCount > contentLinesPerPage) {
                    totalPages++;
                    lineCount = 1;
                }
                if (pageToShow == totalPages) {
                    contentToShowIndex.add(index);
                }
            }
        }

        @Override
        public void showHeader() {
            String theHeader;
            if (header == null) {
                theHeader = contents.get(contentToShowIndex.remove(0));
                contentLinesPerPage--;
            }
            else {
                theHeader = header;
            }

            if (theHeader.contains(PAGE_PLACEHOLDER)) {
                sender.sendMessage(theHeader.replace(PAGE_PLACEHOLDER, parsePaging()));
                return;
            }
            sender.sendMessage(theHeader);
            sender.sendMessage(parsePaging());
        }

        private boolean pageOutOfRange() {
            if (pageToShow < 0 || pageToShow > totalPages) {
                sender.sendMessage(String.format("Please enter a page from 1 to %s.", totalPages));
                return true;
            }
            return false;
        }

        private void emptyContent() {
            sender.sendMessage("No matching content to display.");
        }

        private String parsePaging() {
            return (filterPattern == null)
                    ? String.format("[ Page %s of %s ]", pageToShow, totalPages)
                    : String.format("[ Page %s of %s, %s ]", pageToShow, totalPages, parseFilter());
        }

        private void doEndPadding() {
            IntStream.range(0, contentLinesPerPage - contentToShowIndex.size())
                    .unordered()
                    .mapToObj(i -> " ")
                    .forEach(sender::sendMessage);
        }
    }
}
