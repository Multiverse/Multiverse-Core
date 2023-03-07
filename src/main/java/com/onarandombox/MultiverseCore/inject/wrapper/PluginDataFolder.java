package com.onarandombox.MultiverseCore.inject.wrapper;

import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * An extension of {@link File} that represents the data folder of a plugin.
 */
public class PluginDataFolder extends File {

    /**
     * Creates a new {@link PluginDataFolder} from the given plugin.
     *
     * @param plugin The plugin.
     * @return The plugin data folder.
     */
    public static PluginDataFolder from(Plugin plugin) {
        return new PluginDataFolder(plugin.getDataFolder());
    }

    private PluginDataFolder(File dataFolder) {
        super(dataFolder.getParentFile(), dataFolder.getName());
    }
}
