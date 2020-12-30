package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ContentDisplay<T> {

    protected final Plugin plugin;
    protected final CommandSender sender;
    protected final String header;
    protected final ContentCreator<T> creator;
    protected final ContentFilter filter;
    protected final ColourAlternator colours;

    public ContentDisplay(Plugin plugin, @NotNull CommandSender sender,
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

    public void showContent() {
        getShowPageRunnable().runTask(this.plugin);
    }

    public void showContentAsync() {
        getShowPageRunnable().runTaskAsynchronously(this.plugin);
    }

    public abstract ShowRunnable<T> getShowPageRunnable();

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
