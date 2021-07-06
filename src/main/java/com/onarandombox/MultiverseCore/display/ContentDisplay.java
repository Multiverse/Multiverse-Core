package com.onarandombox.MultiverseCore.display;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Helps to display contents such as list and maps in a nicely formatted fashion.
 *
 * @param <T>   Type of content to display.
 */
public class ContentDisplay<T> {

    public static final String LINE_BREAK = "%br%";

    /**
     * Creates a ContentDisplay.Builder for the given content.
     *
     * @param content The content to be displayed.
     * @param <T> The type of the content which can be inferred.
     * @return A new Builder.
     */
    public static <T> Builder<T> forContent(T content) {
        return new Builder<>(content);
    }

    private final T contents;

    private CommandSender sender;
    private String header;
    private String emptyMessage = "No matching content to display.";
    private DisplayHandler<T> displayHandler;
    private ColorTool colorTool = ColorTool.DEFAULT;
    private ContentFilter filter = ContentFilter.DEFAULT;
    private final Map<DisplaySetting<?>, Object> settingsMap = new WeakHashMap<>();

    private ContentDisplay(T contents) {
        this.contents = contents;
    }

    /**
     * Do the actual displaying of contents to the sender.
     */
    public void send() {
        Collection<String> formattedContent;
        try {
            formattedContent = (this.contents == null) ? null : this.displayHandler.format(this);
        } catch (DisplayFormatException e) {
            this.sender.sendMessage(String.format("%sError: %s", ChatColor.RED, e.getMessage()));
            return;
        }
        this.displayHandler.sendHeader(this);
        this.displayHandler.sendSubHeader(this);
        this.displayHandler.sendBody(this, formattedContent);
    }

    /**
     * @return Gets the target sender.
     */
    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    /**
     * @return Gets the header to display.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the header text.
     */
    public void setHeader(@NotNull String header) {
        this.header = header;
    }

    /**
     * @return Gets the contents to display.
     */
    public T getContents() {
        return contents;
    }

    /**
     * @return Gets the message to display when no content is shown.
     */
    @NotNull
    public String getEmptyMessage() {
        return emptyMessage;
    }

    /**
     * @return Gets the display handler that formats and sends content to sender.
     */
    @NotNull
    public DisplayHandler<T> getDisplayHandler() {
        return displayHandler;
    }

    /**
     * @return Gets the color tool used.
     */
    @NotNull
    public ColorTool getColorTool() {
        return colorTool;
    }

    /**
     * @return Gets the filter used.
     */
    @NotNull
    public ContentFilter getFilter() {
        return filter;
    }

    /**
     * Gets the value for a given setting option.
     *
     * @param setting   The setting option.
     * @param <S>       The setting type.
     * @return Value set for the given setting.
     */
    public <S> S getSetting(@NotNull DisplaySetting<S> setting) {
        return (S) settingsMap.getOrDefault(setting, setting.defaultValue());
    }

    /**
     * Sets other specific settings that may be used by the {@link DisplayHandler}.
     *
     * @param setting   The settings option.
     * @param value     The value to set.
     * @param <S>       The type of setting.
     */
    public <S> void setSetting(@NotNull DisplaySetting<S> setting, S value) {
        this.settingsMap.put(setting, value);
    }

    /**
     * Builds a {@link ContentDisplay}.
     *
     * @param <T>   Type of content to display.
     */
    public static class Builder<T> {

        private final ContentDisplay<T> display;

        private Builder(T content) {
            this.display = new ContentDisplay<>(content);
        }

        /**
         * Sets target sender to display message to. <b>Required.</b>
         *
         * @param sender The target sender.
         * @return The builder.
         */
        @NotNull
        public Builder<T> sender(@NotNull CommandSender sender) {
            this.display.sender = sender;
            return this;
        }

        /**
         * Sets header to be displayed.
         *
         * @param header        The header text.
         * @param replacements  String formatting replacements.
         * @return The builder.
         */
        @NotNull
        public Builder<T> header(@NotNull String header, Object...replacements) {
            this.display.header = String.format(header, replacements);
            return this;
        }

        /**
         * Sets the message to show when no content is available for display.
         *
         * @param emptyMessage  The message text.
         * @param replacements  String formatting replacements.
         * @return The builder.
         */
        @NotNull
        public Builder<T> emptyMessage(@NotNull String emptyMessage, Object...replacements) {
            this.display.emptyMessage = String.format(emptyMessage, replacements);
            return this;
        }

        /**
         * Sets the display handler that does the formatting and sending of content. <b>Required.</b>
         *
         * @param displayHandler    The display handler for the given content type.
         * @return The builder.
         */
        @NotNull
        public Builder<T> displayHandler(@NotNull DisplayHandler<T> displayHandler) {
            this.display.displayHandler = displayHandler;
            return this;
        }

        /**
         * Sets the color tool used to make messages more colourful.
         *
         * @param colorTool The color tool to use.
         * @return The builder.
         */
        @NotNull
        public Builder<T> colorTool(@NotNull ColorTool colorTool) {
            this.display.colorTool = colorTool;
            return this;
        }

        /**
         * Sets content filter used to match specific content to be displayed.
         *
         * @param filter    The filter to use.
         * @return The builder.
         */
        @NotNull
        public Builder<T> filter(@NotNull ContentFilter filter) {
            this.display.filter = filter;
            return this;
        }

        /**
         * Sets other specific settings that may be used by the {@link DisplayHandler}.
         *
         * @param setting   The settings option.
         * @param value     The value to set.
         * @param <S>       The type of setting.
         * @return The builder.
         */
        @NotNull
        public <S> Builder<T> setting(@NotNull DisplaySetting<S> setting, S value) {
            this.display.settingsMap.put(setting, value);
            return this;
        }

        /**
         * Validates and build the content display.
         *
         * @return The content display.
         */
        @NotNull
        public ContentDisplay<T> build() {
            Objects.requireNonNull(this.display.sender);
            Objects.requireNonNull(this.display.displayHandler);
            return this.display;
        }

        /**
         * Build and show the content to the sender.
         */
        public void show(CommandSender sender) {
            this.sender(sender);
            this.build().send();
        }
    }
}
