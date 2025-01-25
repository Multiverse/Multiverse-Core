package org.mvplugins.multiverse.core.commandtools.flag;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import co.aikar.commands.InvalidCommandArgument;
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
     * @param key   The key for the new flag.
     * @param type  The type of the value.
     * @return The builder.
     */
    public static @NotNull <T> Builder<T, ?> builder(@NotNull String key, @NotNull Class<T> type) {
        return new Builder<>(key, type);
    }

    /**
     * A builder for a flag with enum value.
     *
     * @param key   The key for the new flag.
     * @param type  The type of the value, must be enum.
     * @return The builder.
     */
    public static @NotNull <T extends Enum<T>> EnumBuilder<T, ?> enumBuilder(@NotNull String key, @NotNull Class<T> type) {
        return new EnumBuilder<>(key, type);
    }

    private final Class<T> type;
    private final boolean optional;
    private final T defaultValue;
    private final Function<String, T> context;
    private final Function<String, Collection<String>>  completion;

    /**
     * Creates a new flag.
     *
     * @param key               The key for the new flag.
     * @param aliases           The aliases that also refer to this flag.
     * @param type              The type of the value.
     * @param optional          Allow for flag without value.
     * @param defaultValue      The default value if optional is true and user does not specify a value.
     * @param context           Function to parse string into value type.
     * @param completion        Function to get completion for this flag.
     */
    protected CommandValueFlag(
            @NotNull String key,
            @NotNull List<String> aliases,
            @NotNull Class<T> type,
            boolean optional,
            @Nullable T defaultValue,
            @Nullable Function<String, T> context,
            @Nullable Function<String, Collection<String>>  completion
    ) {
        super(key, aliases);
        this.type = type;
        this.optional = optional;
        this.defaultValue = defaultValue;
        this.context = context;
        this.completion = completion;
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
    public @Nullable Function<String, Collection<String>> getCompletion() {
        return completion;
    }

    /**
     * A builder for a flag.
     *
     * @param <T> The type of the value.
     * @param <S> The type of the builder.
     */
    public static class Builder<T, S extends Builder<T, S>> extends CommandFlag.Builder<S> {
        protected final Class<T> type;
        protected boolean optional = false;
        protected T defaultValue = null;
        protected Function<String, T> context = null;
        protected Function<String, Collection<String>> completion = null;

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
         * @param completion The completion. Input is the current input string, and output is the list of suggestions.
         * @return The builder.
         */
        public @NotNull S completion(@NotNull Function<String, Collection<String>>  completion) {
            this.completion = completion;
            return (S) this;
        }

        /**
         * Build the flag.
         *
         * @return The flag.
         */
        @Override
        public @NotNull CommandValueFlag<T> build() {
            if (context == null && !String.class.equals(type)) {
                throw new IllegalStateException("Context is required for non-string value flags");
            }
            return new CommandValueFlag<>(key, aliases, type, optional, defaultValue, context, completion);
        }
    }

    /**
     * Specific builder for a flag with enum value.
     *
     * @param <T> The type of the value.
     * @param <S> The type of the builder.
     */
    public static class EnumBuilder<T extends Enum<T>, S extends EnumBuilder<T, S>> extends CommandFlag.Builder<S> {
        protected final Class<T> type;
        protected boolean optional = false;
        protected T defaultValue = null;
        protected Function<String, T> context = null;
        protected Function<String, Collection<String>>  completion = null;

        public EnumBuilder(@NotNull String key, @NotNull Class<T> type) {
            super(key);
            this.type = type;
            setEnumContext();
            setEnumCompletion();
        }

        private void setEnumContext() {
            this.context = (String value) -> {
                try {
                    return Enum.valueOf(type, value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidCommandArgument("Invalid value for argument " + key + ": " + value);
                }
            };
        }

        private void setEnumCompletion() {
            List<String> types = Arrays.stream(type.getEnumConstants())
                    .map(typeClass -> typeClass.name().toLowerCase())
                    .toList();

            this.completion = (input) -> types;
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
         * Build the flag.
         *
         * @return The flag.
         */
        @Override
        public @NotNull CommandValueFlag<T> build() {
            return new CommandValueFlag<>(key, aliases, type, optional, defaultValue, context, completion);
        }
    }
}
