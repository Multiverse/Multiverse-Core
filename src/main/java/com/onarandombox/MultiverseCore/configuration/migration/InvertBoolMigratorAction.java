package com.onarandombox.MultiverseCore.configuration.migration;

import com.onarandombox.MultiverseCore.configuration.ConfigHandle;

/**
 * Single migrator action that inverts a boolean value for a given path.
 */
public class InvertBoolMigratorAction implements MigratorAction {

    /**
     * Creates a new migrator action that inverts a boolean value for a given path.
     *
     * @param path The path to invert value of.
     * @return The new migrator action.
     */
    public static InvertBoolMigratorAction of(String path) {
        return new InvertBoolMigratorAction(path);
    }

    private final String path;

    protected InvertBoolMigratorAction(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void migrate(ConfigHandle settings) {
        settings.getConfig().set(path, !settings.getConfig().getBoolean(path));
    }
}
