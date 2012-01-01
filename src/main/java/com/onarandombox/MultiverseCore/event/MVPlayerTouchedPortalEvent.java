/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * This event is thrown when a portal is touched.
 */
public class MVPlayerTouchedPortalEvent extends Event implements Cancellable {
    private Player p;
    private Location l;
    private boolean isCancelled;

    public MVPlayerTouchedPortalEvent(Player p, Location l) {
        super("MVPlayerTouchedPortalEvent");
        this.p = p;
        this.l = l;
    }

    public Location getBlockTouched() {
        return this.l;
    }

    public Player getPlayer() {
        return this.p;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
