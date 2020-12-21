package com.onarandombox.MultiverseCore.commands_helper;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class PageDisplay {

    private final CommandSender sender;
    private final String header;
    private final List<String> contents;
    private final int pageToShow;
    private final int contentLinesPerPage; // excludes header
    private final Pattern filter;

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LINES_PER_PAGE = 8;
    private static final String PAGE_PLACEHOLDER = "%page%";
    private static final String LINE_BREAK_PLACEHOLDER = "%lf%";
    private static final Pattern REGEX_SPECIAL_CHARS = Pattern.compile("[.+*?\\[^\\]$(){}=!<>|:-\\\\]");

    public PageDisplay(@NotNull CommandSender sender,
                       @NotNull List<String> contents) {

        this(sender, null, contents, DEFAULT_PAGE, DEFAULT_LINES_PER_PAGE, null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents) {

        this(sender, header, contents, DEFAULT_PAGE, DEFAULT_LINES_PER_PAGE, null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int currentPage) {

        this(sender, header, contents, currentPage, DEFAULT_LINES_PER_PAGE, null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int currentPage,
                       int linesPerPage) {

        this(sender, header, contents, currentPage, linesPerPage, null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int currentPage,
                       @Nullable String filter) {

        this(sender, header, contents, currentPage, DEFAULT_LINES_PER_PAGE, filter);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int currentPage,
                       int linesPerPage,
                       @Nullable String filter) {

        this.sender = sender;
        this.header = header;
        this.contents = contents;
        this.pageToShow = currentPage;
        this.contentLinesPerPage = linesPerPage;
        this.filter = parseFilter(filter);
    }

    private Pattern parseFilter(@Nullable String filter) {
        if (filter == null) {
            return null;
        }
        String cleanedFilter = REGEX_SPECIAL_CHARS.matcher(filter.toLowerCase()).replaceAll("\\\\$0");
        return Pattern.compile("(?i).*" + cleanedFilter + ".*");
    }

    public boolean matchFilter(@Nullable String value) {
        return value != null && (filter == null || value.equals(LINE_BREAK_PLACEHOLDER) || filter.matcher(value).matches());
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
                    .map(line -> line.equals(LINE_BREAK_PLACEHOLDER) ? " " : line)
                    .forEach(sender::sendMessage);
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
                    lineCount = 0;
                }
                if (pageToShow == totalPages) {
                    contentToShowIndex.add(index);
                }
            }
        }

        @Override
        public void showHeader() {
            String theHeader = (header == null)
                    ? contents.get(contentToShowIndex.remove(0))
                    : header;

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
            return String.format("[ Page %s of %s ]", pageToShow, totalPages);
        }

        private void doEndPadding() {
            IntStream.range(0, contentLinesPerPage - contentToShowIndex.size())
                    .mapToObj(i -> " ")
                    .forEach(sender::sendMessage);
        }
    }
}
