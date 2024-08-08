package org.mvplugins.multiverse.core.display.parsers;

import java.util.Collection;
import java.util.Map;

import co.aikar.commands.BukkitCommandIssuer;
import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Simple parser for map object.
 *
 * @param <K>   Key type.
 * @param <V>   Value type.
 */
public class MapContentProvider<K, V> implements ContentProvider {

    /**
     * New map content parser for the given map.
     *
     * @param map   The map object to parse.
     * @param <K>   Key type.
     * @param <V>   Value type.
     * @return New {@link MapContentProvider} instance.
     */
    public static <K, V> MapContentProvider<K, V> forContent(Map<K, V> map) {
        return new MapContentProvider<>(map);
    }

    private final Map<K, V> map;

    private String format = "%s%s%s%s%s";
    private ChatColor keyColor = ChatColor.WHITE;
    private ChatColor valueColor = ChatColor.WHITE;
    private String separator = ": ";

    MapContentProvider(Map<K, V> map) {
        this.map = map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> parse(@NotNull BukkitCommandIssuer issuer) {
        return map.entrySet().stream()
                .map(e -> String.format(format, keyColor, e.getKey(), separator, valueColor, formatValue(e.getValue())))
                .toList();
    }

    private String formatValue(V value) {
        if (value instanceof String stringValue) {
            return Strings.isNullOrEmpty(stringValue) ? "&7&onull" : stringValue;
        }
        return value == null ? "&7&onull" : String.valueOf(value);
    }

    /**
     * Sets the format that will be used to parse each map entry. Uses java string format pattern.
     *
     * @param format    The format to use.
     * @return Same {@link MapContentProvider} for method chaining.
     */
    public MapContentProvider<K, V> withFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     * Sets the color for the key text.
     *
     * @param keyColor  The color to use.
     * @return Same {@link MapContentProvider} for method chaining.
     */
    public MapContentProvider<K, V> withKeyColor(ChatColor keyColor) {
        this.keyColor = keyColor;
        return this;
    }

    /**
     * Sets the color for the value text.
     *
     * @param valueColor    The color to use.
     * @return Same {@link MapContentProvider} for method chaining.
     */
    public MapContentProvider<K, V> withValueColor(ChatColor valueColor) {
        this.valueColor = valueColor;
        return this;
    }

    /**
     * Sets the separator between each key value pairing.
     *
     * @param separator The separator to use.
     * @return Same {@link MapContentProvider} for method chaining.
     */
    public MapContentProvider<K, V> withSeparator(String separator) {
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
