package com.onarandombox.MultiverseCore.display.parsers;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Simple parser for list object.
 *
 * @param <T>   List element type.
 */
public class ListContentParser<T> implements ContentParser {

    /**
     * New list content parser for the given list.
     *
     * @param list  The list object to parse.
     * @param <T>   List element type.
     * @return New {@link MapContentParser} instance.
     */
    public static <T> ListContentParser<T> forContent(List<T> list) {
        return new ListContentParser<>(list);
    }

    private final List<T> list;

    private String format = "%s";

    public ListContentParser(List<T> list) {
        this.list = list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void parse(@NotNull CommandSender sender, @NotNull List<String> content) {
        list.forEach(element -> content.add(String.format(format, element)));
    }

    /**
     * Sets the format that will be used to parse each list entry. Uses java string format pattern.
     *
     * @param format    The format to use.
     * @return Same {@link ListContentParser} for method chaining.
     */
    public ListContentParser<T> withFormat(String format) {
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
