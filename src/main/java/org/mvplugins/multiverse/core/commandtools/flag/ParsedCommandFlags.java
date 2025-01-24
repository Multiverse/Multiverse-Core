package org.mvplugins.multiverse.core.commandtools.flag;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Object to contain the results of the flags present and its values.
 */
public class ParsedCommandFlags
{
    public static final ParsedCommandFlags EMPTY = new ParsedCommandFlags();

    private final Map<String, Object> flagValues;

    ParsedCommandFlags() {
        flagValues = new HashMap<>();
    }

    /**
     * Add a flag result to the parsed flags.
     *
     * @param key   The key of the flag.
     * @param value The value of the flag.
     */
    void addFlagResult(@NotNull String key, @Nullable Object value) {
        flagValues.put(key, value);
    }

    /**
     * Check if a flag is present.
     *
     * @param flag  The flag to check.
     * @return True if the flag is present, false otherwise.
     */
    public boolean hasFlag(@NotNull CommandFlag flag) {
        return hasFlag(flag.getKey());
    }

    /**
     * Check if a flag is present.
     *
     * @param key The key of the flag.
     * @return True if the flag is present, false otherwise.
     */
    public boolean hasFlag(@Nullable String key) {
        return this.flagValues.containsKey(key);
    }

    /**
     * Check if a flag is present and has a value.
     *
     * @param key   The key of the flag.
     * @return True if the flag is present and has a value, false otherwise.
     */
    public boolean hasFlagValue(@Nullable String key) {
        return flagValue(key, Object.class) != null;
    }

    /**
     * Get the value of a flag.
     *
     * @param <T>           The type of the value.
     * @param flag  The flag to get the value of.
     * @param type  The type of the value.
     * @return The value of the flag, null if flag does not exist or no value.
     */
    public @Nullable <T> T flagValue(@NotNull CommandFlag flag, @NotNull Class<T> type) {
        return flagValue(flag.getKey(), type);
    }

    /**
     * Get the value of a flag.
     *
     * @param key   The key of the flag to get the value of.
     * @param type  The type of the value.
     * @return The value of the flag, null if flag does not exist or no value.
     */
    public @Nullable <T> T flagValue(@Nullable String key, @NotNull Class<T> type) {
        Object value = this.flagValues.get(key);
        return (T) value;
    }

    /**
     * Get the value of a flag.
     *
     * @param <T>           The type of the value.
     * @param flag          The flag to get the value of.
     * @return The value of the flag, default value if flag does not exist or no value.
     */
    public @Nullable <T> T flagValue(@NotNull CommandValueFlag<T> flag) {
        return flagValue(flag.getKey(), flag.getType());
    }

    /**
     * Get the value of a flag.
     *
     * @param <T>           The type of the value.
     * @param flag          The flag to get the value of.
     * @param defaultValue  The default value if flag does not exist or no value.
     * @return The value of the flag, default value if flag does not exist or no value.
     */
    public @NotNull <T> T flagValue(@NotNull CommandValueFlag<T> flag, @NotNull T defaultValue) {
        return flagValue(flag.getKey(), defaultValue, flag.getType());
    }

    /**
     * Get the value of a flag.
     *
     * @param <T>           The type of the value.
     * @param key           The key of the flag to get the value of.
     * @param defaultValue  The default value if flag does not exist or no value.
     * @param type          The type of the value.
     * @return The value of the flag, default value if flag does not exist or no value.
     */
    public @NotNull <T> T flagValue(@Nullable String key, @NotNull T defaultValue, @NotNull Class<T> type) {
        T value = flagValue(key, type);
        return value != null ? value : defaultValue;
    }
}
