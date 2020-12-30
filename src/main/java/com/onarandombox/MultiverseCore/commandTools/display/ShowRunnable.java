package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class ShowRunnable<T> extends BukkitRunnable {

    protected final T contents;

    protected ShowRunnable(ContentDisplay<T> display) {
        this.contents = display.getCreator().generateContent();
    }

    @Override
    public void run() {
        calculateContent();
        display();
    }

    public void display() {
        showHeader();
        showContent();
    }

    public abstract void calculateContent();

    public abstract boolean validateContent();

    public abstract void showHeader();

    public abstract void showContent();
}
