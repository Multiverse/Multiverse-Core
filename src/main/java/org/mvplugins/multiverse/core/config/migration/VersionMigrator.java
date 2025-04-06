package org.mvplugins.multiverse.core.config.migration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.config.migration.action.MigratorAction;

/**
 * A version migrator is a collection of migrator actions that are performed when migrating a config to a specific version.
 */
public class VersionMigrator implements Comparable<VersionMigrator> {

    /**
     * Creates a new builder for a VersionMigrator.
     *
     * @param version The version number of the config that this migrator migrates to.
     * @return The builder instance.
     */
    public static Builder builder(double version) {
        return new Builder(version);
    }

    private final double version;
    private final List<MigratorAction> actions;

    protected VersionMigrator(double version, List<MigratorAction> actions) {
        this.version = version;
        this.actions = actions;
    }

    /**
     * Performs all the migrator actions.
     *
     * @param config The target settings instance to migrate.
     */
    public void migrate(ConfigurationSection config) {
        actions.forEach(action -> action.migrate(config));
    }

    /**
     * Gets the version number of the config that this migrator migrates to.
     *
     * @return The version number.
     */
    public double getVersion() {
        return version;
    }

    @Override
    public int compareTo(@NotNull VersionMigrator o) {
        return Double.compare(version, o.version);
    }

    /**
     * A builder for a VersionMigrator.
     */
    public static class Builder {
        private final double version;
        private final List<MigratorAction> actions = new ArrayList<>();

        /**
         * Creates a new builder for a VersionMigrator.
         *
         * @param version The version number of the config that this migrator migrates to.
         */
        public Builder(double version) {
            this.version = version;
        }

        /**
         * Adds a migrator action to the list of actions.
         *
         * @param action The action to add.
         * @return The builder instance.
         */
        public Builder addAction(MigratorAction action) {
            actions.add(action);
            return this;
        }

        /**
         * Builds the VersionMigrator.
         *
         * @return The built VersionMigrator.
         */
        public VersionMigrator build() {
            return new VersionMigrator(version, actions);
        }
    }
}
