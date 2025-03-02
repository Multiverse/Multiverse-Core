package org.mvplugins.multiverse.core.display.parsers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import org.jetbrains.annotations.NotNull;

/**
 * Simple parser for list object.
 *
 * @param <T>   List element type.
 */
public class ListContentProvider<T> implements ContentProvider {

    /**
     * New list content parser for the given list.
     *
     * @param list  The list object to parse.
     * @param <T>   List element type.
     * @return New {@link MapContentProvider} instance.
     */
    public static <T> ListContentProvider<T> forContent(List<T> list) {
        return new ListContentProvider<>(list);
    }

    private final List<T> list;

    private String format = null;

    ListContentProvider(List<T> list) {
        this.list = list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> parse(@NotNull BukkitCommandIssuer issuer) {
        if (format == null) {
            return list.stream().map(Object::toString).collect(Collectors.toList());
        }
        return list.stream().map(element -> String.format(format, element)).collect(Collectors.toList());
    }

    /**
     * Sets the format that will be used to parse each list entry. Uses java string format pattern.
     *
     * @param format    The format to use.
     * @return Same {@link ListContentProvider} for method chaining.
     */
    public ListContentProvider<T> withFormat(String format) {
        this.format = format;
        return this;
    }

    public List<T> getList() {
        return list;
    }

    public String getFormat() {
        return format;
    }
}
