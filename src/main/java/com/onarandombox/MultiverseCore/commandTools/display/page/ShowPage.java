package com.onarandombox.MultiverseCore.commandTools.display.page;

import com.onarandombox.MultiverseCore.commandTools.display.ColourAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.ShowRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class ShowPage extends ShowRunnable<PageDisplay, List<String>> {

    protected final List<Integer> contentToShowIndex;

    public ShowPage(PageDisplay display) {
        super(display);
        this.contentToShowIndex = new ArrayList<>();

        if (this.display.getColours() != null) {
            this.display.getColours().reset();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasContentToShow() {
        return !contentToShowIndex.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showContent() {
        ColourAlternator colours = this.display.getColours();

        contentToShowIndex.stream()
                .map(this.contents::get)
                .map(line -> line.equals(PageDisplay.LINE_BREAK_PLACEHOLDER)
                        ? ""
                        : line.replace(PageDisplay.PAGE_PLACEHOLDER, ""))
                .map(line -> ((colours == null) ? "" : colours.get()) + line)
                .forEach(this.display.getSender()::sendMessage);
    }
}
