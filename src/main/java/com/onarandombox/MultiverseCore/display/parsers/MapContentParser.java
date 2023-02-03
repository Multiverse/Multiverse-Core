package com.onarandombox.MultiverseCore.display.parsers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Simple parser for map object.
 *
 * @param <K>   Key type.
 * @param <V>   Value type.
 */
public class MapContentParser<K, V> implements ContentParser {

    /**
     * New map content parser for the given map.
     *
     * @param map   The map object to parse.
     * @param <K>   Key type.
     * @param <V>   Value type.
     * @return New {@link MapContentParser} instance.
     */
    public static <K, V> MapContentParser<K, V> forContent(Map<K, V> map) {
        return new MapContentParser<>(map);
    }

    private final Map<K, V> map;

    private String format = "%s%s%s%s%s";
    private ChatColor keyColor = ChatColor.WHITE;
    private ChatColor valueColor = ChatColor.WHITE;
    private String separator = ": ";

    public MapContentParser(Map<K, V> map) {
        this.map = map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void parse(@NotNull CommandSender sender, @NotNull List<String> content) {
        map.forEach((k, v) -> content.add(String.format(format, keyColor, k, separator, valueColor, v)));
    }

    /**
     * Sets the format that will be used to parse each map entry. Uses java string format pattern.
     *
     * @param format    The format to use.
     * @return Same {@link MapContentParser} for method chaining.
     */
    public MapContentParser<K, V> withFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     * Sets the color for the key text.
     *
     * @param keyColor  The color to use.
     * @return Same {@link MapContentParser} for method chaining.
     */
    public MapContentParser<K, V> withKeyColor(ChatColor keyColor) {
        this.keyColor = keyColor;
        return this;
    }

    /**
     * Sets the color for the value text.
     *
     * @param valueColor    The color to use.
     * @return Same {@link MapContentParser} for method chaining.
     */
    public MapContentParser<K, V> withValueColor(ChatColor valueColor) {
        this.valueColor = valueColor;
        return this;
    }

    /**
     * Sets the separator between each key value pairing.
     *
     * @param separator The separator to use.
     * @return Same {@link MapContentParser} for method chaining.
     */
    public MapContentParser<K, V> withSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    public Map<K, V> getMap() {
        return map;
    }

    public String getFormat() {
        return format;
    }

    public ChatColor getKeyColor() {
        return keyColor;
    }

    public ChatColor getValueColor() {
        return valueColor;
    }

    public String getSeparator() {
        return separator;
    }
}
