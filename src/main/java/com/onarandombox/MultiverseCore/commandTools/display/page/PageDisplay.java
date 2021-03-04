/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandtools.display.page;

import com.onarandombox.MultiverseCore.commandtools.contexts.PageFilter;
import com.onarandombox.MultiverseCore.commandtools.display.ContentDisplay;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Used to display list of multiple lines with paging and filter.
 */
public class PageDisplay extends ContentDisplay<PageDisplay, List<String>> {

    private int pageToShow = FIST_PAGE;
    private int contentLinesPerPage = DEFAULT_LINES_PER_PAGE; // excludes header

    public static final int FIST_PAGE = 1;
    public static final int DEFAULT_LINES_PER_PAGE = 8;
    public static final String PAGE_PLACEHOLDER = "%page%";
    public static final String LINE_BREAK_PLACEHOLDER = "%lf%";

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NotNull ShowPage getShowRunnable() {
        return (this.sender instanceof ConsoleCommandSender)
                ? new ShowAllPage(this)
                : new ShowSelectedPage(this);
    }

    public @NotNull PageDisplay withPageFilter(@NotNull PageFilter pageFilter) {
        this.pageToShow = pageFilter.getPage();
        this.filter = pageFilter.getFilter();
        return this;
    }

    public @NotNull PageDisplay withPage(int pageToShow) {
        this.pageToShow = pageToShow;
        return this;
    }

    public @NotNull PageDisplay withLinesPerPage(int contentLinesPerPage) {
        this.contentLinesPerPage = contentLinesPerPage;
        return this;
    }

    public void reduceContentLinesPerPage(int by) {
        this.contentLinesPerPage -= by;
    }

    public int getPageToShow() {
        return pageToShow;
    }

    public int getContentLinesPerPage() {
        return contentLinesPerPage;
    }
}
