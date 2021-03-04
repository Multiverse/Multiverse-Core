package com.onarandombox.MultiverseCore.commandtools.display.page;

import com.onarandombox.MultiverseCore.commandtools.display.ContentFilter;

/**
 * Show a everything from a multi-line content.
 */
public class ShowAllPage extends ShowPage {

    public ShowAllPage(PageDisplay display) {
        super(display);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void calculateContent() {
        int index = -1;
        for (String line : this.contents) {
            index++;
            if (PageDisplay.LINE_BREAK_PLACEHOLDER.equals(line)
                || this.display.getFilter().checkMatch(this.contents.get(index))) {
                contentToShowIndex.add(index);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean validateContent() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showHeader() {
        if (this.display.getHeader() == null) {
            return;
        }

        this.display.getSender().sendMessage(this.display.getHeader().replace(PageDisplay.PAGE_PLACEHOLDER, ""));

        ContentFilter filter = this.display.getFilter();
        if (filter.hasFilter()) {
            this.display.getSender().sendMessage(String.format("[ %s ]", filter.getFormattedString()));
        }
    }
}
