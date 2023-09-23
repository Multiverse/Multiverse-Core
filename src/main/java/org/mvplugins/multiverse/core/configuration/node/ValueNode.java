package org.mvplugins.multiverse.core.configuration.node;

import java.util.Collection;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.configuration.functions.NodeSerializer;

public interface ValueNode<T> extends Node {

    /**
     * Gets the name of this node. Used for identifying the node from user input.
     *
     * @return An {@link Option} containing the name of this node, or {@link Option.None} if the node has no name.
     */
    @NotNull Option<String> getName();

    /**
     * Gets the class type {@link T} of the node value.
     *
     * @return The class type of the node value.
     */
    @NotNull Class<T> getType();

    /**
     * Gets the default value with type {@link T} of the node.
     *
     * @return The default value of the node.
     */
    @Nullable T getDefaultValue();

    /**
     * Suggests possible string values for this node. Generated based on the current user input.
     *
     * @param input The current partial user input
     * @return A collection of possible string values.
     */
    @NotNull Collection<String> suggest(@Nullable String input);

    /**
     * Parses the given string into a value of type {@link T}. Used for property set by user input.
     *
     * @param input The string to parse.
     * @return The parsed value, or given exception if parsing failed.
     */
    @NotNull Try<T> parseFromString(@Nullable String input);

    /**
     * Gets the serializer for this node.
     *
     * @return  The serializer for this node.
     */
    @Nullable NodeSerializer<T> getSerializer();

    /**
     * Validates the value of this node.
     *
     * @param value The value to validate.
     * @return An empty {@link Try} if the value is valid, or a {@link Try} containing an exception if the value is
     *         invalid.
     */
    Try<Void> validate(@Nullable T value);

    /**
     * Called when the value of this node is set.
     *
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    void onSetValue(@Nullable T oldValue, @Nullable T newValue);
}
