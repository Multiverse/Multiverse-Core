/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.event;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the Multiverse-config should be reloaded.
 */
public final class MVConfigReloadEvent extends Event {
    private final List<String> configsLoaded;

    public MVConfigReloadEvent(List<String> configsLoaded) {
        this.configsLoaded = configsLoaded;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the handler list. This is required by the event system.
     *
     * @return A list of HANDLERS.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Adds a config to this event.
     *
     * @param config The config to add.
     */
    public void addConfig(String config) {
        this.configsLoaded.add(config);
    }

    /**
     * Gets all loaded configs.
     *
     * @return A list of all loaded configs.
     */
    public List<String> getAllConfigsLoaded() {
        return this.configsLoaded;
    }
}
