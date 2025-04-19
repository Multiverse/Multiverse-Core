package org.mvplugins.multiverse.core.config.handle;

import io.vavr.control.Try;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.config.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.config.node.NodeGroup;

import java.util.logging.Logger;

public class MemoryConfigurationHandle extends ConfigurationSectionHandle<ConfigurationSection> {

    /**
     * Creates a new builder for a {@link ConfigurationSectionHandle}.
     *
     * @param configurationSection  The configuration section.
     * @param nodes                 The nodes.
     * @return The builder.
     */
    public static MemoryConfigurationHandle.Builder<? extends ConfigurationSectionHandle.Builder<ConfigurationSection, ?>> builder(
            @NotNull ConfigurationSection configurationSection, @NotNull NodeGroup nodes) {
        return new MemoryConfigurationHandle.Builder<>(configurationSection, nodes);
    }

    protected MemoryConfigurationHandle(
            @NotNull ConfigurationSection configurationSection,
            @Nullable Logger logger,
            @NotNull NodeGroup nodes,
            @Nullable ConfigMigrator migrator) {
        super(configurationSection, logger, nodes, migrator);
    }

    @Override
    public Try<Void> save() {
        return Try.run(() -> config = new MemoryConfiguration())
                .flatMap(ignore -> super.save());
    }

    /**
     * Builder for {@link MemoryConfigurationHandle}.
     *
     * @param <B>   The builder type.
     */
    public static class Builder<B extends Builder<B>> extends ConfigurationSectionHandle.Builder<ConfigurationSection, B> {

        protected Builder(@NotNull ConfigurationSection configurationSection, @NotNull NodeGroup nodes) {
            super(configurationSection, nodes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull MemoryConfigurationHandle build() {
            return new MemoryConfigurationHandle(configurationSection, logger, nodes, migrator);
        }
    }
}
