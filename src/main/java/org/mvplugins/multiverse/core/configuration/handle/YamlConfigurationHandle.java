package org.mvplugins.multiverse.core.configuration.handle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import io.vavr.control.Try;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.node.NodeGroup;

/**
 * Configuration handle for YAML files.
 */
public class YamlConfigurationHandle extends FileConfigurationHandle<YamlConfiguration> {

    /**
     * Creates a new builder for {@link YamlConfigurationHandle}.
     *
     * @param configPath    The path to the config file.
     * @param nodes         The nodes.
     * @return The builder.
     */
    public static @NotNull Builder<? extends Builder> builder(@NotNull Path configPath, @NotNull NodeGroup nodes) {
        return new Builder<>(configPath, nodes);
    }

    protected YamlConfigurationHandle(
            @NotNull Path configPath,
            @Nullable Logger logger,
            @NotNull NodeGroup nodes,
            @Nullable ConfigMigrator migrator) {
        super(configPath, logger, nodes, migrator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadConfigObject() throws IOException, InvalidConfigurationException {
        config = new YamlConfiguration();
        config.load(configFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> save() {
        return Try.run(() -> config.save(configFile));
    }

    /**
     * Builder for {@link YamlConfigurationHandle}.
     *
     * @param <B>   The type of the builder.
     */
    public static class Builder<B extends Builder<B>> extends FileConfigurationHandle.Builder<YamlConfiguration, B> {

        protected Builder(@NotNull Path configPath, @NotNull NodeGroup nodes) {
            super(configPath, nodes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull YamlConfigurationHandle build() {
            return new YamlConfigurationHandle(configPath, logger, nodes, migrator);
        }
    }
}
