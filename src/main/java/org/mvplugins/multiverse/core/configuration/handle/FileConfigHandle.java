package org.mvplugins.multiverse.core.configuration.handle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import io.vavr.control.Try;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.node.NodeGroup;

/**
 * Generic configuration handle for file based configurations.
 *
 * @param <C>   The configuration type.
 */
public abstract class FileConfigHandle<C extends FileConfiguration> extends GenericConfigHandle<C> {

    protected final @NotNull Path configPath;
    protected final @NotNull File configFile;

    protected FileConfigHandle(
            @NotNull Path configPath,
            @Nullable Logger logger,
            @NotNull NodeGroup nodes,
            @Nullable ConfigMigrator migrator) {
        super(logger, nodes, migrator);
        this.configPath = configPath;
        this.configFile = configPath.toFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> load() {
        boolean isNewFile = !configFile.exists();
        return createConfigFile()
                .andThenTry(this::loadConfigObject)
                .andThenTry(() -> {
                    if (!isNewFile) {
                        migrateConfig();
                    }
                    setUpNodes();
                });
    }

    /**
     * Create a new config file if file does not exist.
     *
     * @return Whether the file was created or its given error.
     */
    protected Try<Void> createConfigFile() {
        return Try.run(() -> {
            if (configFile.exists()) {
                return;
            }
            if (!configFile.createNewFile()) {
                throw new IOException("Failed to create config file: " + configFile.getName());
            }
        });
    }

    /**
     * Loads the configuration object.
     */
    protected abstract void loadConfigObject() throws IOException, InvalidConfigurationException;

    /**
     * Saves the configuration.
     */
    public abstract Try<Void> save();

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
    public abstract static class Builder<C extends FileConfiguration, B extends Builder<C, B>>
            extends GenericConfigHandle.Builder<C, B> {

        protected @NotNull Path configPath;

        protected Builder(@NotNull Path configPath, @NotNull NodeGroup nodes) {
            super(nodes);
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
