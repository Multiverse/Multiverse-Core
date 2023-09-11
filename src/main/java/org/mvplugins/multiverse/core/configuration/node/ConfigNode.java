package org.mvplugins.multiverse.core.configuration.node;

import java.util.function.BiConsumer;
import java.util.function.Function;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A node that contains a value.
 * @param <T>   The type of the value.
 */
public class ConfigNode<T> extends ConfigHeaderNode implements ValueNode<T> {

    /**
     * Creates a new builder for a {@link ConfigNode}.
     *
     * @param path  The path of the node.
     * @param type  The type of the value.
     * @return The new builder.
     * @param <T>   The type of the value.
     */
    public static @NotNull <T> ConfigNode.Builder<T, ? extends ConfigNode.Builder<T, ?>> builder(
            @NotNull String path,
            @NotNull Class<T> type
    ) {
        return new ConfigNode.Builder<>(path, type);
    }

    protected final @Nullable String name;
    protected final @NotNull Class<T> type;
    protected final @Nullable T defaultValue;
    protected final @Nullable NodeSerializer<T> serializer;
    protected final @Nullable Function<T, Try<Void>> validator;
    protected final @Nullable BiConsumer<T, T> onSetValue;

    protected ConfigNode(
            @NotNull String path,
            @NotNull String[] comments,
            @Nullable String name,
            @NotNull Class<T> type,
            @Nullable T defaultValue,
            @Nullable NodeSerializer<T> serializer,
            @Nullable Function<T, Try<Void>> validator,
            @Nullable BiConsumer<T, T> onSetValue
    ) {
        super(path, comments);
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.serializer = serializer;
        this.validator = validator;
        this.onSetValue = onSetValue;
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
    public @Nullable T getDefaultValue() {
        return defaultValue;
    }

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
    public void onSetValue(@Nullable T oldValue, @Nullable T newValue) {
        if (onSetValue != null) {
            onSetValue.accept(oldValue, newValue);
        }
    }

    /**
     * Builder for {@link ConfigNode}.
     *
     * @param <T>   The type of the value.
     * @param <B>   The type of the builder.
     */
    public static class Builder<T, B extends ConfigNode.Builder<T, B>> extends ConfigHeaderNode.Builder<B> {
        private static final NodeSerializer<?> ENUM_NODE_SERIALIZER = new EnumNodeSerializer<>();

        protected @Nullable String name;
        protected @NotNull final Class<T> type;
        protected @Nullable T defaultValue;
        protected @Nullable NodeSerializer<T> serializer;
        protected @Nullable Function<T, Try<Void>> validator;
        protected @Nullable BiConsumer<T, T> onSetValue;

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
            if (type.isEnum()) {
                this.serializer = (NodeSerializer<T>) ENUM_NODE_SERIALIZER;
            }
        }

        /**
         * Sets the default value for this node.
         *
         * @param defaultValue The default value.
         * @return This builder.
         */
        public @NotNull B defaultValue(@NotNull T defaultValue) {
            this.defaultValue = defaultValue;
            return (B) this;
        }

        /**
         * Sets the name of this node. Used for identifying the node from user input.
         *
         * @param name The name of this node.
         * @return This builder.
         */
        public @NotNull B name(@Nullable String name) {
            this.name = name;
            return (B) this;
        }

        public @NotNull B serializer(@NotNull NodeSerializer<T> serializer) {
            this.serializer = serializer;
            return (B) this;
        }

        public @NotNull B validator(@NotNull Function<T, Try<Void>> validator) {
            this.validator = validator;
            return (B) this;
        }

        /**
         * Sets the action to be performed when the value is set.
         *
         * @param onSetValue    The action to be performed.
         * @return This builder.
         */
        public @NotNull B onSetValue(@NotNull BiConsumer<T, T> onSetValue) {
            this.onSetValue = onSetValue;
            return (B) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull ConfigNode<T> build() {
            return new ConfigNode<>(path, comments.toArray(new String[0]), name, type, defaultValue, serializer, validator, onSetValue);
        }
    }
}
