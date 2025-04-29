package org.mvplugins.multiverse.core.locale;

import co.aikar.commands.BukkitLocales;
import jakarta.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandManager;

/**
 * Locale manager with additional methods for loading locales from plugin's locales folder.
 */
@Service
public final class PluginLocales extends BukkitLocales {

    private static final String DEFAULT_LOCALE_FOLDER_PATH = "locales";

    /**
     * Creates a new instance of {@link PluginLocales}.
     *
     * @param manager   The command manager.
     */
    @Inject
    public PluginLocales(MVCommandManager manager) {
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
