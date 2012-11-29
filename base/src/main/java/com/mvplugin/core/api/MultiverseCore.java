package com.mvplugin.core.api;

import com.dumptruckman.minecraft.pluginbase.plugin.PluginBase;

/**
 * Multiverse 3 Core API
 * <p>
 * This API contains a bunch of useful things you can get out of Multiverse in general!
 * This is the class you should cast your plugin to unless you need more Implementation specific API.
 */
public interface MultiverseCore extends MultiversePlugin, PluginBase {

    /**
     * Gets the Multiverse world manager.
     *
     * The world manager allows you to perform various tasks related to Minecraft worlds.
     *
     * @return {@link WorldManager}.
     */
    WorldManager getWorldManager();

    /**
     * Gets the Multiverse-Core configuration.
     *
     * @return The Multiverse-Core configuration.
     */
    CoreConfig getMVConfig();
}
