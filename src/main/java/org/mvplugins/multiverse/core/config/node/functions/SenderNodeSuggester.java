package org.mvplugins.multiverse.core.config.node.functions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A function that suggests possible values for a node value with sender contextual information.
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
@FunctionalInterface
public interface SenderNodeSuggester extends NodeSuggester {

    /**
     * Suggests possible values for a node value. Generated based on the current user input and sender contextual information.
     *
     * @param sender    The sender context.
     * @param input     The current partial user input
     * @return The possible values.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    @NotNull Collection<String> suggest(@NotNull CommandSender sender, @Nullable String input);

    @Override
    default @NotNull Collection<String> suggest(@Nullable String input) {
        return suggest(Bukkit.getConsoleSender(), input);
    }
}
