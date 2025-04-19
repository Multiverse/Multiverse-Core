package org.mvplugins.multiverse.core.config.migration.action;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Single migrator action that converts a string value to a long.
 */
public final class LongMigratorAction implements MigratorAction {

    public static LongMigratorAction of(String path) {
        return new LongMigratorAction(path);
    }

    private final String path;

    private LongMigratorAction(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, ACFUtil.parseLong(config.getString(path)));
        Logging.config("Converted %s to long %s", path, config.getLong(path));
    }
}
