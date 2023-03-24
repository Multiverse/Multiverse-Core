package com.onarandombox.MultiverseCore.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.node.EnchancedValueNode;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;
import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;
import io.github.townyadvanced.commentedconfiguration.setting.ValueNode;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class that makes use of CommentedConfiguration to provide a simple way to load and save with node objects.
 */
public class ConfigHandle {

    /**
     * A builder class for creating a ConfigHandle.
     *
     * @param configPath    The path to the configuration file in string.
     * @return A new Builder instance.
     */
    public static Builder builder(String configPath) {
        return new Builder(configPath);
    }

    /**
     * A builder class for creating a ConfigHandle.
     *
     * @param configPath    The path to the configuration file.
     * @return A new Builder instance.
     */
    public static Builder builder(Path configPath) {
        return new Builder(configPath);
    }

    protected final Path configPath;
    protected final Logger logger;
    protected final NodeGroup nodes;

    protected final ConfigMigrator migrator;

    protected CommentedConfiguration config;

    /**
     * Creates a new MVSettings instance that makes use of CommentedConfiguration.
     *
     * @param configPath    The path to the configuration file.
     * @param logger        The Logger to use for error messages.
     * @param nodes         All the node path and values for the configuration.
     * @param migrator      The migrator to use for migrating the configuration.
     */
    protected ConfigHandle(@NotNull Path configPath, @Nullable Logger logger, @NotNull NodeGroup nodes, ConfigMigrator migrator) {
        this.configPath = configPath;
        this.nodes = nodes;
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

    /**
     * Migration of the configuration based on {@link ConfigMigrator}.
     */
    protected void migrateConfig() {
        migrator.migrate(this);
    }

    /**
     * Adds default node values to the configuration if they are not already present.
     */
    protected void addDefaultNodes() {
        if (nodes.isEmpty()) {
            return;
        }

        CommentedConfiguration tempConfig = new CommentedConfiguration(configPath, logger);
        for (CommentedNode node : nodes) {
            if (node.getComments().length > 0) {
                tempConfig.addComment(node.getPath(), node.getComments());
            }
            if (node instanceof ValueNode) {
                tempConfig.set(node.getPath(), get((ValueNode) node));
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

    /**
     * Checks if the configuration is loaded.
     *
     * @return True if the configuration is loaded, false otherwise.
     */
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
     * Get the value of the node by name.
     *
     * @param name  The name of the node to get the value of.
     * @return The value of the node.
     */
    public Object get(@NotNull String name) {
        return nodes.findNode(name)
                .map(node -> (node instanceof ValueNode) ? get((ValueNode) node) : null)
                .orElse(null);
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
     * Set the value of the node by name.
     *
     * @param name  The name of the node to set the value of.
     * @param value The value to set.
     */
    public boolean set(@NotNull String name, Object value) {
        return nodes.findNode(name)
                .map(node -> node instanceof ValueNode && set((ValueNode) node, value))
                .orElse(false);
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param node  The node to set the value of.
     * @param value The value to set.
     */
    public boolean set(@NotNull ValueNode node, Object value) {
        if (node instanceof TypedValueNode) {
            return set(node, value);
        }
        config.set(node.getPath(), value);
        return true;
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param node  The node to set the value of.
     * @param value The value to set.
     * @param <T>   The type of the node value.
     */
    public <T> boolean set(@NotNull TypedValueNode<T> node, T value) {
        if (node instanceof EnchancedValueNode) {
            return set((EnchancedValueNode<T>) node, value);
        }
        config.set(node.getPath(), value);
        return true;
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param node  The node to set the value of.
     * @param value The value to set.
     * @return True if the value was set, false otherwise.
     * @param <T>   The type of the node value.
     */
    public <T> boolean set(@NotNull EnchancedValueNode<T> node, T value) {
        T oldValue = get(node);
        config.set(node.getPath(), value);
        node.onSetValue(oldValue, get(node));
        return true;
    }

    /**
     * Sets the default value of a node.
     *
     * @param node  The node to set the default value of.
     * @param <T>   The type of the node value.
     */
    public <T> void setDefault(@NotNull TypedValueNode<T> node) {
        config.set(node.getPath(), node.getDefaultValue());
    }

    /**
     * Gets the inner configuration object.
     *
     * @return The configuration object.
     */
    public @NotNull CommentedConfiguration getConfig() {
        return config;
    }

    /**
     * Gets the path of the configuration file.
     */
    public static class Builder {

        private final Path configPath;
        private Logger logger;
        private NodeGroup nodes;

        private ConfigMigrator migrator;

        /**
         * Creates a new builder.
         *
         * @param configPath The path of the configuration file.
         */
        public Builder(String configPath) {
            this.configPath = Path.of(configPath);
        }

        /**
         * Creates a new builder.
         *
         * @param configPath The path of the configuration file.
         */
        public Builder(Path configPath) {
            this.configPath = configPath;
        }

        /**
         * Sets the logger to use.
         *
         * @param plugin    The plugin to get the logger from.
         * @return The builder.
         */
        public Builder logger(@NotNull Plugin plugin) {
            return logger(plugin.getLogger());
        }

        /**
         * Sets the logger to use.
         *
         * @param logger    The logger to use.
         * @return The builder.
         */
        public Builder logger(@Nullable Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Sets the nodes to use.
         *
         * @param nodes The nodes to use.
         * @return The builder.
         */
        public Builder nodes(@Nullable NodeGroup nodes) {
            this.nodes = nodes;
            return this;
        }

        /**
         * Sets the migrator to use.
         *
         * @param migrator  The migrator to use.
         * @return The builder.
         */
        public Builder migrator(@Nullable ConfigMigrator migrator) {
            this.migrator = migrator;
            return this;
        }

        /**
         * Builds the settings.
         *
         * @return The built settings.
         */
        public ConfigHandle build() {
            return new ConfigHandle(configPath, logger, nodes, migrator);
        }
    }
}
