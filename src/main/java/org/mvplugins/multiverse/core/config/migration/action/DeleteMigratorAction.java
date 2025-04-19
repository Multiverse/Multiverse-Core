package org.mvplugins.multiverse.core.config.migration.action;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

public final class DeleteMigratorAction implements MigratorAction {

    public static DeleteMigratorAction of(String path) {
        return new DeleteMigratorAction(path);
    }

    private final String path;

    private DeleteMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, null);
        Logging.config("Deleted %s", path);
    }
}
