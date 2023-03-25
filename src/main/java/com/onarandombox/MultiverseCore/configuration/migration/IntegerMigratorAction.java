package com.onarandombox.MultiverseCore.configuration.migration;

import co.aikar.commands.ACFUtil;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.ConfigHandle;
import org.bukkit.util.NumberConversions;

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
    public void migrate(ConfigHandle settings) {
        settings.getConfig().set(path, ACFUtil.parseInt(settings.getConfig().getString(path)));
        Logging.info("Converted %s to integer %s", path, settings.getConfig().getInt(path));
    }
}
