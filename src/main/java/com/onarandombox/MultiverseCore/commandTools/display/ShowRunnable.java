package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @param <D> {@link ContentDisplay} type that is targted to.
 * @param <T> Type of content its displaying.
 */
public abstract class ShowRunnable<D extends ContentDisplay<?, T>, T> extends BukkitRunnable {

    protected final D display;
    protected T contents;

    protected ShowRunnable(@NotNull D display) {
        this.display = display;
    }

    /**
     * Run the showing of {@link ContentDisplay}.
     */
    @Override
    public void run() {
        this.contents = this.display.getCreator().generateContent();
        calculateContent();
        if (!validateContent()) {
            return;
        }
        display();
    }

    /**
     * Show header and contents to sender.
     */
    protected void display() {
        showHeader();
        if (!hasContentToShow()) {
            this.display.getSender().sendMessage(this.display.getEmptyMessage());
            return;
        }
        showContent();
    }

    /**
     * Generate the content to show based on filter, pages or other factors depending on implementation.
     */
    protected abstract void calculateContent();

    /**
     * Check if there is anything to show after {@link ShowRunnable#calculateContent()}.
     *
     * @return True if content is present, false otherwise.
     */
    protected abstract boolean hasContentToShow();

    /**
     *
     * @return True if valid, false otherwise.
     */
    protected abstract boolean validateContent();

    /**
     * Displays header to the sender.
     */
    protected abstract void showHeader();

    /**
     * Displays content to the sender.
     */
    protected abstract void showContent();
}
