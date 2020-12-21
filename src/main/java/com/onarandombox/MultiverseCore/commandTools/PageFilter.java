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
