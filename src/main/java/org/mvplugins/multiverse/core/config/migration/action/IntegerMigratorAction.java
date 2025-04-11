package org.mvplugins.multiverse.core.config.migration.action;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Single migrator action that converts a string value to an integer.
 */
public final class IntegerMigratorAction implements MigratorAction {

    public static IntegerMigratorAction of(String path) {
        return new IntegerMigratorAction(path);
    }

    private final String path;

    private IntegerMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, ACFUtil.parseInt(config.getString(path)));
        Logging.config("Converted %s to integer %s", path, config.getInt(path));
    }
}
