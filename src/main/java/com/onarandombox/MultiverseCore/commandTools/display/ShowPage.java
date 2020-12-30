package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class ShowPage extends BukkitRunnable {

    protected PageDisplay display;
    protected final List<Integer> contentToShowIndex;

    public ShowPage(PageDisplay display) {
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

    public void display() {
        showHeader();
        showContent();
    }

    public abstract void calculateContent();

    public abstract boolean validateContent();

    public abstract void showHeader();

    public void showContent() {
        ColourAlternator colours = this.display.getColours();

        contentToShowIndex.stream()
                .map(this.display.getContents()::get)
                .map(line -> line.equals(PageDisplay.LINE_BREAK_PLACEHOLDER)
                        ? ""
                        : line.replace(PageDisplay.PAGE_PLACEHOLDER, ""))
                .map(line -> ((colours == null) ? "" : colours.get()) + line)
                .forEach(this.display.getSender()::sendMessage);
    }
}
