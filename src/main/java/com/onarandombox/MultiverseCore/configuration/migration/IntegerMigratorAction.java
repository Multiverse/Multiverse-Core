package com.onarandombox.MultiverseCore.configuration.migration;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Single migrator action that converts a string value to an integer.
 */
public class IntegerMigratorAction implements MigratorAction {

    public static IntegerMigratorAction of(String path) {
        return new IntegerMigratorAction(path);
    }

    private final String path;

    public IntegerMigratorAction(String path) {
        this.path = path;
    }

    @Override
    public void migrate(ConfigurationSection config) {
        config.set(path, ACFUtil.parseInt(config.getString(path)));
        Logging.info("Converted %s to integer %s", path, config.getInt(path));
    }
}
