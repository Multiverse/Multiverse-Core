/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import com.onarandombox.MultiverseCore.event.MVPlayerTouchedPortalEvent;
import com.onarandombox.MultiverseCore.event.MVRespawnEvent;
import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import com.onarandombox.MultiverseCore.event.MVVersionEvent;
import com.onarandombox.MultiverseCore.event.MVWorldDeleteEvent;
import com.onarandombox.MultiverseCore.event.MVWorldPropertyChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Subclasses of this listener can be used to conveniently listen to MultiverseCore-events.
 */
public abstract class MultiverseCoreListener implements Listener {
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
