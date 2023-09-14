package org.mvplugins.multiverse.core.configuration.functions;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface NodeStringParser<T> {
    Try<T> parse(@Nullable String string, @NotNull Class<T> type);
}
