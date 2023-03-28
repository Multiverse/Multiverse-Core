package com.onarandombox.MultiverseCore.configuration.node;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ValueNode<T> extends Node {
    /**
     * Gets the name of this node. Used for identifying the node from user input.
     *
     * @return The name of this node.
     */
    Optional<String> getName();

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
     * Validates the value of this node.
     *
     * @param value The value to validate.
     * @return True if the value is valid, false otherwise.
     */
    boolean validate(T value);

    /**
     * Called when the value of this node is set.
     *
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    void onSetValue(T oldValue, T newValue);
}
