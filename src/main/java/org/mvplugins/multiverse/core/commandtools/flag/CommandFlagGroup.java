package org.mvplugins.multiverse.core.commandtools.flag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A group of flags.
 */
public class CommandFlagGroup {
    /**
     * A builder for a flag group.
     *
     * @param name The name of the group.
     * @return The builder.
     */
    public static @NotNull Builder builder(@NotNull String name) {
        return new Builder(name);
    }

    private final String name;
    private final List<String> keys;
    private final Map<String, CommandFlag> keysFlagMap;

    /**
     * Creates a new flag group.
     *
     * @param builder The builder.
     */
    protected CommandFlagGroup(@NotNull Builder builder) {
        name = builder.name;
        keys = builder.keys;
        keysFlagMap = builder.keysFlagMap;
    }

    /**
     * Get the name of this group.
     *
     * @return The name of this group.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Check if this group contains a flag with the given key. Works with alias keys.
     *
     * @param key The key to check.
     * @return True if the group contains a flag with the given key, false otherwise.
     */
    public boolean hasKey(@Nullable String key) {
        return keysFlagMap.containsKey(key);
    }

    /**
     * Get the remaining keys after the given flags have been removed. Works with alias keys.
     *
     * @param flags The flags to remove.
     * @return The remaining keys.
     */
    public @NotNull Set<String> getRemainingKeys(@NotNull String[] flags) {
        Set<String> keysRemaining = new HashSet<>(this.keys);
        for (String flag : flags) {
            CommandFlag mvFlag = this.getFlagByKey(flag);
            if (mvFlag != null) {
                keysRemaining.remove(mvFlag.getKey());
            }
        }
        return keysRemaining;
    }

    /**
     * Get a flag by its key. Alias keys are supported as well.
     *
     * @param key The key of the flag.
     * @return The flag if found, null otherwise.
     */
    public @Nullable CommandFlag getFlagByKey(String key) {
        return keysFlagMap.get(key);
    }

    /**
     * A builder for {@link CommandFlagGroup}.
     */
    public static class Builder {
        private final String name;
        private final List<String> keys;
        private final Map<String, CommandFlag> keysFlagMap;

        /**
         * Creates a new builder.
         *
         * @param name The name of the flag group.
         */
        public Builder(@NotNull String name) {
            this.name = name;
            this.keys = new ArrayList<>();
            this.keysFlagMap = new HashMap<>();
        }

        /**
         * Adds a flag to the group.
         *
         * @param flag The flag to add.
         * @return The builder.
         */
        public @NotNull Builder add(CommandFlag flag) {
            keys.add(flag.getKey());
            keysFlagMap.put(flag.getKey(), flag);
            flag.getAliases().forEach((alias) -> keysFlagMap.put(alias, flag));
            return this;
        }

        /**
         * Builds the flag group.
         *
         * @return The flag group.
         */
        public @NotNull CommandFlagGroup build() {
            return new CommandFlagGroup(this);
        }
    }
}
