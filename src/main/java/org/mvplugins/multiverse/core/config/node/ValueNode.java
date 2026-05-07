package org.mvplugins.multiverse.core.config.node;

import java.util.Collection;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.config.handle.BaseConfigurationHandle;
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
     * @return The aliases of this node, or an empty array if the node has no aliases.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    default @NotNull String[] getAliases() {
        return Strings.EMPTY_ARRAY;
    }

    /**
     * Gets the default value for this node.
     *
     * <p>The returned value is used when no explicit value is present or when deserialization falls back to the
     * node's default. Implementations may return {@code null} to indicate that no default value is available.
     *
     * @return The default value of the node, or {@code null} if none is configured.
     */
    @Nullable T getDefaultValue();

    /**
     * Suggests possible string values for this node.
     *
     * <p>The returned values are based on the current partial user input and should be suitable for command-line
     * tab completion or other interactive hints.
     *
     * @param input The current partial user input, or {@code null} if no input has been typed yet.
     * @return A collection of possible string values.
     */
    @NotNull Collection<String> suggest(@Nullable String input);

    /**
     * Suggests possible string values for this node using sender context.
     *
     * <p>Implementations may use the sender's name, permissions, or player location to tailor the returned
     * suggestions. If no sender-specific logic is available, implementations should fall back to
     * {@link #suggest(String)}.
     *
     * @param sender The sender context.
     * @param input The current partial user input, or {@code null} if no input has been typed yet.
     * @return A collection of possible string values.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    @NotNull Collection<String> suggest(@NotNull CommandSender sender, @Nullable String input);

    /**
     * Parses the given string into a value of type {@link T}.
     *
     * <p>This is typically used for values entered by a user or read from configuration text.
     *
     * @param input The string to parse, or {@code null} if the source value is absent.
     * @return The parsed value, or a failed {@link Try} containing the parsing error.
     */
    @NotNull Try<T> parseFromString(@Nullable String input);

    /**
     * Parses the given string into a value of type {@link T} with context from the sender.
     *
     * <p>Sender-aware implementations may use permissions or other sender-specific information when converting the
     * input. If no sender-aware parser is available, implementations should fall back to
     * {@link #parseFromString(String)}.
     *
     * @param sender The sender context.
     * @param input The string to parse, or {@code null} if the source value is absent.
     * @return The parsed value, or a failed {@link Try} containing the parsing error.
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
     * <p>This method exists for backward compatibility while the preferred API is
     * {@link #serialize(Object)} and {@link #deserialize(Object)}.
     *
     * @return The serializer for this node, or {@code null} if none is configured.
     *
     * @deprecated Use {@link #serialize(Object)} and {@link #deserialize(Object)} instead.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    @Nullable NodeSerializer<T> getSerializer();

    /**
     * Deserializes a raw configuration object into a value of type {@link T}.
     *
     * <p>The input is typically a value read from YAML or another configuration source. Implementations should
     * return a sensible fallback, such as {@link #getDefaultValue()}, when the object cannot be converted.
     *
     * @param object The raw object to deserialize.
     * @return The deserialized value, or {@code null} if the object cannot be converted and no fallback exists.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Nullable T deserialize(@Nullable Object object);

    /**
     * Serializes a value of type {@link T} into a configuration-friendly object.
     *
     * <p>The returned object should be suitable for persistence in a configuration file.
     *
     * @param value The value to serialize.
     * @return The serialized representation, or {@code null} if the value cannot be serialized.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @Nullable Object serialize(@Nullable T value);

    /**
     * Validates the value of this node.
     *
     * @param value The value to validate.
     * @return A successful {@link Try} if the value is valid, or a failed {@link Try} containing the validation
     *         exception if the value is invalid.
     */
    Try<Void> validate(@Nullable T value);

    /**
     * Called when the value of this node is loaded by {@link BaseConfigurationHandle#load()}.
     *
     * <p>This callback is invoked after the value has been deserialized and validated during a configuration load.
     *
     * @param value The loaded value, or {@code null} if the configured value could not be resolved.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    void onLoad(@Nullable T value);

    /**
     * Called when the value of this node is loaded or changed.
     *
     * <p>This callback is shared by both initial load and subsequent updates. When invoked during a load, the
     * {@code oldValue} parameter will be {@code null} and the sender will be the console sender.
     *
     * @param sender The sender who triggered the change. If triggered by loading or no target sender is specified,
     *               it will be the console sender.
     * @param oldValue The previous value, or {@code null} when called during load.
     * @param newValue The new value.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    void onLoadAndChange(@NotNull CommandSender sender, @Nullable T oldValue, @Nullable T newValue);

    /**
     * Called when the value of this node is changed by {@link BaseConfigurationHandle#set(ValueNode, Object)}.
     *
     * <p>This callback is only invoked for explicit updates after the value already exists.
     *
     * @param sender The sender who changed the value, or the console sender if no target sender was specified.
     * @param oldValue The old value.
     * @param newValue The new value.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    void onChange(@NotNull CommandSender sender, @Nullable T oldValue, @Nullable T newValue);

    /**
     * Called when the value of this node is set.
     *
     * <p>This is the legacy, senderless form of {@link #onLoadAndChange(CommandSender, Object, Object)}.
     *
     * @param oldValue The old value.
     * @param newValue The new value.
     *
     * @deprecated Use {@link #onLoadAndChange(CommandSender, Object, Object)} instead.
     */
    @Deprecated(since = "5.4", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    default void onSetValue(@Nullable T oldValue, @Nullable T newValue) {
        onLoadAndChange(Bukkit.getConsoleSender(), oldValue, newValue);
    }
}
