package org.mvplugins.multiverse.core.configuration.migration;

import java.util.Optional;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

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
     * {@inheritDoc}
     */
    @Override
    public void migrate(ConfigurationSection config) {
        Optional.ofNullable(config.get(fromPath))
                .ifPresent(value -> {
                    config.set(fromPath, null);
                    config.set(toPath, value);
                    Logging.config("Moved path %s to %s", fromPath, toPath);
                });
    }
}
