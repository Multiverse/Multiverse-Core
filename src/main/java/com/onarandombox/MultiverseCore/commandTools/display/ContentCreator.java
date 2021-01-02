package com.onarandombox.MultiverseCore.commandTools.display;

import org.jetbrains.annotations.NotNull;

/**
 * Generate content for displaying with {@link ContentDisplay}.
 *
 * @param <T> Type of content to create.
 */
@FunctionalInterface
public interface ContentCreator<T> {
    @NotNull T generateContent();
}
