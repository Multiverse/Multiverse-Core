package org.mvplugins.multiverse.core.configuration.migration;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

public class DoubleMigrationAction implements MigratorAction {

    public static DoubleMigrationAction of(String path) {
        return new DoubleMigrationAction(path);
    }

    private final String path;

    public DoubleMigrationAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, ACFUtil.parseDouble(config.getString(path)));
        Logging.info("Converted %s to double %s", path, config.getDouble(path));
    }
}