/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import org.bukkit.event.EventHandler;

import org.mvplugins.multiverse.core.event.MVConfigReloadEvent;
import org.mvplugins.multiverse.core.event.MVPlayerTouchedPortalEvent;
import org.mvplugins.multiverse.core.event.MVRespawnEvent;
import org.mvplugins.multiverse.core.event.MVTeleportEvent;
import org.mvplugins.multiverse.core.event.MVVersionEvent;
import org.mvplugins.multiverse.core.event.MVWorldDeleteEvent;
import org.mvplugins.multiverse.core.event.MVWorldPropertyChangeEvent;
import org.mvplugins.multiverse.core.inject.InjectableListener;

/**
 * Subclasses of this listener can be used to conveniently listen to MultiverseCore-events.
 */
public abstract class MultiverseCoreListener implements InjectableListener {
    /**
     * Called when a {@link MVWorldPropertyChangeEvent} is fired.
     * @param event The event.
     */
    @EventHandler
    public void worldPropertyChange(MVWorldPropertyChangeEvent event) {
    }

    /**
     * Called when a {@link MVWorldDeleteEvent} is fired.
     * @param event The event.
     */
    @EventHandler
    public void worldDelete(MVWorldDeleteEvent event) {
    }

    /**
     * Called when a {@link MVVersionEvent} is fired.
     * @param event The event.
     */
    @EventHandler
    public void versionRequest(MVVersionEvent event) {
    }

    /**
     * Called when a {@link MVTeleportEvent} is fired.
     * @param event The event.
     */
    @EventHandler
    public void playerTeleport(MVTeleportEvent event) {
    }

    /**
     * Called when a {@link MVRespawnEvent} is fired.
     * @param event The event.
     */
    @EventHandler
    public void playerRespawn(MVRespawnEvent event) {
    }

    /**
     * Called when a {@link MVPlayerTouchedPortalEvent} is fired.
     * @param event The event.
     */
    @EventHandler
    public void playerTouchedPortal(MVPlayerTouchedPortalEvent event) {
    }

    /**
     * Called when a {@link MVConfigReloadEvent} is fired.
     * @param event The event.
     */
    @EventHandler
    public void configReload(MVConfigReloadEvent event) {
    }
}
