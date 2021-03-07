package com.onarandombox.MultiverseCore.displaytools;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class ContentDisplay<T> {

    public static final String PAGE_PLACEHOLDER = "%page%";
    public static final String LINE_BREAK = "%br%";

    private CommandSender sender;
    private String header;
    private T contents;
    private String emptyMessage = "No matching content to display.";
    private DisplayHandler<T> displayHandler;
    private ColorTool colorTool = ColorTool.DEFAULT;
    private ContentFilter filter = ContentFilter.DEFAULT;
    private final Map<DisplaySetting<?>, Object> settingsMap = new WeakHashMap<>();

    private ContentDisplay() { }

    public void send() {
        Collection<String> formattedContent = (this.contents == null) ? null : this.displayHandler.format(this);
        this.displayHandler.sendHeader(this);
        this.displayHandler.sendSubHeader(this);
        this.displayHandler.sendBody(this, formattedContent);
    }

    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(@NotNull String header) {
        this.header = header;
    }

    public T getContents() {
        return contents;
    }

    @NotNull
    public String getEmptyMessage() {
        return emptyMessage;
    }

    @NotNull
    public DisplayHandler<T> getDisplayHandler() {
        return displayHandler;
    }

    @NotNull
    public ColorTool getColorTool() {
        return colorTool;
    }

    @NotNull
    public ContentFilter getFilter() {
        return filter;
    }

    public <S> S getSetting(@NotNull DisplaySetting<S> setting) {
        return (S) settingsMap.getOrDefault(setting, setting.defaultValue());
    }

    public <S> void setSetting(@NotNull DisplaySetting<S> setting, S value) {
        this.settingsMap.put(setting, value);
    }

    public static class Builder<T> {

        private final ContentDisplay<T> display;

        public Builder() {
            this.display = new ContentDisplay<>();
        }

        @NotNull
        public Builder<T> sender(@NotNull CommandSender sender) {
            this.display.sender = sender;
            return this;
        }

        @NotNull
        public Builder<T> header(@NotNull String header, Object...replacements) {
            this.display.header = String.format(header, replacements);
            return this;
        }

        @NotNull
        public Builder<T> contents(@Nullable T contents) {
            this.display.contents = contents;
            return this;
        }

        @NotNull
        public Builder<T> emptyMessage(@NotNull String emptyMessage, Object...replacements) {
            this.display.emptyMessage = String.format(emptyMessage, replacements);
            return this;
        }

        @NotNull
        public Builder<T> displayHandler(@NotNull DisplayHandler<T> displayHandler) {
            this.display.displayHandler = displayHandler;
            return this;
        }

        @NotNull
        public Builder<T> colorTool(@NotNull ColorTool colorTool) {
            this.display.colorTool = colorTool;
            return this;
        }

        @NotNull
        public Builder<T> filter(@NotNull ContentFilter filter) {
            this.display.filter = filter;
            return this;
        }

        @NotNull
        public <S> Builder<T> setting(@NotNull DisplaySetting<S> setting, S value) {
            this.display.settingsMap.put(setting, value);
            return this;
        }

        @NotNull
        public ContentDisplay<T> build() {
            Objects.requireNonNull(this.display.sender);
            Objects.requireNonNull(this.display.contents);
            Objects.requireNonNull(this.display.displayHandler);
            return this.display;
        }

        public void display() {
            this.build().send();
        }
    }
}
