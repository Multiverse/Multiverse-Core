/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools.display.page;

import com.onarandombox.MultiverseCore.commandTools.PageFilter;
import com.onarandombox.MultiverseCore.commandTools.display.ColourAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentDisplay;
import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import com.onarandombox.MultiverseCore.commandTools.display.ShowRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PageDisplay extends ContentDisplay<List<String>> {

    private final int pageToShow;
    private int contentLinesPerPage; // excludes header

    public static final int FIST_PAGE = 1;
    public static final int DEFAULT_LINES_PER_PAGE = 8;
    public static final String PAGE_PLACEHOLDER = "%page%";
    public static final String LINE_BREAK_PLACEHOLDER = "%lf%";

    public PageDisplay(@NotNull Plugin plugin,
                       @NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull ContentCreator<List<String>> creator,
                       @NotNull ContentFilter filter,
                       @Nullable ColourAlternator colours,
                       int pageToShow,
                       int contentLinesPerPage) {

        super(plugin, sender, header, creator, filter, colours);
        this.pageToShow = pageToShow;
        this.contentLinesPerPage = contentLinesPerPage;
    }

    @Override
    public ShowRunnable<List<String>> getShowPageRunnable() {
        return (this.sender instanceof ConsoleCommandSender)
                ? new ShowAllPage(this)
                : new ShowSelectedPage(this);
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
