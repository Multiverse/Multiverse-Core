package com.onarandombox.MultiverseCore.configuration.handle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import com.onarandombox.MultiverseCore.configuration.node.ValueNode;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration handle for YAML files.
 */
public class YamlConfigHandle extends FileConfigHandle<YamlConfiguration> {

    /**
     * Creates a new builder for {@link YamlConfigHandle}.
     *
     * @param configPath    The path to the config file.
     * @return The builder.
     */
    public static @NotNull Builder<? extends Builder> builder(@NotNull Path configPath) {
        return new Builder<>(configPath);
    }

    protected YamlConfigHandle(@NotNull Path configPath, @Nullable Logger logger, @Nullable NodeGroup nodes, @Nullable ConfigMigrator migrator) {
        super(configPath, logger, nodes, migrator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean loadConfigObject() {
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Builder for {@link YamlConfigHandle}.
     * @param <B>   The type of the builder.
     */
    public static class Builder<B extends Builder<B>> extends FileConfigHandle.Builder<YamlConfiguration, B> {

        protected Builder(@NotNull Path configPath) {
            super(configPath);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull YamlConfigHandle build() {
            return new YamlConfigHandle(configPath, logger, nodes, migrator);
        }
    }
}
