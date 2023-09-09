package com.onarandombox.MultiverseCore.configuration.migration;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;

public class LongMigratorAction implements MigratorAction {

    public static LongMigratorAction of(String path) {
        return new LongMigratorAction(path);
    }

    private final String path;

    LongMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, ACFUtil.parseLong(config.getString(path)));
        Logging.info("Converted %s to integer %s", path, config.getLong(path));
    }
}
