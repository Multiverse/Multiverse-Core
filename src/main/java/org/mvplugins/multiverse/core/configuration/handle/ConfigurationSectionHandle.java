package org.mvplugins.multiverse.core.configuration.handle;

import java.util.logging.Logger;

import io.vavr.control.Try;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.node.NodeGroup;

/**
 * Configuration handle for a single configuration section.
 */
public class ConfigurationSectionHandle<C extends ConfigurationSection> extends BaseConfigurationHandle<C> {
    /**
     * Creates a new builder for a {@link ConfigurationSectionHandle}.
     *
     * @param configurationSection  The configuration section.
     * @param nodes                 The nodes.
     * @return The builder.
     */
    public static <C extends ConfigurationSection> Builder<C, ? extends Builder<C, ?>> builder(
            @NotNull C configurationSection, @NotNull NodeGroup nodes) {
        return new Builder<>(configurationSection, nodes);
    }

    protected ConfigurationSectionHandle(
            @NotNull C configurationSection,
            @Nullable Logger logger,
            @NotNull NodeGroup nodes,
            @Nullable ConfigMigrator migrator) {
        super(logger, nodes, migrator);
        this.config = configurationSection;
    }

    /**
     * Loads the configuration with a new configuration section.
     *
     * @param section  The configuration section.
     * @return Whether the configuration was loaded or its given error.
     */
    public Try<Void> load(@NotNull C section) {
        this.config = section;
        return load();
    }

    /**
     * Builder for {@link ConfigurationSectionHandle}.
     *
     * @param <B>   The builder type.
     */
    public static class Builder<C extends ConfigurationSection, B extends Builder<C, B>> extends BaseConfigurationHandle.Builder<C, B> {
        protected final C configurationSection;

        protected Builder(@NotNull C configurationSection, @NotNull NodeGroup nodes) {
            super(nodes);
            this.configurationSection = configurationSection;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull ConfigurationSectionHandle<C> build() {
            return new ConfigurationSectionHandle<>(configurationSection, logger, nodes, migrator);
        }
    }
}
