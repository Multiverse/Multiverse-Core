package com.onarandombox.MultiverseCore.commandTools.display.page;

import com.onarandombox.MultiverseCore.commandTools.display.ColorAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.ShowRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Show multi-line contents.
 */
public abstract class ShowPage extends ShowRunnable<PageDisplay, List<String>> {

    protected final List<Integer> contentToShowIndex;

    public ShowPage(PageDisplay display) {
        super(display);
        this.contentToShowIndex = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasContentToShow() {
        return !contentToShowIndex.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showContent() {
        ColorAlternator colours = this.display.getColours();
        colours.reset();

        contentToShowIndex.stream()
                .map(this.contents::get)
                .map(line -> line.equals(PageDisplay.LINE_BREAK_PLACEHOLDER)
                        ? ""
                        : line.replace(PageDisplay.PAGE_PLACEHOLDER, ""))
                .map(line -> colours.get() + line)
                .forEach(this.display.getSender()::sendMessage);
    }
}
