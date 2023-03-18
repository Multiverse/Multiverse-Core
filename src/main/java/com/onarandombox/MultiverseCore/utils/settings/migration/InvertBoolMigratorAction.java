package com.onarandombox.MultiverseCore.utils.settings.migration;

import com.onarandombox.MultiverseCore.utils.settings.MVSettings;

public class InvertBoolMigratorAction implements MigratorAction {
    public static InvertBoolMigratorAction of(String path) {
        return new InvertBoolMigratorAction(path);
    }

    private final String path;

    public InvertBoolMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(MVSettings settings) {
        settings.getConfig().set(path, !settings.getConfig().getBoolean(path));
    }
}
