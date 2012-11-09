package com.onarandombox.multiverse.core.api;

import com.dumptruckman.minecraft.pluginbase.plugin.PluginBase;

/**
 * Multiverse 3 Core API
 * <p>
 * This API contains a bunch of useful things you can get out of Multiverse in general!
 * This is the class you should cast your plugin to unless you need more Implementation specific API.
 */
public interface Core extends PluginBase<CoreConfig> {

    /**
     * Gets the primary class responsible for managing Multiverse Worlds.
     *
     * @return {@link WorldManager}.
     */
    WorldManager getMVWorldManager();
}
