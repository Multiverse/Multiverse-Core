package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected String emptyMessage = DEFAULT_EMPTY_MESSAGE;

    public static final String DEFAULT_EMPTY_MESSAGE = "No matching content to display.";

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

    public @NotNull C withSender(@NotNull CommandSender sender) {
        this.sender = sender;
        return (C) this;
    }

    public @NotNull C withHeader(@NotNull String header) {
        this.header = header;
        return (C) this;
    }

    public @NotNull C withCreator(@NotNull ContentCreator<T> creator) {
        this.creator = creator;
        return (C) this;
    }

    public @NotNull C withFilter(@NotNull ContentFilter filter) {
        this.filter = filter;
        return (C) this;
    }

    public @NotNull C withColors(@NotNull ColorAlternator colours) {
        this.colours = colours;
        return (C) this;
    }

    public @NotNull C withEmptyMessage(@NotNull String emptyMessage) {
        this.emptyMessage = emptyMessage;
        return (C) this;
    }

    public @NotNull CommandSender getSender() {
        return sender;
    }

    public @Nullable String getHeader() {
        return header;
    }

    public @NotNull ContentCreator<T> getCreator() {
        return creator;
    }

    public @NotNull ContentFilter getFilter() {
        return filter;
    }

    public @NotNull ColorAlternator getColours() {
        return colours;
    }

    public @NotNull String getEmptyMessage() {
        return emptyMessage;
    }
}
