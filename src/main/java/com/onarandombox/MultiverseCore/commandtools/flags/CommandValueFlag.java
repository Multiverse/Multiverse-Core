package com.onarandombox.MultiverseCore.commandtools.flags;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a flag with a value.
 *
 * @param <T> The type of the value.
 */
public class CommandValueFlag<T> extends CommandFlag {
    /**
     * A builder for a flag.
     *
     * @param key  The key for the new flag.
     * @param type The type of the value.
     * @return The builder.
     */
    public static @NotNull <T> Builder<T, ?> builder(@NotNull String key, @NotNull Class<T> type) {
        return new Builder<>(key, type);
    }

    private final Class<T> type;
    private final boolean optional;
    private final T defaultValue;
    private final Function<String, T> context;
    private final Supplier<Collection<String>> completion;

    /**
     * Creates a new flag.
     *
     * @param builder The builder.
     */
    protected CommandValueFlag(@NotNull Builder<T, ?> builder) {
        super(builder);
        type = builder.type;
        optional = builder.optional;
        defaultValue = builder.defaultValue;
        context = builder.context;
        completion = builder.completion;
    }

    /**
     * Get the type of the value.
     *
     * @return The type of the value.
     */
    public @NotNull Class<T> getType() {
        return type;
    }

    /**
     * Check if it is optional for users to specify a value.
     *
     * @return True if the value is optional, false otherwise.
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Get the default value. May be null.
     *
     * @return The default value.
     */
    public @Nullable T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the context. May be null for {@link String} value type.
     *
     * @return The context.
     */
    public @Nullable Function<String, T> getContext() {
        return context;
    }

    /**
     * Get the completion. May be null.
     *
     * @return The completion.
     */
    public @Nullable Supplier<Collection<String>> getCompletion() {
        return completion;
    }

    /**
     * A builder for a flag.
     *
     * @param <T> The type of the value.
     * @param <S> The type of the builder.
     */
    public static class Builder<T, S extends Builder<T, S>> extends CommandFlag.Builder<S> {
        private final Class<T> type;
        private boolean optional = false;
        private T defaultValue = null;
        private Function<String, T> context = null;
        private Supplier<Collection<String>> completion = null;

        /**
         * Create a new builder.
         *
         * @param key  The key for the new flag.
         * @param type The type of the value.
         */
        public Builder(@NotNull String key, @NotNull Class<T> type) {
            super(key);
            this.type = type;
        }

        /**
         * Set the flag as optional for users to specify a value.
         *
         * @return The builder.
         */
        public @NotNull S optional() {
            this.optional = true;
            return (S) this;
        }

        /**
         * Set the default value. Used if optional is true and user does not specify a value.
         *
         * @param defaultValue The default value.
         * @return The builder.
         */
        public @NotNull S defaultValue(@NotNull T defaultValue) {
            this.defaultValue = defaultValue;
            return (S) this;
        }

        /**
         * Set the context callback for parsing string into value type.
         *
         * @param context The context.
         * @return The builder.
         */
        public @NotNull S context(@NotNull Function<String, T> context) {
            this.context = context;
            return (S) this;
        }

        /**
         * Set the completion callback for autocomplete.
         *
         * @param completion The completion.
         * @return The builder.
         */
        public @NotNull S completion(@NotNull Supplier<Collection<String>> completion) {
            this.completion = completion;
            return (S) this;
        }

        /**
         * Build the flag.
         *
         * @return The flag.
         */
        @Override
        public @NotNull CommandFlag build() {
            if (context == null && !String.class.equals(type)) {
                throw new IllegalStateException("Context is required for none-string value flags");
            }
            return new CommandValueFlag<>(this);
        }
    }
}
