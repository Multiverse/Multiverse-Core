package com.onarandombox.MultiverseCore.utils.settings.migration;

import java.util.Optional;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.utils.settings.MVSettings;

public class MoveMigratorAction implements MigratorAction {
    public static MoveMigratorAction of(String fromPath, String toPath) {
        return new MoveMigratorAction(fromPath, toPath);
    }

    private final String fromPath;
    private final String toPath;

    public MoveMigratorAction(String fromPath, String toPath) {
        this.fromPath = fromPath;
        this.toPath = toPath;
    }

    @Override
    public void migrate(MVSettings settings) {
        Optional.ofNullable(settings.getConfig().get(fromPath))
                .ifPresent(value -> {
                    settings.getConfig().set(toPath, value);
                    settings.getConfig().set(fromPath, null);
                    Logging.config("Moved path %s to %s", fromPath, toPath);
                });
    }
}
