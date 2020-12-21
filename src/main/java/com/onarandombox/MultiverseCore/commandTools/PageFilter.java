/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

public class PageFilter {
    private final String filter;
    private final int page;

    public PageFilter(String filter, int page) {
        this.filter = filter;
        this.page = page;
    }

    public String getFilter() {
        return filter;
    }

    public int getPage() {
        return page;
    }
}
