package com.onarandombox.MultiverseCore.configuration.node;

import java.util.Optional;

import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;

/**
 * A {@link TypedValueNode} that has a name, validation, and action to be performed when the value is set.
 *
 * @param <T> The type of the node's value.
 */
public interface EnhancedValueNode<T> extends TypedValueNode<T> {
    /**
     * Gets the name of this node. Used for identifying the node from user input.
     *
     * @return The name of this node.
     */
    Optional<String> getName();

    boolean isValid(T value);

    /**
     * Called when the value of this node is set.
     *
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    void onSetValue(T oldValue, T newValue);
}
