package org.mvplugins.multiverse.core.config.migration.action;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

public final class DoubleMigratorAction implements MigratorAction {

    public static DoubleMigratorAction of(String path) {
        return new DoubleMigratorAction(path);
    }

    private final String path;

    private DoubleMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, ACFUtil.parseDouble(config.getString(path)));
        Logging.config("Converted %s to double %s", path, config.getDouble(path));
    }
}
