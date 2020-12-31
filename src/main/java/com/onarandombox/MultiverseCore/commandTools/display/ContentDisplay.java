package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Displays various types of content to sender.
 *
 * @param <T> Type of content to display.
 */
public abstract class ContentDisplay<T> {

    protected final Plugin plugin;
    protected final CommandSender sender;
    protected final String header;
    protected final ContentCreator<T> creator;
    protected final ContentFilter filter;
    protected final ColourAlternator colours;

    public ContentDisplay(@NotNull Plugin plugin,
                          @NotNull CommandSender sender,
                          @Nullable String header,
                          @NotNull ContentCreator<T> creator,
                          @NotNull ContentFilter filter,
                          @Nullable ColourAlternator colours) {

        this.plugin = plugin;
        this.sender = sender;
        this.header = header;
        this.creator = creator;
        this.filter = filter;
        this.colours = colours;
    }

    /**
     * Display the content to the {@link ContentDisplay#sender}.
     */
    public void showContent() {
        getShowRunnable().runTask(this.plugin);
    }

    /**
     * Display the content to the {@link ContentDisplay#sender} with a asynchronous task.
     */
    public void showContentAsync() {
        getShowRunnable().runTaskAsynchronously(this.plugin);
    }

    /**
     * Runnable used to format and display contents to
     *
     * @return {@link ShowRunnable}
     */
    public abstract ShowRunnable<? extends ContentDisplay<T>, T> getShowRunnable();

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

    public ColourAlternator getColours() {
        return colours;
    }
}
