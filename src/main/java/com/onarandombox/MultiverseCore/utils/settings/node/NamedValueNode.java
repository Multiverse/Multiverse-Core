package com.onarandombox.MultiverseCore.utils.settings.node;

import java.util.Optional;

import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;

/**
 * A {@link TypedValueNode} that has a name.
 *
 * @param <T> The type of the node's value.
 */
public interface NamedValueNode<T> extends TypedValueNode<T> {
    /**
     * Gets the name of this node. Used for identifying the node from user input.
     *
     * @return The name of this node.
     */
    Optional<String> getName();
}
