package org.mvplugins.multiverse.core;

import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.inject.PluginServiceLocator;

/**
 * Common plugin class for all Multiverse plugins.
 */
public abstract class MultiversePlugin extends JavaPlugin {
    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        super.onEnable();
        MultiversePluginsRegistration.get().registerMultiversePlugin(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        super.onDisable();
        MultiversePluginsRegistration.get().unregisterMultiversePlugin(this);
    }

    /**
     * The minimum version that this plugin is compatible with Multiverse-Core.
     *
     * @return The version number.
     */
    public abstract double getTargetCoreVersion();

    /**
     * Gets the {@link PluginServiceLocator} for this plugin.
     *
     * @return The {@link PluginServiceLocator}
     */
    public abstract PluginServiceLocator getServiceLocator();

    protected double getVersionAsNumber() {
        String[] split = this.getDescription().getVersion().split("\\.");
        if (split.length < 2) {
            return -1;
        }
        return Double.parseDouble(split[0] + "." + split[1]);
    }
}
