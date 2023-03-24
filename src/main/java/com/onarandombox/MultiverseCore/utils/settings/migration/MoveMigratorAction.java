package com.onarandombox.MultiverseCore.utils.settings.migration;

import java.util.Optional;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.utils.settings.MVSettings;

/**
 * Single migrator action that moves a value from one path to another.
 */
public class MoveMigratorAction implements MigratorAction {

    /**
     * Creates a new migrator action that moves a value from one path to another.
     *
     * @param fromPath  The path to move value from.
     * @param toPath    The path to move value to.
     * @return The new migrator action.
     */
    public static MoveMigratorAction of(String fromPath, String toPath) {
        return new MoveMigratorAction(fromPath, toPath);
    }

    private final String fromPath;
    private final String toPath;

    protected MoveMigratorAction(String fromPath, String toPath) {
        this.fromPath = fromPath;
        this.toPath = toPath;
    }

    /**
     * {@InheritDoc}
     */
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
