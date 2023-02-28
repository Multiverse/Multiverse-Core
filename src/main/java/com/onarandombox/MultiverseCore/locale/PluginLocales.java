package com.onarandombox.MultiverseCore.locale;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.BukkitLocales;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PluginLocales extends BukkitLocales {

    private static final String DEFAULT_LOCALE_FOLDER_PATH = "locales";

    public PluginLocales(BukkitCommandManager manager) {
        super(manager);
    }

    public boolean addFileResClassLoader(@NotNull Plugin plugin) {
        return this.addBundleClassLoader(new FileResClassLoader(plugin, DEFAULT_LOCALE_FOLDER_PATH));
    }

    public boolean addFileResClassLoader(@NotNull Plugin plugin, @NotNull String localesFolderPath) {
        return this.addBundleClassLoader(new FileResClassLoader(plugin, localesFolderPath));
    }
}
