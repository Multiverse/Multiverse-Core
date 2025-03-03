package org.mvplugins.multiverse.core.configuration.migration;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

public class DeleteMigratorAction implements MigratorAction {

    public static DeleteMigratorAction of(String path) {
        return new DeleteMigratorAction(path);
    }

    private final String path;

    DeleteMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, null);
        Logging.info("Deleted %s", path);
    }
}
