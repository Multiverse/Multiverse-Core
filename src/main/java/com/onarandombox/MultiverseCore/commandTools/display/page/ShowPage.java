package com.onarandombox.MultiverseCore.commandTools.display.page;

import com.onarandombox.MultiverseCore.commandTools.display.ColourAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.ShowRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class ShowPage extends ShowRunnable<List<String>> {

    protected PageDisplay display;
    protected final List<Integer> contentToShowIndex;

    public ShowPage(PageDisplay display) {
        super(display);
        this.display = display;
        this.contentToShowIndex = new ArrayList<>();

        if (this.display.getColours() != null) {
            this.display.getColours().reset();
        }
    }

    @Override
    public void run() {
        calculateContent();
        if (!validateContent()) {
            return;
        }
        if (contentToShowIndex.isEmpty()) {
            this.display.getSender().sendMessage("No matching content to display.");
            return;
        }
        display();
    }

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
