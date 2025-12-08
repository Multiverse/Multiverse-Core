package org.mvplugins.multiverse.core.config.node.functions;

import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.config.node.ConfigNode;

/**
 * Handler called when a node's value is loaded. See {@link ConfigNode#onLoad(Object)}.
 *
 * @since 5.4
 */
@ApiStatus.AvailableSince("5.4")
@FunctionalInterface
public interface NodeLoadCallback<T> {
    /**
     * Called when the value of a node is loaded.
     *
     * @param value The loaded value of the node.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    void run(T value);

    /**
     * Chains another {@link NodeLoadCallback} to be executed after this one.
     *
     * @param after The callback to execute after this one.
     * @return A new {@link NodeLoadCallback} that executes both callbacks in order.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    default NodeLoadCallback<T> then(NodeLoadCallback<T> after) {
        return (T value) -> {
            this.run(value);
            after.run(value);
        };
    }
}
