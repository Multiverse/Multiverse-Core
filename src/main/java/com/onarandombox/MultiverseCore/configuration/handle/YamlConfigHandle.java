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

public class YamlConfigHandle extends FileConfigHandle<YamlConfiguration> {

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
    protected void setUpNodes() {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        YamlConfiguration oldConfig = config;
        config = new YamlConfiguration();
        nodes.forEach(node -> {
            if (node instanceof ValueNode valueNode) {
                set(valueNode, oldConfig.getObject(valueNode.getPath(), valueNode.getType(), valueNode.getDefaultValue()));
            }
        });
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
