package com.onarandombox.MultiverseCore.configuration.migration;

import com.onarandombox.MultiverseCore.configuration.ConfigHandle;

/**
 * A migrator action is a single action that is performed when migrating a config.
 */
public interface MigratorAction {

    /**
     * Performs the migration action.
     *
     * @param settings The target settings instance to migrate.
     */
    void migrate(ConfigHandle settings);
}
