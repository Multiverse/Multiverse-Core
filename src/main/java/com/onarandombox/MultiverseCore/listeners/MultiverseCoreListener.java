/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import com.onarandombox.MultiverseCore.event.MVPlayerTouchedPortalEvent;
import com.onarandombox.MultiverseCore.event.MVRespawnEvent;
import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import com.onarandombox.MultiverseCore.event.MVVersionEvent;
import com.onarandombox.MultiverseCore.event.MVWorldDeleteEvent;
import com.onarandombox.MultiverseCore.event.MVWorldPropertyChangeEvent;

/**
 * Subclasses of this listener can be used to conveniently listen to MultiverseCore-events.
 */
public abstract class MultiverseCoreListener extends CustomEventListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public final void onCustomEvent(Event event) {
        if (event.getEventName().equals("MVConfigReload") && event instanceof MVConfigReloadEvent) {
            onMVConfigReload((MVConfigReloadEvent) event);
        } else if (event.getEventName().equals("MVPlayerTouchedPortalEvent") && event instanceof MVPlayerTouchedPortalEvent) {
            onPlayerTouchedPortal((MVPlayerTouchedPortalEvent) event);
        } else if (event.getEventName().equals("MVRespawn") && event instanceof MVRespawnEvent) {
            onPlayerRespawn((MVRespawnEvent) event);
        } else if (event.getEventName().equals("SafeTTeleporter") && event instanceof MVTeleportEvent) {
            onPlayerTeleport((MVTeleportEvent) event);
        } else if (event.getEventName().equals("MVVersionEvent") && event instanceof MVVersionEvent) {
            onVersionRequest((MVVersionEvent) event);
        } else if (event.getEventName().equals("MVWorldDeleteEvent") && event instanceof MVWorldDeleteEvent) {
            onWorldDelete((MVWorldDeleteEvent) event);
        } else if (event.getEventName().equals("MVWorldPropertyChange") && event instanceof MVWorldPropertyChangeEvent) {
            onWorldPropertyChange((MVWorldPropertyChangeEvent) event);
        }
    }

    /**
     * Called when a {@link MVWorldPropertyChangeEvent} is fired.
     * @param event The event.
     */
    public void onWorldPropertyChange(MVWorldPropertyChangeEvent event) {
    }

    /**
     * Called when a {@link MVWorldDeleteEvent} is fired.
     * @param event The event.
     */
    public void onWorldDelete(MVWorldDeleteEvent event) {
    }

    /**
     * Called when a {@link MVVersionEvent} is fired.
     * @param event The event.
     */
    public void onVersionRequest(MVVersionEvent event) {
    }

    /**
     * Called when a {@link MVTeleportEvent} is fired.
     * @param event The event.
     */
    public void onPlayerTeleport(MVTeleportEvent event) {
    }

    /**
     * Called when a {@link MVRespawnEvent} is fired.
     * @param event The event.
     */
    public void onPlayerRespawn(MVRespawnEvent event) {
    }

    /**
     * Called when a {@link MVPlayerTouchedPortalEvent} is fired.
     * @param event The event.
     */
    public void onPlayerTouchedPortal(MVPlayerTouchedPortalEvent event) {
    }

    /**
     * Called when a {@link MVConfigReloadEvent} is fired.
     * @param event The event.
     */
    public void onMVConfigReload(MVConfigReloadEvent event) {
    }

}
