package org.mvplugins.multiverse.core.config.node.functions;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A function that suggests possible values for a node value. These suggestions must be able to be used to parse the
 * value from string with {@link NodeStringParser}.
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
