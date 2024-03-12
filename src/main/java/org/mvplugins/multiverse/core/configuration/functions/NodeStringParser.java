package org.mvplugins.multiverse.core.configuration.functions;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A function that parses a string into a node value object of type {@link T}.
 *
 * @param <T>   The type of the object to parse.
 */
@FunctionalInterface
public interface NodeStringParser<T> {
    /**
     * Parses a string into a node value object of type {@link T}.
     *
     * @param string    The string to parse.
     * @param type      The type of the object to parse.
     * @return The parsed object, or {@link Try.Failure} if the string could not be parsed.
     */
    @NotNull Try<T> parse(@Nullable String string, @NotNull Class<T> type);
}
