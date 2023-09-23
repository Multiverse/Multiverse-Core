package org.mvplugins.multiverse.core.configuration.functions;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A function that suggests possible values for a node value.
 */
@FunctionalInterface
public interface NodeSuggester {
    /**
     * Suggests possible values for a node value. Generated based on the current user input.
     *
     * @param input The current partial user input
     * @return The possible values.
     */
    @NotNull Collection<String> suggest(@Nullable String input);
}
