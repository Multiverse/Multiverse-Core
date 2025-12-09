package org.mvplugins.multiverse.core.config.node;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.config.node.functions.NodeChangeCallback;
import org.mvplugins.multiverse.core.config.node.functions.NodeValueCallback;
import org.mvplugins.multiverse.core.config.node.functions.SenderNodeChangeCallback;
import org.mvplugins.multiverse.core.config.node.functions.SenderNodeStringParser;
import org.mvplugins.multiverse.core.config.node.functions.SenderNodeSuggester;
import org.mvplugins.multiverse.core.config.node.serializer.DefaultSerializerProvider;
import org.mvplugins.multiverse.core.config.node.functions.DefaultStringParserProvider;
import org.mvplugins.multiverse.core.config.node.functions.DefaultSuggesterProvider;
import org.mvplugins.multiverse.core.config.node.serializer.NodeSerializer;
import org.mvplugins.multiverse.core.config.node.functions.NodeStringParser;
import org.mvplugins.multiverse.core.config.node.functions.NodeSuggester;
import org.mvplugins.multiverse.core.config.handle.StringPropertyHandle;

/**
 * A node that contains a value.
 *
 * @param <T>   The type of the value.
 */
public class ConfigNode<T> extends ConfigHeaderNode implements ValueNode<T> {

    /**
     * Creates a new builder for a {@link ConfigNode}.
     *
     * @param path  The path of the node.
     * @param type  The type of the value.
     * @param <T>   The type of the value.
     * @return The new builder.
     */
    public static @NotNull <T> ConfigNode.Builder<T, ? extends ConfigNode.Builder<T, ?>> builder(
            @NotNull String path,
            @NotNull Class<T> type) {
        return new ConfigNode.Builder<>(path, type);
    }

    protected final @Nullable String name;
    protected final @NotNull Class<T> type;
    protected final @NotNull String[] aliases;
    protected @Nullable Supplier<T> defaultValue;
    protected @Nullable NodeSuggester suggester;
    protected @Nullable NodeStringParser<T> stringParser;
    protected @Nullable NodeSerializer<T> serializer;
    protected @Nullable Function<T, Try<Void>> validator;
    protected @Nullable NodeValueCallback<T> onLoad;
    protected @Nullable NodeChangeCallback<T> onLoadAndChange;
    protected @Nullable NodeChangeCallback<T> onChange;

    protected ConfigNode(
            @NotNull String path,
            @NotNull String[] comments,
            @Nullable String name,
            @NotNull Class<T> type,
            @NotNull String[] aliases,
            @Nullable Supplier<T> defaultValue,
            @Nullable NodeSuggester suggester,
            @Nullable NodeStringParser<T> stringParser,
            @Nullable NodeSerializer<T> serializer,
            @Nullable Function<T, Try<Void>> validator,
            @Nullable NodeValueCallback<T> onLoad,
            @Nullable NodeChangeCallback<T> onLoadAndChange,
            @Nullable NodeChangeCallback<T> onChange) {
        super(path, comments);
        this.name = name;
        this.type = type;
        this.aliases = aliases;
        this.defaultValue = defaultValue;
        this.suggester = (suggester != null)
                ? suggester
                : DefaultSuggesterProvider.getDefaultSuggester(type);
        this.stringParser = (stringParser != null)
                ? stringParser
                : DefaultStringParserProvider.getDefaultStringParser(type);
        this.serializer = (serializer != null)
                ? serializer
                : DefaultSerializerProvider.getDefaultSerializer(type);
        this.validator = validator;
        this.onLoad = onLoad;
        this.onLoadAndChange = onLoadAndChange;
        this.onChange = onChange;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Option<String> getName() {
        return Option.of(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Class<T> getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String[] getAliases() {
        return aliases;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable T getDefaultValue() {
        if (defaultValue != null) {
            return defaultValue.get();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<String> suggest(@Nullable String input) {
        if (suggester != null) {
            return suggester.suggest(input);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSender sender, @Nullable String input) {
        if (suggester != null && suggester instanceof SenderNodeSuggester senderSuggester) {
            return senderSuggester.suggest(sender, input);
        }
        return suggest(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Try<T> parseFromString(@Nullable String input) {
        if (stringParser != null) {
            return stringParser.parse(input, type);
        }
        return Try.failure(new UnsupportedOperationException("No string parser for type " + type.getName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Try<T> parseFromString(@NotNull CommandSender sender, @Nullable String input) {
        if (stringParser != null && stringParser instanceof SenderNodeStringParser<T> senderStringParser) {
            return senderStringParser.parse(sender, input, type);
        }
        return parseFromString(input);
    }

    /**
     * {@inheritDoc}
     */
    public @Nullable NodeSerializer<T> getSerializer() {
        return serializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> validate(@Nullable T value) {
        if (validator != null) {
            return validator.apply(value);
        }
        return Try.success(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoad(@Nullable T value) {
        if (onLoad != null) {
            onLoad.run(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadAndChange(@NotNull CommandSender sender, @Nullable T oldValue, @Nullable T newValue) {
        if (onLoadAndChange != null) {
            onLoadAndChange.run(sender, oldValue, newValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChange(@NotNull CommandSender sender, @Nullable T oldValue, @Nullable T newValue) {
        if (onChange != null) {
            onChange.run(sender, oldValue, newValue);
        }
    }

    /**
     * Builder for {@link ConfigNode}.
     *
     * @param <T>   The type of the value.
     * @param <B>   The type of the builder.
     */
    public static class Builder<T, B extends ConfigNode.Builder<T, B>> extends ConfigHeaderNode.Builder<B> {

        protected @Nullable String name;
        protected @NotNull final Class<T> type;
        protected @NotNull String[] aliases = Strings.EMPTY_ARRAY;
        protected @Nullable Supplier<T> defaultValue;
        protected @Nullable NodeSuggester suggester;
        protected @Nullable NodeStringParser<T> stringParser;
        protected @Nullable NodeSerializer<T> serializer;
        protected @Nullable Function<T, Try<Void>> validator;
        protected @Nullable NodeValueCallback<T> onLoad;
        protected @Nullable NodeChangeCallback<T> onLoadAndChange;
        protected @Nullable NodeChangeCallback<T> onChange;

        /**
         * Creates a new builder.
         *
         * @param path  The path of the node.
         * @param type  The type of the value.
         */
        protected Builder(@NotNull String path, @NotNull Class<T> type) {
            super(path);
            this.name = path;
            this.type = type;
        }

        /**
         * Gets the path of this node.
         *
         * @return The path of this node.
         */
        public @NotNull String path() {
            return path;
        }

        /**
         * Sets the default value for this node.
         *
         * @param defaultValue The default value.
         * @return This builder.
         */
        public @NotNull B defaultValue(@NotNull T defaultValue) {
            this.defaultValue = () -> defaultValue;
            return self();
        }

        /**
         * Sets the default value for this node.
         *
         * @param defaultValue The default value supplier.
         * @return This builder.
         */
        public @NotNull B defaultValue(@NotNull Supplier<T> defaultValue) {
            this.defaultValue = defaultValue;
            return self();
        }

        /**
         * Sets the name of this node. Used for identifying the node from user input.
         *
         * @param name The name of this node.
         * @return This builder.
         */
        public @NotNull B name(@Nullable String name) {
            this.name = name;
            return self();
        }

        /**
         * Gets the name of this node. Used for identifying the node from user input.
         *
         * @return The name of this node, or {@code null} if the node has no name.
         */
        public @Nullable String name() {
            return name;
        }

        /**
         * Sets the name as null, and will not be shown in {@link StringPropertyHandle} as a property.
         *
         * @return This builder.
         */
        public @NotNull B hidden() {
            return name(null);
        }

        /**
         * Sets the aliases for this node. Aliases are alternative identifiers for referencing the node.
         *
         * @param aliases The aliases to set for this node.
         * @return This builder.
         *
         * @since 5.1
         */
        @ApiStatus.AvailableSince("5.1")
        public @NotNull B aliases(@NotNull String... aliases) {
            this.aliases = aliases;
            return self();
        }

        /**
         * Sets the suggester for this node.
         *
         * @param suggester The suggester for this node.
         * @return This builder.
         */
        public @NotNull B suggester(@NotNull NodeSuggester suggester) {
            this.suggester = suggester;
            return self();
        }

        /**
         * Sets the suggester for this node with sender context.
         *
         * @param suggester The suggester for this node.
         * @return This builder.
         *
         * @since 5.1
         */
        @ApiStatus.AvailableSince("5.1")
        public @NotNull B suggester(@NotNull SenderNodeSuggester suggester) {
            this.suggester = suggester;
            return self();
        }

        /**
         * Sets the string parser for this node.
         *
         * @param stringParser  The string parser for this node.
         * @return This builder.
         */
        public @NotNull B stringParser(@NotNull NodeStringParser<T> stringParser) {
            this.stringParser = stringParser;
            return self();
        }

        /**
         * Sets the string parser for this node with sender context.
         *
         * @param stringParser  The string parser for this node.
         * @return This builder.
         *
         * @since 5.1
         */
        @ApiStatus.AvailableSince("5.1")
        public @NotNull B stringParser(@NotNull SenderNodeStringParser<T> stringParser) {
            this.stringParser = stringParser;
            return self();
        }

        /**
         * Sets the serializer for this node.
         *
         * @param serializer    The serializer for this node.
         * @return This builder.
         */
        public @NotNull B serializer(@NotNull NodeSerializer<T> serializer) {
            this.serializer = serializer;
            return self();
        }

        /**
         * Sets the validator for this node.
         *
         * @param validator The validator for this node.
         * @return This builder.
         */
        public @NotNull B validator(@NotNull Function<T, Try<Void>> validator) {
            this.validator = validator;
            return self();
        }

        /**
         * Sets the action to be performed when the value is loaded.
         *
         * @param onLoad    The action to be performed.
         * @return This builder.
         */
        @ApiStatus.AvailableSince("5.4")
        public @NotNull B onLoad(@NotNull NodeValueCallback<T> onLoad) {
            this.onLoad = this.onLoad == null ? onLoad : this.onLoad.then(onLoad);
            return self();
        }

        /**
         * Sets the action to be performed when the value is loaded and changed.
         *
         * @param onLoadAndChange    The action to be performed.
         * @return This builder.
         *
         * @since 5.4
         */
        @ApiStatus.AvailableSince("5.4")
        public @NotNull B onLoadAndChange(@NotNull NodeChangeCallback<T> onLoadAndChange) {
            this.onLoadAndChange = this.onLoadAndChange == null ? onLoadAndChange : this.onLoadAndChange.then(onLoadAndChange);
            return self();
        }

        /**
         * Sets the action to be performed when the value is loaded and changed.
         *
         * @param onLoadAndChange    The action to be performed.
         * @return This builder.
         *
         * @since 5.4
         */
        @ApiStatus.AvailableSince("5.4")
        public @NotNull B onLoadAndChange(@NotNull SenderNodeChangeCallback<T> onLoadAndChange) {
            return onLoadAndChange((NodeChangeCallback<T>) onLoadAndChange);
        }

        /**
         * Sets the action to be performed when the value is changed.
         *
         * @param onChange    The action to be performed.
         * @return This builder.
         *
         * @since 5.4
         */
        @ApiStatus.AvailableSince("5.4")
        public @NotNull B onChange(@NotNull NodeChangeCallback<T> onChange) {
            this.onChange = this.onChange == null ? onChange : this.onChange.then(onChange);
            return self();
        }

        /**
         * Sets the action to be performed when the value is changed.
         *
         * @param onChange    The action to be performed.
         * @return This builder.
         *
         * @since 5.4
         */
        @ApiStatus.AvailableSince("5.4")
        public @NotNull B onChange(@NotNull SenderNodeChangeCallback<T> onChange) {
            return onChange((NodeChangeCallback<T>) onChange);
        }

        /**
         * Sets the action to be performed when the value is set.
         *
         * @param onSetValue    The action to be performed.
         * @return This builder.
         *
         * @deprecated Use {@link #onLoadAndChange(NodeChangeCallback)} instead.
         */
        @Deprecated(since = "5.4", forRemoval = true)
        public @NotNull B onSetValue(@NotNull BiConsumer<T, T> onSetValue) {
            return onLoadAndChange(onSetValue::accept);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull ConfigNode<T> build() {
            return new ConfigNode<>(path, comments.toArray(new String[0]),
                    name, type, aliases, defaultValue, suggester, stringParser, serializer, validator,
                    onLoad, onLoadAndChange, onChange);
        }
    }
}
