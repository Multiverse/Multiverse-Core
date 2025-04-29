package org.mvplugins.multiverse.core.config.migration.action;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Single migrator action that inverts a boolean value for a given path.
 */
public final class InvertBoolMigratorAction implements MigratorAction {

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

    private InvertBoolMigratorAction(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void migrate(ConfigurationSection config) {
        boolean boolValue = !config.getBoolean(path);
        config.set(path, boolValue);
        Logging.config("Inverted %s to boolean %s", path, boolValue);
    }
}
