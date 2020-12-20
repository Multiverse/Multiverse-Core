package com.onarandombox.MultiverseCore.commands_helper;

public abstract class PagedDisplay {

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    private static final String PAGE_PLACEHOLDER = "%page%";

    private String[] header;
    private String[] contents;
    private int currentPage;
    private int totalPages;
    private int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;

    public void setHeader(String[] header) {
        this.header = header;
    }

    public void setContents(String[] contents) {
        this.contents = contents;
    }


}
