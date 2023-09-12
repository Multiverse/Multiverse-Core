package org.mvplugins.multiverse.core.display.handlers;

import java.util.List;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;

/**
 * Base implementation of {@link SendHandler} with some common parameters.
 *
 * @param <T>   The subclass that inherited this baseclass.
 */
public abstract class BaseSendHandler<T extends BaseSendHandler<?>> implements SendHandler {

    /**
     * Header to be displayed.
     */
    protected String header = "";

    /**
     * Filter to keep only contents that matches the filter.
     */
    protected ContentFilter filter = DefaultContentFilter.get();

    /**
     * Fallback message to be displayed when there is no content to display.
     */
    protected String noContentMessage = String.format("%sThere is no content to display.", ChatColor.RED);

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull BukkitCommandIssuer issuer, @NotNull List<String> content) {
        sendHeader(issuer);
        List<String> filteredContent = filterContent(content);
        if (filteredContent.isEmpty() && !Strings.isNullOrEmpty(noContentMessage)) {
            issuer.sendMessage(noContentMessage);
            return;
        }
        sendContent(issuer, filteredContent);
    }

    /**
     * Sends the header if header is present.
     *
     * @param issuer    The target which the header will be displayed to.
     */
    protected void sendHeader(BukkitCommandIssuer issuer) {
        if (!Strings.isNullOrEmpty(header)) {
            issuer.sendMessage(header);
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
     * @param issuer    The target which the content will be displayed to.
     * @param content   The content to display.
     */
    protected abstract void sendContent(@NotNull BukkitCommandIssuer issuer, @NotNull List<String> content);

    /**
     * Sets header to be displayed.
     *
     * @param header        The header text.
     * @param replacements  String formatting replacements.
     * @return Same {@link T} for method chaining.
     */
    public T withHeader(@NotNull String header, @NotNull Object... replacements) {
        this.header = String.format(header, replacements);
        return getT();
    }

    /**
     * Sets content filter used to match specific content to be displayed.
     *
     * @param filter    The filter to use.
     * @return Same {@link T} for method chaining.
     */
    public T withFilter(@NotNull ContentFilter filter) {
        this.filter = filter;
        return getT();
    }

    /**
     * Sets the message to be displayed when there is no content to display.
     *
     * @param message   The message to display. Null to disable.
     * @return Same {@link T} for method chaining.
     */
    public T noContentMessage(@Nullable String message) {
        this.noContentMessage = message;
        return getT();
    }

    @SuppressWarnings("unchecked")
    private @NotNull T getT() {
        return (T) this;
    }

    public String getHeader() {
        return header;
    }

    public ContentFilter getFilter() {
        return filter;
    }

    public String getNoContentMessage() {
        return noContentMessage;
    }
}
