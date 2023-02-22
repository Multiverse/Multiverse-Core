package com.onarandombox.MultiverseCore.commandtools.flags;

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

    public ParsedCommandFlags() {
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
     * @param key The key of the flag.
     * @return True if the flag is present, false otherwise.
     */
    public boolean hasFlag(@Nullable String key) {
        return this.flagValues.containsKey(key);
    }

    public boolean hasFlagValue(@Nullable String key) {
        return flagValue(key, Object.class) != null;
    }

    /**
     * Get the value of a flag.
     *
     * @param key The key of the flag.
     * @return The value of the flag, null if flag does not exist or no value.
     */
    public @Nullable <T> T flagValue(@Nullable String key, @NotNull Class<T> type) {
        Object value = this.flagValues.get(key);
        return (T) value;
    }

    public @NotNull <T> T flagValue(@Nullable String key, @NotNull T defaultValue, @NotNull Class<T> type) {
        T value = flagValue(key, type);
        return value != null ? value : defaultValue;
    }
}
