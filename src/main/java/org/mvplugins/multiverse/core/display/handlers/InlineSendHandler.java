package org.mvplugins.multiverse.core.display.handlers;

import java.util.List;

import co.aikar.commands.BukkitCommandIssuer;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

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
    private String prefix = null;

    InlineSendHandler() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendContent(@NotNull BukkitCommandIssuer issuer, @NotNull List<String> content) {
        if (filter.needToFilter()) {
            issuer.sendMessage(String.format("%s[Filter '%s']", ChatColor.GRAY, filter));
        }
        String message = String.join(delimiter, content);
        if (prefix != null) {
            message = prefix + message;
        }
        issuer.sendMessage(message);
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

    public InlineSendHandler withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getPrefix() {
        return prefix;
    }
}
