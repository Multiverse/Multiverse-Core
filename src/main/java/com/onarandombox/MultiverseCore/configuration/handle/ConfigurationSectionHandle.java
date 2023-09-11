package com.onarandombox.MultiverseCore.configuration.handle;

import java.util.logging.Logger;

import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import io.vavr.control.Try;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration handle for a single configuration section.
 */
public class ConfigurationSectionHandle extends GenericConfigHandle<ConfigurationSection> {
    /**
     * Creates a new builder for a {@link ConfigurationSectionHandle}.
     *
     * @param configurationSection  The configuration section.
     * @return The builder.
     */
    public static Builder<? extends Builder> builder(@NotNull ConfigurationSection configurationSection) {
        return new Builder<>(configurationSection);
    }

    protected ConfigurationSectionHandle(@NotNull ConfigurationSection configurationSection,
                                      @Nullable Logger logger,
                                      @Nullable NodeGroup nodes,
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
    public Try<Void> load(@NotNull ConfigurationSection section) {
        this.config = section;
        return load();
    }

    /**
     * Builder for {@link ConfigurationSectionHandle}.
     *
     * @param <B>   The builder type.
     */
    public static class Builder<B extends Builder<B>> extends GenericConfigHandle.Builder<ConfigurationSection, B> {
        private final ConfigurationSection configurationSection;

        protected Builder(@NotNull ConfigurationSection configurationSection) {
            this.configurationSection = configurationSection;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull ConfigurationSectionHandle build() {
            return new ConfigurationSectionHandle(configurationSection, logger, nodes, migrator);
        }
    }
}
