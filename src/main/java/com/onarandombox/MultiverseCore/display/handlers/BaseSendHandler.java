package com.onarandombox.MultiverseCore.display.handlers;

import com.google.common.base.Strings;
import com.onarandombox.MultiverseCore.display.filters.ContentFilter;
import com.onarandombox.MultiverseCore.display.filters.DefaultContentFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base implementation of {@link SendHandler} with some common parameters.
 *
 * @param <T>   The subclass that inherited this baseclass.
 */
public abstract class BaseSendHandler<T extends BaseSendHandler<?>> implements SendHandler {

    protected String header = "";
    protected ContentFilter filter = DefaultContentFilter.getInstance();

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull CommandSender sender, @NotNull List<String> content) {
        sendHeader(sender);
        List<String> filteredContent = filterContent(content);
        if (filteredContent.isEmpty()) {
            sender.sendMessage(String.format("%sThere is no content to display.", ChatColor.RED));
            return;
        }
        sendContent(sender, filteredContent);
    }

    /**
     * Sends the header if header is present.
     *
     * @param sender    The target which the header will be displayed to.
     */
    protected void sendHeader(CommandSender sender) {
        if (!Strings.isNullOrEmpty(header)) {
            sender.sendMessage(header);
        }
    }

    /**
     * Filter to keep only contents that matches the filter.
     *
     * @param content   The content to filter on.
     * @return The filtered list of content.
     */
    protected List<String> filterContent(@NotNull List<String> content) {
        if (filter.needToFilter()) {
            return content.stream().filter(filter::checkMatch).collect(Collectors.toList());
        }
        return content;
    }

    /**
     * Display the contents.
     *
     * @param sender    The target which the content will be displayed to.
     * @param content   The content to display.
     */
    protected abstract void sendContent(@NotNull CommandSender sender, @NotNull List<String> content);

    /**
     * Sets header to be displayed.
     *
     * @param header        The header text.
     * @param replacements  String formatting replacements.
     * @return Same {@link T} for method chaining.
     */
    public T withHeader(@NotNull String header, @NotNull Object...replacements) {
        this.header = String.format(header, replacements);
        return (T) this;
    }

    /**
     * Sets content filter used to match specific content to be displayed.
     *
     * @param filter    The filter to use.
     * @return Same {@link T} for method chaining.
     */
    public T withFilter(@NotNull ContentFilter filter) {
        this.filter = filter;
        return (T) this;
    }

    public String getHeader() {
        return header;
    }

    public ContentFilter getFilter() {
        return filter;
    }
}
