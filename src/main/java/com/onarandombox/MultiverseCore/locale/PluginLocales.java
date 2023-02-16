package com.onarandombox.MultiverseCore.locale;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.BukkitLocales;
import org.bukkit.plugin.Plugin;

public class PluginLocales extends BukkitLocales {
    public PluginLocales(BukkitCommandManager manager) {
        super(manager);
    }

    public boolean addFileResClassLoader(Plugin plugin) {
        return this.addBundleClassLoader(new FileResClassLoader(plugin.getClass().getClassLoader(), plugin));
    }
}
