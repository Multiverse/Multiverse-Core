package com.onarandombox.MultiverseCore.displaytools;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

public class ContentDisplay<T> {

    public static final String PAGE_PLACEHOLDER = "%page%";
    public static final String LINE_BREAK_PLACEHOLDER = "%lf%";

    private CommandSender sender;
    private String header;
    private T contents;
    private String emptyMessage;
    private DisplayHandler<T> displayHandler;
    private ColorTool colorTool;
    private ContentFilter filter;
    private final Map<DisplaySetting<?>, Object> settingsMap;

    private ContentDisplay() {
        settingsMap = new WeakHashMap<>();
    }

    public void display() {
        Collection<String> formattedContent = this.displayHandler.format(this);
        sendHeader();
        sendBody(formattedContent);
    }

    public void sendHeader() {
        this.sender.sendMessage(this.header);
    }

    public void sendBody(Collection<String> bodyContent) {
        this.sender.sendMessage(bodyContent.toArray(new String[0]));
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getHeader() {
        return header;
    }

    public T getContents() {
        return contents;
    }

    public String getEmptyMessage() {
        return emptyMessage;
    }

    public DisplayHandler<T> getDisplayHandler() {
        return displayHandler;
    }

    public ColorTool getColorTool() {
        return colorTool;
    }

    public ContentFilter getFilter() {
        return filter;
    }

    public <S> S getSetting(DisplaySetting<S> setting) {
        return (S) settingsMap.getOrDefault(setting, setting.defaultValue());
    }

    public static class Builder<T> {

        private final ContentDisplay<T> display;

        public Builder() {
            this.display = new ContentDisplay<>();
        }

        public Builder<T> sender(CommandSender sender) {
            this.display.sender = sender;
            return this;
        }

        public Builder<T> header(String header, Object...replacements) {
            this.display.header = String.format(header, replacements);
            return this;
        }

        public Builder<T> contents(T contents) {
            this.display.contents = contents;
            return this;
        }

        public Builder<T> emptyMessage(String emptyMessage, Object...replacements) {
            this.display.emptyMessage = String.format(emptyMessage, replacements);
            return this;
        }

        public Builder<T> displayHandler(DisplayHandler<T> displayHandler) {
            this.display.displayHandler = displayHandler;
            return this;
        }

        public Builder<T> colorTool(ColorTool colorTool) {
            this.display.colorTool = colorTool;
            return this;
        }

        public Builder<T> filter(ContentFilter filter) {
            this.display.filter = filter;
            return this;
        }

        public <S> Builder<T> setting(DisplaySetting<S> setting, S value) {
            this.display.settingsMap.put(setting, value);
            return this;
        }

        public void display() {
            this.display.display();
        }

        public void display(Plugin plugin) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, this.display::display);
        }
    }
}
