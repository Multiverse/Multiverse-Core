package org.mvplugins.multiverse.core.utils.message;

import io.vavr.control.Either;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Captures string replacements for {@link Message}s.
 */
public final class MessageReplacement {

    /**
     * Creates a replacement key for the given key string.
     *
     * @param key The string to replace
     * @return A new replacement key which can be used to create a replacement
     */
    @Contract(value = "_ -> new", pure = true)
    public static MessageReplacement.Key replace(@NotNull String key) {
        return new MessageReplacement.Key(key);
    }

    /**
     * A replacement key that maps to a value it can be replaced with.
     */
    public static final class Key {

        private final @NotNull String key;

        private Key(@NotNull String key) {
            this.key = key;
        }

        /**
         * Creates a replacement for this key.
         *
         * @param replacement The replacement message
         * @return A new message replacement
         */
        @Contract(value = "_ -> new", pure = true)
        public MessageReplacement with(@NotNull Message replacement) {
            return new MessageReplacement(key, replacement);
        }

        /**
         * Creates a replacement for this key.
         *
         * @param replacement The replacement value, if null it will be replaced with a string equal to "null"
         * @return A new message replacement
         */
        @Contract(value = "_ -> new", pure = true)
        public MessageReplacement with(@Nullable Object replacement) {
            return new MessageReplacement(key, replacement);
        }
    }

    private final @NotNull String key;
    private final @NotNull Either<String, Message> replacement;

    private MessageReplacement(@NotNull String key, @NotNull Message replacement) {
        this.key = key;
        this.replacement = Either.right(replacement);
    }

    private MessageReplacement(@NotNull String key, @Nullable Object replacement) {
        this.key = key;
        this.replacement = Either.left(String.valueOf(replacement));
    }

    /**
     * Gets the string to be replaced.
     *
     * @return The key
     */
    public @NotNull String getKey() {
        return key;
    }

    /**
     * Gets the replacement value.
     *
     * @return The replacement
     */
    public @NotNull Either<String, Message> getReplacement() {
        return replacement;
    }
}
