package org.mvplugins.multiverse.core.config.migration.action;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Single migrator action that converts a string value to a boolean.
 */
public final class BooleanMigratorAction implements MigratorAction {

    public static BooleanMigratorAction of(String path) {
        return new BooleanMigratorAction(path);
    }

    private final String path;

    private BooleanMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, ACFUtil.isTruthy(config.getString(path, "")));
        Logging.config("Converted %s to boolean %s", path, config.getBoolean(path));
    }
}

