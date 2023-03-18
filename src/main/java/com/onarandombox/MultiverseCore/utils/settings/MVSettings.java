package com.onarandombox.MultiverseCore.utils.settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import com.onarandombox.MultiverseCore.utils.settings.migration.ConfigMigrator;
import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;
import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;
import io.github.townyadvanced.commentedconfiguration.setting.ValueNode;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MVSettings {

    public static Builder builder(String configPath) {
        return new Builder(configPath);
    }

    public static Builder builder(Path configPath) {
        return new Builder(configPath);
    }

    protected final Path configPath;
    protected final Logger logger;
    protected final List<CommentedNode> defaultNodes;

    protected final ConfigMigrator migrator;

    protected CommentedConfiguration config;

    /**
     * Creates a new MVSettings instance that makes use of CommentedConfiguration.
     *
     * @param configPath   The path to the configuration file.
     * @param logger       The Logger to use for error messages.
     * @param defaultNodes The default node values to add to the configuration.
     * @param migrator
     */
    protected MVSettings(@NotNull Path configPath, @Nullable Logger logger, @Nullable List<CommentedNode> defaultNodes, ConfigMigrator migrator) {
        this.configPath = configPath;
        this.defaultNodes = defaultNodes;
        this.logger = logger;
        this.migrator = migrator;
    }

    /**
     * Loads the configuration.
     *
     * @return True if the configuration was loaded successfully, false otherwise.
     */
    public boolean load() {
        if (!createConfigFile()) {
            return false;
        }
        this.config = new CommentedConfiguration(configPath, logger);
        if (!config.load()) {
            return false;
        }
        migrateConfig();
        addDefaultNodes();
        return true;
    }

    /**
     * Create a new config file if file does not exist
     *
     * @return True if file exist or created successfully, otherwise false.
     */
    protected boolean createConfigFile() {
        File configFile = configPath.toFile();
        if (configFile.exists()) {
            return true;
        }
        try {
            if (!configFile.createNewFile()) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    protected void migrateConfig() {
        migrator.migrate(this);
    }

    /**
     * Adds default node values to the configuration if they are not already present.
     */
    protected void addDefaultNodes() {
        if (defaultNodes == null || defaultNodes.isEmpty()) {
            return;
        }

        CommentedConfiguration tempConfig = new CommentedConfiguration(configPath, logger);
        for (CommentedNode node : defaultNodes) {
            if (node.getComments().length > 0) {
                tempConfig.addComment(node.getPath(), node.getComments());
            }
            if (node instanceof ValueNode) {
                ValueNode valueNode = (ValueNode) node;
                tempConfig.set(node.getPath(), get(valueNode));
            }
        }

        this.config = tempConfig;
    }

    /**
     * Saves the configuration.
     */
    public void save() {
        config.save();
    }

    public boolean isLoaded() {
        return config != null;
    }

    /**
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     *
     * @param node The node to get the value of.
     * @return The value of the node.
     */
    public Object get(@NotNull ValueNode node) {
        return config.get(node.getPath(), node.getDefaultValue());
    }

    /**
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     *
     * @param node The node to get the value of.
     * @param type The type of the node value.
     * @param <T>  The type of the node value.
     * @return The value of the node.
     */
    public <T> T get(@NotNull ValueNode node, Class<T> type) {
        return config.getObject(node.getPath(), type, (T) node.getDefaultValue());
    }

    /**
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     *
     * @param node The node to get the value of.
     * @param <T>  The type of the node value.
     * @return The value of the node.
     */
    public <T> T get(@NotNull TypedValueNode<T> node) {
        return config.getObject(node.getPath(), node.getType(), node.getDefaultValue());
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param node  The node to set the value of.
     * @param value The value to set.
     */
    public void set(@NotNull ValueNode node, Object value) {
        config.set(node.getPath(), value);
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param node  The node to set the value of.
     * @param value The value to set.
     * @param <T>   The type of the node value.
     */
    public <T> void set(@NotNull TypedValueNode<T> node, T value) {
        config.set(node.getPath(), value);
    }

    /**
     * Gets the configuration object.
     *
     * @return The configuration object.
     */
    public @NotNull CommentedConfiguration getConfig() {
        return config;
    }

    public static class Builder {

        private final Path configPath;
        private Logger logger;
        private List<CommentedNode> defaultNodes;

        private ConfigMigrator migrator;

        public Builder(String configPath) {
            this.configPath = Path.of(configPath);
        }

        public Builder(Path configPath) {
            this.configPath = configPath;
        }

        public Builder logger(@NotNull Plugin plugin) {
            return logger(plugin.getLogger());
        }

        public Builder logger(@Nullable Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder defaultNodes(@Nullable List<CommentedNode> defaultNodes) {
            this.defaultNodes = defaultNodes;
            return this;
        }

        public Builder migrator(@Nullable ConfigMigrator migrator) {
            this.migrator = migrator;
            return this;
        }

        public MVSettings build() {
            return new MVSettings(configPath, logger, defaultNodes, migrator);
        }
    }
}
