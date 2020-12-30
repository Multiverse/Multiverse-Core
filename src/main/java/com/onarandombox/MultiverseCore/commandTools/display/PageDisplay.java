/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools.display;

import com.onarandombox.MultiverseCore.commandTools.PageFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PageDisplay extends ContentDisplay {

    private final List<String> contents;
    private final int pageToShow;
    private int contentLinesPerPage; // excludes header

    public static final int FIST_PAGE = 1;
    public static final int DEFAULT_LINES_PER_PAGE = 8;
    public static final String PAGE_PLACEHOLDER = "%page%";
    public static final String LINE_BREAK_PLACEHOLDER = "%lf%";

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents) {

        this(sender, header, contents, FIST_PAGE, DEFAULT_LINES_PER_PAGE, new ContentFilter(null), null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int pageToShow) {

        this(sender, header, contents, pageToShow, DEFAULT_LINES_PER_PAGE, new ContentFilter(null), null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int pageToShow,
                       int contentLinesPerPage) {

        this(sender, header, contents, pageToShow, contentLinesPerPage, new ContentFilter(null), null);
    }

    public PageDisplay(@NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull List<String> contents,
                       int pageToShow,
                       int contentLinesPerPage,
                       @Nullable ColourAlternator colours) {

        this(sender, header, contents, pageToShow, contentLinesPerPage, new ContentFilter(null), colours);
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
                       @NotNull ContentFilter filter,
                       @Nullable ColourAlternator colours) {

        super(sender, header, filter, colours);
        this.contents = contents;
        this.pageToShow = pageToShow;
        this.contentLinesPerPage = contentLinesPerPage;
    }

    @Override
    public void showContent() {
        getShowPageRunnable().run();
    }

    @Override
    public void showContentAsync(@NotNull Plugin plugin) {
        getShowPageRunnable().runTaskAsynchronously(plugin);
    }

    @NotNull
    public ShowPage getShowPageRunnable() {
        return (sender instanceof ConsoleCommandSender)
                ? new ShowAllPage(this)
                : new ShowSelectedPage(this);
    }

    public void reduceContentLinesPerPage(int by) {
        this.contentLinesPerPage -= by;
    }

    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    @Nullable
    public String getHeader() {
        return header;
    }

    @NotNull
    public List<String> getContents() {
        return contents;
    }

    public int getPageToShow() {
        return pageToShow;
    }

    public int getContentLinesPerPage() {
        return contentLinesPerPage;
    }

    @NotNull
    public ContentFilter getFilter() {
        return filter;
    }

    @Nullable
    public ColourAlternator getColours() {
        return colours;
    }
}
