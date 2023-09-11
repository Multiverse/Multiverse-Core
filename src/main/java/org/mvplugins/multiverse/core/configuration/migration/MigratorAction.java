package org.mvplugins.multiverse.core.configuration.migration;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A migrator action is a single action that is performed when migrating a config.
 */
public interface MigratorAction {

    /**
     * Performs the migration action.
     *
     * @param config The target settings instance to migrate.
     */
    void migrate(ConfigurationSection config);
}
