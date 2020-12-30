package com.onarandombox.MultiverseCore.commandTools.display;

import java.util.stream.IntStream;

public class ShowSelectedPage extends ShowPage {

    private int totalPages = 1;

    public ShowSelectedPage(PageDisplay display) {
        super(display);
    }

    @Override
    public void display() {
        super.display();
        doEndPadding();
    }

    @Override
    public void calculateContent() {
        int lineCount = 0;
        int index = -1;
        for (String line : this.display.getContents()) {
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

    @Override
    public boolean validateContent() {
        return !pageOutOfRange();
    }

    @Override
    public void showHeader() {
        String theHeader;
        if (this.display.getHeader() == null) {
            theHeader = this.display.getContents().get(contentToShowIndex.remove(0));
            this.display.reduceContentLinesPerPage(1);
        }
        else {
            theHeader = this.display.getHeader();
        }

        if (theHeader.contains(PageDisplay.PAGE_PLACEHOLDER)) {
            this.display.getSender().sendMessage(theHeader.replace(PageDisplay.PAGE_PLACEHOLDER, parsePaging()));
            return;
        }
        this.display.getSender().sendMessage(theHeader);
        this.display.getSender().sendMessage(parsePaging());
    }

    private boolean pageOutOfRange() {
        if (this.display.getPageToShow() < 0 || this.display.getPageToShow() > totalPages) {
            this.display.getSender().sendMessage((totalPages == 1)
                    ? "There is only 1 page."
                    : String.format("Please enter a page from 1 to %s.", totalPages));
            return true;
        }
        return false;
    }

    private String parsePaging() {
        ContentFilter filter = this.display.getFilter();
        return (filter.hasFilter())
                ? String.format("[ Page %s of %s, %s ]", this.display.getPageToShow(), totalPages, filter.getFormattedString())
                : String.format("[ Page %s of %s ]", this.display.getPageToShow(), totalPages);
    }

    private void doEndPadding() {
        IntStream.range(0, this.display.getContentLinesPerPage() - contentToShowIndex.size())
                .unordered()
                .mapToObj(i -> " ")
                .forEach(this.display.getSender()::sendMessage);
    }
}
