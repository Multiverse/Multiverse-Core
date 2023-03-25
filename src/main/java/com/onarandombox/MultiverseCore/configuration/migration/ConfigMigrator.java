package com.onarandombox.MultiverseCore.configuration.migration;

import java.util.ArrayList;
import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.ConfigHandle;
import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;

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
    public static Builder builder(TypedValueNode<Double> versionNode) {
        return new Builder(versionNode);
    }

    private final TypedValueNode<Double> versionNode;
    private final List<VersionMigrator> versionMigrators;

    protected ConfigMigrator(TypedValueNode<Double> versionNode, List<VersionMigrator> versionMigrators) {
        this.versionNode = versionNode;
        this.versionMigrators = versionMigrators;
    }

    /**
     * Migrates the config to the latest version if necessary.
     *
     * @param settings The target settings instance to migrate.
     */
    public void migrate(ConfigHandle settings) {
        double versionNumber = settings.get(versionNode);
        for (VersionMigrator versionMigrator : versionMigrators) {
            if (versionNumber < versionMigrator.getVersion()) {
                Logging.info("Migrating config from version %s to %s...", versionNumber, versionMigrator.getVersion());
                versionMigrator.migrate(settings);
            }
        }
        // Set the version number to the latest version number
        settings.setDefault(versionNode);
    }

    /**
     * A builder for a ConfigMigrator.
     */
    public static class Builder {
        private final TypedValueNode<Double> versionNode;
        private final List<VersionMigrator> versionMigrators;

        /**
         * Creates a new builder for a ConfigMigrator.
         *
         * @param versionNode   The node that stores the version number of the config.
         *                      Default value should be the current latest version number.
         */
        public Builder(TypedValueNode<Double> versionNode) {
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
