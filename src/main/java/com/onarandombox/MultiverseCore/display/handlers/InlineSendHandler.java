package com.onarandombox.MultiverseCore.display.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Display the contents in a single line.
 */
public class InlineSendHandler extends BaseSendHandler<InlineSendHandler> {

    /**
     * Makes a new {@link InlineSendHandler} instance to use.
     *
     * @return  New {@link InlineSendHandler} instance.
     */
    public static InlineSendHandler create() {
        return new InlineSendHandler();
    }

    private String delimiter = ChatColor.WHITE + ", ";

    public InlineSendHandler() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendContent(@NotNull CommandSender sender, @NotNull List<String> content) {
        if (filter.needToFilter()) {
            sender.sendMessage(String.format("%s[Filter '%s']", ChatColor.GRAY, filter));
        }
        sender.sendMessage(String.join(delimiter, content));
    }

    /**
     * Sets the delimiter. A sequence of characters that is used to separate each of the elements in content.
     *
     * @param delimiter The delimiter to use.
     * @return Same {@link InlineSendHandler} for method chaining.
     */
    public InlineSendHandler withDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public String getDelimiter() {
        return delimiter;
    }
}
