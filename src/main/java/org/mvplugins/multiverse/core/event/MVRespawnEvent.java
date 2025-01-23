/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player is respawning.
 */
public final class MVRespawnEvent extends PlayerEvent implements Cancellable {
    private Location location;
    private boolean cancelled = false;

    public MVRespawnEvent(Location spawningAt, Player player) {
        super(player);
        this.location = spawningAt;
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
     * Gets the player's respawn-{@link Location}.
     *
     * @return The player's respawn-{@link Location}.
     */
    public Location getRespawnLocation() {
        return this.location;
    }

    /**
     * Sets the player's respawn-{@link Location}.
     *
     * @param location The new respawn-{@link Location}.
     */
    public void setRespawnLocation(Location location) {
        this.location = location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
