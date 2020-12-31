package com.onarandombox.MultiverseCore.commandTools.display;

/**
 * Generate content for displaying with {@link ContentDisplay}.
 *
 * @param <T> Type of content to create.
 */
@FunctionalInterface
public interface ContentCreator<T> {
    T generateContent();
}
