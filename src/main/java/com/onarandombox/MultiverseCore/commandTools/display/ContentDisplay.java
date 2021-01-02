package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Displays various types of content to sender.
 *
 * @param <T> Type of content to display.
 */
public abstract class ContentDisplay<C extends ContentDisplay<?, T>, T> {

    protected CommandSender sender;
    protected String header;
    protected ContentCreator<T> creator;
    protected ContentFilter filter = ContentFilter.EMPTY;
    protected ColorAlternator colours;

    /**
     * Build into a runnable for showing of content.
     *
     * @return {@link ShowRunnable}.
     */
    public ShowRunnable<C, T> build() {
        buildValidation();
        return getShowRunnable();
    }

    /**
     * Set defaults if null and ensure that required fields are not null.
     */
    protected void buildValidation() {
        if (this.colours == null) {
            this.colours = new ColorAlternator();
        }
        if (sender == null || creator == null) {
            throw new IllegalStateException("Incomplete ContentDisplay fields.");
        }
    }

    /**
     * Runnable used to format and display contents to
     *
     * @return {@link ShowRunnable}
     */
    protected abstract @NotNull ShowRunnable<C, T> getShowRunnable();

    public C withSender(CommandSender sender) {
        this.sender = sender;
        return (C) this;
    }

    public C withHeader(String header) {
        this.header = header;
        return (C) this;
    }

    public C withCreator(ContentCreator<T> creator) {
        this.creator = creator;
        return (C) this;
    }

    public C withFilter(ContentFilter filter) {
        this.filter = filter;
        return (C) this;
    }

    public C withColors(ColorAlternator colours) {
        this.colours = colours;
        return (C) this;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getHeader() {
        return header;
    }

    public ContentCreator<T> getCreator() {
        return creator;
    }

    public ContentFilter getFilter() {
        return filter;
    }

    public ColorAlternator getColours() {
        return colours;
    }
}
