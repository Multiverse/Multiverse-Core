package org.mvplugins.multiverse.core.config.node.functions;

import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A function that parses a string into a node value object of type {@link T} with contextual information from the sender.
 *
 * @param <T>   The type of the object to parse.
 */
@ApiStatus.AvailableSince("5.1")
public interface SenderNodeStringParser<T> extends NodeStringParser<T> {
    /**
     * Parses a string into a node value object of type {@link T} with contextual information from the sender.
     * This ties in with {@link SenderNodeSuggester} that provides suggestions based on the sender context.
     *
     * @param sender    The sender context.
     * @param string    The string to parse.
     * @param type      The type of the object to parse.
     * @return The parsed object, or {@link Try.Failure} if the string could not be parsed.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    @NotNull Try<T> parse(@NotNull CommandSender sender, @Nullable String string, @NotNull Class<T> type);

    @Override
    default @NotNull Try<T> parse(@Nullable String string, @NotNull Class<T> type) {
        return parse(Bukkit.getConsoleSender(), string, type);
    }
}
