package com.onarandombox.MultiverseCore.configuration.node;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link EnchancedValueNode}.
 * @param <T> The type of the value.
 */
public class MVValueNode<T> extends MVCommentedNode implements EnchancedValueNode<T> {

    /**
     * Creates a new builder for a {@link MVValueNode}.
     *
     * @param path  The path of the node.
     * @param type  The type of the value.
     * @return The new builder.
     * @param <T>   The type of the value.
     */
    public static <T> Builder<T, ? extends Builder> builder(String path, Class<T> type) {
        return new Builder<>(path, type);
    }

    protected final Class<T> type;
    protected final T defaultValue;
    protected final String name;
    protected final BiConsumer<T, T> onSetValue;

    protected MVValueNode(String path, String[] comments, Class<T> type, T defaultValue, String name, BiConsumer<T, T> onSetValue) {
        super(path, comments);
        this.type = type;
        this.defaultValue = defaultValue;
        this.name = name;
        this.onSetValue = onSetValue;
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
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public void onSetValue(T oldValue, T newValue) {
        onSetValue.accept(oldValue, newValue);
    }

    /**
     * Builder for {@link MVValueNode}.
     *
     * @param <T>   The type of the value.
     * @param <B>   The type of the builder.
     */
    public static class Builder<T, B extends Builder<T, B>> extends MVCommentedNode.Builder<B> {

        protected final Class<T> type;
        protected T defaultValue;
        protected String name;
        protected BiConsumer<T, T> onSetValue;

        /**
         * Creates a new builder.
         *
         * @param path  The path of the node.
         * @param type  The type of the value.
         */
        protected Builder(@NotNull String path, @NotNull Class<T> type) {
            super(path);
            this.type = type;
            this.name = path;
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

        public B onSetValue(@Nullable BiConsumer<T, T> onSetValue) {
            this.onSetValue = onSetValue;
            return (B) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MVValueNode<T> build() {
            return new MVValueNode<>(path, comments.toArray(new String[0]), type, defaultValue, name, onSetValue);
        }
    }
}
