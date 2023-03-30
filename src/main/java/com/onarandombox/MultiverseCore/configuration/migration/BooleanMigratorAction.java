package com.onarandombox.MultiverseCore.configuration.migration;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.file.FileConfiguration;

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
    public void migrate(FileConfiguration config) {
        config.set(path, ACFUtil.isTruthy(config.getString(path, "")));
        Logging.info("Converted %s to boolean %s", path, config.getBoolean(path));
    }
}
