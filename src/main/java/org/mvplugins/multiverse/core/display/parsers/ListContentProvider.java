package org.mvplugins.multiverse.core.display.parsers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.locale.message.Message;

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

    ListContentProvider(List<T> list) {
        this.list = list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> parse(@NotNull MVCommandIssuer issuer) {
        return list.stream()
                .map(object -> object instanceof Message message ? message.formatted(issuer) : String.valueOf(object))
                .toList();
    }

    public List<T> getList() {
        return list;
    }
}
