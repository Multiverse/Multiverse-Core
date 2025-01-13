package org.mvplugins.multiverse.core.configuration.migration;

import java.util.ArrayList;
import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

import org.mvplugins.multiverse.core.configuration.node.ValueNode;

/**
 * Helper class for migrating configs to the latest config version.
 */
public class ConfigMigrator {

    /**
     * Creates a new builder for a ConfigMigrator.
     *
     * @param versionNode   The node that stores the version number of the config.
     *                      Default value should be the current latest version number.
     * @return The builder instance.
     */
    public static Builder builder(ValueNode<Double> versionNode) {
        return new Builder(versionNode);
    }

    private final ValueNode<Double> versionNode;
    private final List<VersionMigrator> versionMigrators;

    protected ConfigMigrator(ValueNode<Double> versionNode, List<VersionMigrator> versionMigrators) {
        this.versionNode = versionNode;
        this.versionMigrators = versionMigrators;
    }

    /**
     * Migrates the config to the latest version if necessary.
     *
     * @param config The target settings instance to migrate.
     */
    public void migrate(ConfigurationSection config) {
        if (config.getKeys(false).isEmpty()) {
            config.set(versionNode.getPath(), getLatestVersion());
            return;
        }

        double versionNumber = config.getDouble(versionNode.getPath());
        for (VersionMigrator versionMigrator : versionMigrators) {
            if (versionNumber < versionMigrator.getVersion()) {
                Logging.info("Migrating config from version %s to %s...", versionNumber, versionMigrator.getVersion());
                versionMigrator.migrate(config);
                // Set the version number to the latest version number
                config.set(versionNode.getPath(), versionMigrator.getVersion());
            }
        }
    }

    /**
     * Gets the latest version number of the config.
     *
     * @return The latest version number.
     */
    private double getLatestVersion() {
        if (versionMigrators.isEmpty()) {
            return 0.0;
        }
        return versionMigrators.get(versionMigrators.size() - 1).getVersion();
    }

    /**
     * A builder for a ConfigMigrator.
     */
    public static class Builder {
        private final ValueNode<Double> versionNode;
        private final List<VersionMigrator> versionMigrators;

        /**
         * Creates a new builder for a ConfigMigrator.
         *
         * @param versionNode   The node that stores the version number of the config.
         *                      Default value should be the current latest version number.
         */
        public Builder(ValueNode<Double> versionNode) {
            this.versionNode = versionNode;
            this.versionMigrators = new ArrayList<>();
        }

        /**
         * Adds a version migrator to the list of migrators.
         *
         * @param versionMigrator  The migrator to add.
         * @return The builder instance.
         */
        public Builder addVersionMigrator(VersionMigrator versionMigrator) {
            versionMigrators.add(versionMigrator);
            return this;
        }

        /**
         * Builds the ConfigMigrator.
         *
         * @return The built ConfigMigrator.
         */
        public ConfigMigrator build() {
            return new ConfigMigrator(versionNode, versionMigrators);
        }
    }
}
