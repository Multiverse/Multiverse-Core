package org.mvplugins.multiverse.core.config.node;

import java.util.Collection;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.config.node.serializer.NodeSerializer;

public interface ValueNode<T> extends Node {

    /**
     * Gets the name of this node. Used for identifying the node from user input. This must be unique within a node group.
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
     * Gets the aliases of this node. Serves as shorter or legacy alternatives the {@link #getName()} and must be
     * unique within a node group.
     *
     * @return The aliases of this node.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    default @NotNull String[] getAliases() {
        return Strings.EMPTY_ARRAY;
    }

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
     * Suggests possible string values for this node. Use contextural information from the sender such as
     * sender name, permissions, or player location for better suggestions.
     *
     * @param sender    The sender context.
     * @param input     The input string.
     * @return A collection of possible string values
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    @NotNull Collection<String> suggest(@NotNull CommandSender sender, @Nullable String input);

    /**
     * Parses the given string into a value of type {@link T}. Used for property set by user input.
     *
     * @param input The string to parse.
     * @return The parsed value, or given exception if parsing failed.
     */
    @NotNull Try<T> parseFromString(@Nullable String input);

    /**
     * Parses the given string into a value of type {@link T} with context from the sender.
     * Used for property set by user input.
     *
     * @param sender    The sender context.
     * @param input     The string to parse.
     * @return The parsed value, or given exception if parsing failed.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    default @NotNull Try<T> parseFromString(@NotNull CommandSender sender, @Nullable String input) {
        return parseFromString(input);
    }

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
