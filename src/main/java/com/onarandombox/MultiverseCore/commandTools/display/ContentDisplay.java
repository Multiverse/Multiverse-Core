package com.onarandombox.MultiverseCore.commandTools.display;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ContentDisplay {

    protected final CommandSender sender;
    protected final String header;
    protected final ContentFilter filter;
    protected final ColourAlternator colours;

    public ContentDisplay(@NotNull CommandSender sender,
                          @Nullable String header,
                          @NotNull ContentFilter filter,
                          @Nullable ColourAlternator colours) {

        this.sender = sender;
        this.header = header;
        this.filter = filter;
        this.colours = colours;
    }

    public abstract void showContent();

    public abstract void showContentAsync(@NotNull Plugin plugin);

    public CommandSender getSender() {
        return sender;
    }

    public String getHeader() {
        return header;
    }

    public ContentFilter getFilter() {
        return filter;
    }

    public ColourAlternator getColours() {
        return colours;
    }
}
