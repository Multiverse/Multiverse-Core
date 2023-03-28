package com.onarandombox.MultiverseCore.configuration.node;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigNode<T> extends ConfigHeaderNode implements ValueNode<T> {

    /**
     * Creates a new builder for a {@link ConfigNode}.
     *
     * @param path  The path of the node.
     * @param type  The type of the value.
     * @return The new builder.
     * @param <T>   The type of the value.
     */
    public static <T> ConfigNode.Builder<T, ? extends ConfigNode.Builder> builder(String path, Class<T> type) {
        return new ConfigNode.Builder<>(path, type);
    }

    protected final String name;
    protected final Class<T> type;
    protected final T defaultValue;
    protected final Function<T, Boolean> validator;
    protected final BiConsumer<T, T> onSetValue;

    protected ConfigNode(String path, String[] comments, String name, Class<T> type, T defaultValue, Function<T, Boolean> validator, BiConsumer<T, T> onSetValue) {
        super(path, comments);
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.validator = validator;
        this.onSetValue = onSetValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(T value) {
        if (validator != null) {
            return validator.apply(value);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSetValue(T oldValue, T newValue) {
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

        protected String name;
        protected final Class<T> type;
        protected T defaultValue;
        protected Function<T, Boolean> validator;
        protected BiConsumer<T, T> onSetValue;

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
         * Sets the default value for this node.
         *
         * @param defaultValue The default value.
         * @return This builder.
         */
        public B defaultValue(@NotNull T defaultValue) {
            this.defaultValue = defaultValue;
            return (B) this;
        }

        /**
         * Sets the name of this node. Used for identifying the node from user input.
         *
         * @param name The name of this node.
         * @return This builder.
         */
        public B name(@Nullable String name) {
            this.name = name;
            return (B) this;
        }

        public B validator(@Nullable Function<T, Boolean> validator) {
            this.validator = validator;
            return (B) this;
        }

        /**
         * Sets the action to be performed when the value is set.
         *
         * @param onSetValue    The action to be performed.
         * @return This builder.
         */
        public B onSetValue(@Nullable BiConsumer<T, T> onSetValue) {
            this.onSetValue = onSetValue;
            return (B) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ConfigNode<T> build() {
            return new ConfigNode<>(path, comments.toArray(new String[0]), name, type, defaultValue, validator, onSetValue);
        }
    }
}
