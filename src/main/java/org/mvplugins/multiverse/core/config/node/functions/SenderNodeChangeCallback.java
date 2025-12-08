package org.mvplugins.multiverse.core.config.node.functions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

/**
 * A callback interface for handling changes to configuration node values with contextual information from the sender.
 *
 * @param <T> The type of the node value.
 *
 * @since 5.4
 */
@ApiStatus.AvailableSince("5.4")
@FunctionalInterface
public interface SenderNodeChangeCallback<T> extends NodeChangeCallback<T> {
    /**
     * {@inheritDoc}
     */
    @ApiStatus.AvailableSince("5.4")
    void run(CommandSender sender, T oldValue, T newValue);

    /**
     * {@inheritDoc}
     */
    @ApiStatus.AvailableSince("5.4")
    @Override
    default void run(T oldValue, T newValue) {
        run(Bukkit.getConsoleSender(), oldValue, newValue);
    }
}
