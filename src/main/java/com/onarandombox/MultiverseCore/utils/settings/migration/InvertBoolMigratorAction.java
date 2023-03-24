package com.onarandombox.MultiverseCore.utils.settings.migration;

import com.onarandombox.MultiverseCore.utils.settings.MVSettings;

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
     * {@InheritDoc}
     */
    @Override
    public void migrate(MVSettings settings) {
        settings.getConfig().set(path, !settings.getConfig().getBoolean(path));
    }
}
