package org.mvplugins.multiverse.core.config.node.functions;

import org.jetbrains.annotations.ApiStatus;

/**
 * Handler called when an action is performed on a node such as loading.
 *
 * @since 5.4
 */
@ApiStatus.AvailableSince("5.4")
@FunctionalInterface
public interface NodeValueCallback<T> {
    /**
     * Called when an action is performed on a node.
     *
     * @param value The value of the node.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    void run(T value);

    /**
     * Chains another {@link NodeValueCallback} to be executed after this one.
     *
     * @param after The callback to execute after this one.
     * @return A new {@link NodeValueCallback} that executes both callbacks in order.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    default NodeValueCallback<T> then(NodeValueCallback<T> after) {
        return (T value) -> {
            this.run(value);
            after.run(value);
        };
    }
}
