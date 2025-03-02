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
     * The minimum protocol version that this plugin is compatible with Multiverse-Core.
     *
     * @return The Integer protocol version.
     */
    public abstract int getTargetCoreProtocolVersion();

    /**
     * Gets the {@link PluginServiceLocator} for this plugin.
     *
     * @return The {@link PluginServiceLocator}
     */
    public abstract PluginServiceLocator getServiceLocator();
}
