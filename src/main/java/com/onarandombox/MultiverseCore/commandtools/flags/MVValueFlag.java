package com.onarandombox.MultiverseCore.commandtools.flags;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class MVValueFlag<T> extends MVFlag {
    public static <T> Builder<T, ?> builder(String key, Class<T> type) {
        return new Builder<>(key, type);
    }

    private Class<T> type;
    private boolean optional;
    private T defaultValue;
    private Function<String, T> context;
    private Supplier<Collection<String>> completion;

    private MVValueFlag(Builder<T, ?> builder) {
        super(builder);
        type = builder.type;
        optional = builder.optional;
        defaultValue = builder.defaultValue;
        context = builder.context;
        completion = builder.completion;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Function<String, T> getContext() {
        return context;
    }

    public Supplier<Collection<String>> getCompletion() {
        return completion;
    }

    public static class Builder<T, S extends Builder<T, S>> extends MVFlag.Builder<S> {
        private final Class<T> type;
        private boolean optional = false;
        private T defaultValue = null;
        private Function<String, T> context = null;
        private Supplier<Collection<String>> completion = null;

        public Builder(String key, Class<T> type) {
            super(key);
            this.type = type;
        }

        public S optional() {
            this.optional = true;
            return (S) this;
        }

        public S defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return (S) this;
        }

        public S context(Function<String, T> context) {
            this.context = context;
            return (S) this;
        }

        public S completion(Supplier<Collection<String>> completion) {
            this.completion = completion;
            return (S) this;
        }

        @Override
        public MVValueFlag<T> build() {
            if (context == null && !String.class.equals(type)) {
                throw new IllegalStateException("Context is required for none-string value flags");
            }
            return new MVValueFlag<>(this);
        }
    }
}
