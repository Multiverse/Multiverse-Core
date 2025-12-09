package org.mvplugins.multiverse.core.config.node.functions;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

/**
 * A callback interface for handling changes to configuration node values.
 *
 * @param <T> The type of the node value.
 *
 * @since 5.4
 */
@ApiStatus.AvailableSince("5.4")
@FunctionalInterface
public interface NodeChangeCallback<T> {
    /**
     * Called when the value of a node changes.
     *
     * @param oldValue The old value of the node.
     * @param newValue The new value of the node.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    void run(T oldValue, T newValue);

    /**
     * Called when the value of a node changes, with the sender of the change.
     *
     * @param sender   The sender of the change.
     * @param oldValue The old value of the node.
     * @param newValue The new value of the node.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    default void run(CommandSender sender, T oldValue, T newValue) {
        run(oldValue, newValue);
    }

    /**
     * Chains another {@link NodeChangeCallback} to be executed after this one.
     *
     * @param after The callback to execute after this one.
     * @return A new {@link NodeChangeCallback} that executes both callbacks in order.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    default NodeChangeCallback<T> then(NodeChangeCallback<T> after) {
        return new SenderNodeChangeCallback<T>() {
            @Override
            public void run(CommandSender sender, T oldValue, T newValue) {
                NodeChangeCallback.this.run(sender, oldValue, newValue);
                after.run(sender, oldValue, newValue);
            }

            @Override
            public void run(T oldValue, T newValue) {
                NodeChangeCallback.this.run(oldValue, newValue);
                after.run(oldValue, newValue);
            }
        };
    }
}
