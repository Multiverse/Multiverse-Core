package org.mvplugins.multiverse.core.commandtools.flag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a flag.
 */
public class CommandFlag {
    /**
     * A builder for a flag.
     *
     * @param key The key for the new flag.
     * @return The builder.
     */
    public static @NotNull Builder<?> builder(@NotNull String key){
        return new Builder<>(key);
    }

    private final String key;
    private final List<String> aliases;

    /**
     * Creates a new flag.
     *
     * @param key     The key for the new flag.
     * @param aliases The aliases that also refer to this flag.
     */
    protected CommandFlag(@NotNull String key, @NotNull List<String> aliases) {
        this.key = key;
        this.aliases = aliases;
    }

    /**
     * Get the key of this flag.
     *
     * @return The key of this flag.
     */
    public @NotNull String getKey() {
        return key;
    }

    /**
     * Get the aliases of this flag.
     *
     * @return The aliases of this flag.
     */
    public @NotNull List<String> getAliases() {
        return aliases;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Builder{"
                + "key='" + key + '\''
                + ", aliases=" + aliases
                + '}';
    }

    /**
     * A builder for a flag.
     *
     * @param <S> The type of the builder.
     */
    public static class Builder<S extends Builder<?>> {
        protected final String key;
        protected final List<String> aliases;

        /**
         * Create a new builder.
         *
         * @param key The key for the new flag.
         */
        public Builder(@NotNull String key) {
            this.key = key;
            aliases = new ArrayList<>();
        }

        /**
         * Add one or more alias to the flag.
         *
         * @param alias The alias to add.
         * @return The builder.
         */
        public @NotNull S addAlias(@NotNull String...alias){
            Collections.addAll(this.aliases, alias);
            return (S) this;
        }

        /**
         * Build the flag.
         *
         * @return The flag.
         */
        public @NotNull CommandFlag build(){
            return new CommandFlag(key, aliases);
        }
    }
}
