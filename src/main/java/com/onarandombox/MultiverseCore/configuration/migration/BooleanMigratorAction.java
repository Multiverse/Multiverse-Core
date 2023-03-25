package com.onarandombox.MultiverseCore.configuration.migration;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.ConfigHandle;

/**
 * Single migrator action that converts a string value to a boolean.
 */
public class BooleanMigratorAction implements MigratorAction {

    public static BooleanMigratorAction of(String path) {
        return new BooleanMigratorAction(path);
    }

    private final String path;

    protected BooleanMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigHandle settings) {
        settings.getConfig().set(path, ACFUtil.isTruthy(settings.getConfig().getString(path, "")));
        Logging.info("Converted %s to boolean %s", path, settings.getConfig().getBoolean(path));
    }
}

