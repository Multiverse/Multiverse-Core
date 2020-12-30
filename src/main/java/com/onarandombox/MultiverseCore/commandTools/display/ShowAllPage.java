package com.onarandombox.MultiverseCore.commandTools.display;

public class ShowAllPage extends ShowPage {

    public ShowAllPage(PageDisplay display) {
        super(display);
    }

    @Override
    public void calculateContent() {
        int index = -1;
        for (String line : this.display.getContents()) {
            index++;
            if (PageDisplay.LINE_BREAK_PLACEHOLDER.equals(line)
                || this.display.getFilter().checkMatch(this.display.getContents().get(index))) {
                contentToShowIndex.add(index);
            }
        }
    }

    @Override
    public boolean validateContent() {
        return true;
    }

    @Override
    public void showHeader() {
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
