/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools.contexts;

import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import org.jetbrains.annotations.NotNull;

/**
 * Filter and paging.
 */
public class PageFilter {
    private final ContentFilter filter;
    private final int page;

    public PageFilter(@NotNull ContentFilter filter,
                      int page) {
        this.filter = filter;
        this.page = page;
    }

    @NotNull
    public ContentFilter getFilter() {
        return filter;
    }

    public int getPage() {
        return page;
    }
}
