package com.onarandombox.MultiverseCore.utils.settings.migration;

import com.onarandombox.MultiverseCore.utils.settings.MVSettings;

/**
 * A migrator action is a single action that is performed when migrating a config.
 */
public interface MigratorAction {

    /**
     * Performs the migration action.
     *
     * @param settings The target settings instance to migrate.
     */
    void migrate(MVSettings settings);
}
