package com.onarandombox.MultiverseCore.configuration.handle;

import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * Configuration handle for a single configuration section.
 */
public class ConfigurationSectionHandle extends GenericConfigHandle<ConfigurationSection> {
    public static Builder<? extends Builder> builder(@NotNull ConfigurationSection configurationSection) {
        return new Builder<>(configurationSection);
    }

    public ConfigurationSectionHandle(@NotNull ConfigurationSection configurationSection,
                                      @Nullable Logger logger,
                                      @Nullable NodeGroup nodes,
                                      @Nullable ConfigMigrator migrator) {
        super(logger, nodes, migrator);
        this.config = configurationSection;
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
