package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.BukkitLocales;
import com.onarandombox.MultiverseCore.utils.file.FileResClassLoader;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Locale manager with additional methods for loading locales from plugin's locales folder.
 */
public class PluginLocales extends BukkitLocales {

    private static final String DEFAULT_LOCALE_FOLDER_PATH = "locales";

    /**
     * Creates a new instance of {@link PluginLocales}.
     *
     * @param manager   The command manager.
     */
    public PluginLocales(BukkitCommandManager manager) {
        super(manager);
    }

    /**
     * Adds a {@link FileResClassLoader} to the list of class loaders to load locales data from.
     *
     * @param plugin    The plugin.
     * @return True if the class loader was added successfully.
     */
    public boolean addFileResClassLoader(@NotNull Plugin plugin) {
        return this.addBundleClassLoader(new FileResClassLoader(plugin, DEFAULT_LOCALE_FOLDER_PATH));
    }

    /**
     * Adds a {@link FileResClassLoader} to the list of class loaders to load locales data from.
     *
     * @param plugin            The plugin.
     * @param localesFolderPath The path to the folder containing the locales.
     * @return True if the class loader was added successfully.
     */
    public boolean addFileResClassLoader(@NotNull Plugin plugin, @NotNull String localesFolderPath) {
        return this.addBundleClassLoader(new FileResClassLoader(plugin, localesFolderPath));
    }
}
