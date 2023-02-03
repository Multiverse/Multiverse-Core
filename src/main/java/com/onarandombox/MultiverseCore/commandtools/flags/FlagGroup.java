package com.onarandombox.MultiverseCore.commandtools.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A group of flags.
 */
public class FlagGroup {
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
    private final List<MVFlag> flags;
    private final List<String> keys;
    private final Map<String, MVFlag> keysFlagMap;

    /**
     * Creates a new flag group.
     *
     * @param builder The builder.
     */
    protected FlagGroup(@NotNull Builder builder) {
        name = builder.name;
        flags = builder.flags;
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
     * Get the flags contained within in this group.
     *
     * @return The flags in this group.
     */
    public @NotNull List<MVFlag> getFlags() {
        return flags;
    }

    /**
     * Get the keys of the flags contained within in this group. Does not include aliases.
     *
     * @return The keys of the flags in this group.
     */
    public @NotNull List<String> getKeys() {
        return keys;
    }

    /**
     * Get the keys and flags contained within in this group.
     *
     * @return The keys and flags in this group.
     */
    public @NotNull Map<String, MVFlag>getKeysFlagMap() {
        return keysFlagMap;
    }

    /**
     * Get a flag by its key. Alias keys are supported as well.
     *
     * @param key The key of the flag.
     * @return The flag if found, null otherwise.
     */
    public @Nullable MVFlag getFlagByKey(String key) {
        return keysFlagMap.get(key);
    }

    /**
     * A builder for {@link FlagGroup}.
     */
    public static class Builder {
        private final String name;
        private final List<MVFlag> flags;
        private final List<String> keys;
        private final Map<String, MVFlag> keysFlagMap;

        /**
         * Creates a new builder.
         *
         * @param name The name of the flag group.
         */
        public Builder(@NotNull String name) {
            this.name = name;
            this.flags = new ArrayList<>();
            this.keys = new ArrayList<>();
            this.keysFlagMap = new HashMap<>();
        }

        /**
         * Adds a flag to the group.
         *
         * @param flag The flag to add.
         * @return The builder.
         */
        public @NotNull Builder add(MVFlag flag) {
            flags.add(flag);
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
        public @NotNull FlagGroup build() {
            return new FlagGroup(this);
        }
    }
}
