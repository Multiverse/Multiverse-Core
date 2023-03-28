package com.onarandombox.MultiverseCore.configuration.handle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import com.onarandombox.MultiverseCore.configuration.node.ValueNode;
import io.vavr.control.Try;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class FileConfigHandle<C extends FileConfiguration> {

    protected final @NotNull Path configPath;
    protected final @NotNull File configFile;
    protected final @Nullable Logger logger;
    protected final @NotNull NodeGroup nodes;
    protected final @Nullable ConfigMigrator migrator;

    protected C config;

    protected FileConfigHandle(@NotNull Path configPath, @Nullable Logger logger, @NotNull NodeGroup nodes, @Nullable ConfigMigrator migrator) {
        this.configPath = configPath;
        this.configFile = configPath.toFile();
        this.logger = logger;
        this.nodes = nodes;
        this.migrator = migrator;
    }

    /**
     * Loads the configuration.
     *
     * @return True if the configuration was loaded successfully, false otherwise.
     */
    public boolean load() {
        if (!createConfigFile()) {
            Logging.severe("Failed to create config file: %s", configFile.getName());
            return false;
        }
        if (!loadConfigObject()) {
            Logging.severe("Failed to load config file: %s", configFile.getName());
            return false;
        }
        migrateConfig();
        setUpNodes();
        return true;
    }

    /**
     * Create a new config file if file does not exist
     *
     * @return True if file exist or created successfully, otherwise false.
     */
    protected boolean createConfigFile() {
        if (configFile.exists()) {
            return true;
        }
        try {
            if (!configFile.createNewFile()) {
                return false;
            }
            Logging.info("Created new config file: %s", configFile.getName());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    protected abstract boolean loadConfigObject();

    protected void migrateConfig() {
        if (migrator != null) {
            migrator.migrate(config);
        }
    }

    protected abstract void setUpNodes();

    /**
     * Saves the configuration.
     */
    public abstract boolean save();

    /**
     * Checks if the configuration is loaded.
     *
     * @return True if the configuration is loaded, false otherwise.
     */
    public boolean isLoaded() {
        return config != null;
    }

    /**
     * Gets the configuration.
     *
     * @return The configuration.
     */
    public C getConfig() {
        return config;
    }

    public Try<Object> get(@Nullable String name) {
        return nodes.findNode(name, ValueNode.class)
                .map(node -> Try.of(() -> get(node)))
                .orElse(Try.failure(new Exception("Node not found")));
    }

    /**
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     *
     * @param node The node to get the value of.
     * @return The value of the node.
     */
    public <T> T get(@NotNull ValueNode<T> node) {
        return config.getObject(node.getPath(), node.getType(), node.getDefaultValue());
    }

    public Try<Boolean> set(@Nullable String name, Object value) {
        return nodes.findNode(name, ValueNode.class)
                .map(node -> (Try<Boolean>) set(node, value))
                .orElse(Try.failure(new Exception("Node not found")));
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param node  The node to set the value of.
     * @param value The value to set.
     * @return True if the value was set, false otherwise.
     * @param <T>   The type of the node value.
     */
    public <T> Try<Boolean> set(@NotNull ValueNode<T> node, T value) {
        if (!node.validate(value)) {
            return Try.failure(new Exception("Validation failed"));
        }
        T oldValue = get(node);
        config.set(node.getPath(), value);
        node.onSetValue(oldValue, get(node));
        return Try.success(true);
    }

    /**
     * Sets the default value of a node.
     *
     * @param node  The node to set the default value of.
     */
    public void setDefault(@NotNull ValueNode node) {
        config.set(node.getPath(), node.getDefaultValue());
    }

    public static abstract class Builder<C extends FileConfiguration, B extends Builder<C, B>> {

        protected @NotNull Path configPath;
        protected @Nullable Logger logger;
        protected @NotNull NodeGroup nodes;
        protected @Nullable ConfigMigrator migrator;

        protected Builder(@NotNull Path configPath, @NotNull NodeGroup nodes) {
            this.configPath = configPath;
            this.nodes = nodes;
        }

        /**
         * Sets the logger.
         *
         * @param logger The logger.
         * @return The builder.
         */
        public B logger(@Nullable Logger logger) {
            this.logger = logger;
            return self();
        }

        public B logger(Plugin plugin) {
            this.logger = plugin.getLogger();
            return self();
        }

        /**
         * Sets the migrator.
         *
         * @param migrator The migrator.
         * @return The builder.
         */
        public B migrator(@Nullable ConfigMigrator migrator) {
            this.migrator = migrator;
            return self();
        }

        /**
         * Builds the configuration handle.
         *
         * @return The configuration handle.
         */
        public abstract @NotNull FileConfigHandle<C> build();

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }
}
