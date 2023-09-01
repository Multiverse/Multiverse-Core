package com.onarandombox.MultiverseCore.configuration.handle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generic configuration handle for file based configurations.
 * @param <C>   The configuration type.
 */
abstract class FileConfigHandle<C extends FileConfiguration> extends GenericConfigHandle<C> {

    protected final @NotNull Path configPath;
    protected final @NotNull File configFile;

    protected FileConfigHandle(@NotNull Path configPath, @Nullable Logger logger, @Nullable NodeGroup nodes, @Nullable ConfigMigrator migrator) {
        super(logger, nodes, migrator);
        this.configPath = configPath;
        this.configFile = configPath.toFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean load() {
        boolean newFileCreated;
        try {
            newFileCreated = createConfigFile();
        } catch (IOException e) {
            Logging.severe("Failed to create config file: %s", configFile.getName());
            Logging.severe(e.getMessage());
            return false;
        }
        if (!loadConfigObject()) {
            Logging.severe("Failed to load config file: %s", configFile.getName());
            return false;
        }
        if (!newFileCreated) {
            migrateConfig();
        }
        setUpNodes();
        return true;
    }

    /**
     * Create a new config file if file does not exist
     *
     * @return True if file exist or created successfully, otherwise false.
     */
    protected boolean createConfigFile() throws IOException {
        if (configFile.exists()) {
            return false;
        }
        return configFile.createNewFile();
    }

    /**
     * Loads the configuration object.
     *
     * @return True if the configuration was loaded successfully, false otherwise.
     */
    protected abstract boolean loadConfigObject();

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

    /**
     * Abstract builder for {@link FileConfigHandle}.
     *
     * @param <C>   The configuration type.
     * @param <B>   The builder type.
     */
    public static abstract class Builder<C extends FileConfiguration, B extends Builder<C, B>> extends GenericConfigHandle.Builder<C, B> {

        protected @NotNull Path configPath;

        protected Builder(@NotNull Path configPath) {
            this.configPath = configPath;
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
