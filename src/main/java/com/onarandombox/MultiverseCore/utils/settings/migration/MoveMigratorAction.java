package com.onarandombox.MultiverseCore.utils.settings.migration;

import com.onarandombox.MultiverseCore.utils.settings.MVSettings;

public class MoveMigratorAction implements MigratorAction {
    private final String fromPath;
    private final String toPath;

    public MoveMigratorAction(String fromPath, String toPath) {
        this.fromPath = fromPath;
        this.toPath = toPath;
    }

    @Override
    public void migrate(MVSettings settings) {
        Object value = settings.getConfig().get(fromPath);
        if (value != null) {
            settings.getConfig().set(toPath, value);
        }
    }
}
