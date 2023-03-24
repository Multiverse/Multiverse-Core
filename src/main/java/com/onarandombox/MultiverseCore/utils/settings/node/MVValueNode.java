package com.onarandombox.MultiverseCore.utils.settings.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link NamedValueNode}.
 * @param <T> The type of the value.
 */
public class MVValueNode<T> extends MVCommentedNode implements NamedValueNode<T> {

    public static <T> Builder<T, ? extends Builder> builder(String path, Class<T> type) {
        return new Builder<>(path, type);
    }

    protected final Class<T> type;
    protected final T defaultValue;
    protected final String name;

    protected MVValueNode(String path, String[] comments, Class<T> type, T defaultValue, String name) {
        super(path, comments);
        this.type = type;
        this.defaultValue = defaultValue;
        this.name = name;
    }

    /**
     * {@InheritDoc}
     */
    @Override
    public @NotNull Class<T> getType() {
        return type;
    }

    /**
     * {@InheritDoc}
     */
    @Override
    public @Nullable T getDefaultValue() {
        return defaultValue;
    }

    /**
     * {@InheritDoc}
     */
    @Override
    public @Nullable String getName() {
        return name;
    }

    public static class Builder<T, B extends Builder<T, B>> extends MVCommentedNode.Builder<B> {

        protected final Class<T> type;
        protected T defaultValue;
        private String name;

        public Builder(String path, Class<T> type) {
            super(path);
            this.type = type;
            this.name = path;
        }

        public B defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return (B) this;
        }

        public B name(String name) {
            this.name = name;
            return (B) this;
        }

        @Override
        public MVValueNode<T> build() {
            return new MVValueNode<>(path, comments.toArray(new String[0]), type, defaultValue, name);
        }
    }
}
