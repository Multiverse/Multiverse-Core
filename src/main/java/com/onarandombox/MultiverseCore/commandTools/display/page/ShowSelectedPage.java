package com.onarandombox.MultiverseCore.commandTools.display.page;

import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;

import java.util.stream.IntStream;

/**
 * Show a single page from a multi-line content.
 */
public class ShowSelectedPage extends ShowPage {

    private int totalPages = 1;

    public ShowSelectedPage(PageDisplay display) {
        super(display);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void display() {
        super.display();
        doEndPadding();
    }

    private void doEndPadding() {
        IntStream.range(0, this.display.getContentLinesPerPage() - contentToShowIndex.size())
                .unordered()
                .mapToObj(i -> " ")
                .forEach(this.display.getSender()::sendMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void calculateContent() {
        int lineCount = 0;
        int index = -1;
        for (String line : this.contents) {
            index++;
            if (PageDisplay.LINE_BREAK_PLACEHOLDER.equals(line)) {
                lineCount = this.display.getContentLinesPerPage();
                continue;
            }
            if (!this.display.getFilter().checkMatch(line)) {
                continue;
            }
            if (++lineCount > this.display.getContentLinesPerPage()) {
                totalPages++;
                lineCount = 1;
            }
            if (this.display.getPageToShow() == totalPages) {
                contentToShowIndex.add(index);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean validateContent() {
        return !pageOutOfRange();
    }

    private boolean pageOutOfRange() {
        if (this.display.getPageToShow() < 1 || this.display.getPageToShow() > totalPages) {
            this.display.getSender().sendMessage((totalPages == 1)
                    ? "There is only 1 page."
                    : String.format("Please enter a page from 1 to %s.", totalPages));
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showHeader() {
        String header = getHeader();

        // Paging inline with header
        if (header.contains(PageDisplay.PAGE_PLACEHOLDER)) {
            this.display.getSender().sendMessage(header.replace(PageDisplay.PAGE_PLACEHOLDER, parsePaging()));
            return;
        }

        this.display.getSender().sendMessage(header);
        this.display.getSender().sendMessage(parsePaging());
    }

    private String getHeader() {
        if (this.display.getHeader() != null) {
            return this.display.getHeader();
        }
        // Let first content line be the header.
        this.display.reduceContentLinesPerPage(1);
        return this.contents.get(contentToShowIndex.remove(0));
    }

    private String parsePaging() {
        ContentFilter filter = this.display.getFilter();
        return (filter.hasFilter())
                ? String.format("[ Page %s of %s, %s ]", this.display.getPageToShow(), totalPages, filter.getFormattedString())
                : String.format("[ Page %s of %s ]", this.display.getPageToShow(), totalPages);
    }
}
