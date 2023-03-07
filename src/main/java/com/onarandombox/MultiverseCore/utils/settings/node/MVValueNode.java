package com.onarandombox.MultiverseCore.utils.settings.node;

import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MVValueNode<T> extends MVCommentedNode implements TypedValueNode<T> {

    public static <T> Builder<T, ? extends Builder> builder(String path, Class<T> type) {
        return new Builder<>(path, type);
    }

    protected final Class<T> type;

    private final T defaultValue;

    protected MVValueNode(String path, String[] comments, Class<T> type, T defaultValue) {
        super(path, comments);
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public @NotNull Class<T> getType() {
        return type;
    }

    @Override
    public @Nullable T getDefaultValue() {
        return defaultValue;
    }

    public static class Builder<T, B extends Builder<T, B>> extends MVCommentedNode.Builder<B> {

        protected final Class<T> type;
        protected T defaultValue;

        public Builder(String path, Class<T> type) {
            super(path);
            this.type = type;
        }

        public B defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return (B) this;
        }

        @Override
        public MVValueNode<T> build() {
            return new MVValueNode<>(path, comments.toArray(new String[0]), type, defaultValue);
        }
    }
}
