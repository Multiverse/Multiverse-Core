/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.event;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;

/**
 * Event that gets called when a player teleports to a {@link DestinationInstance} with {@link AsyncSafetyTeleporter}.
 */
public final class MVTeleportDestinationEvent extends Event implements Cancellable {
    private final Entity teleportee;
    private final CommandSender teleporter;
    private final DestinationInstance<?, ?> dest;
    private boolean isCancelled;

    public MVTeleportDestinationEvent(DestinationInstance<?, ?> dest, Entity teleportee, CommandSender teleporter) {
        this.teleportee = teleportee;
        this.teleporter = teleporter;
        this.dest = dest;
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
     * Returns the player who will be teleported by this event.
     *
     * @return The player who will be teleported by this event.
     */
    public Entity getTeleportee() {
        return this.teleportee;
    }

    /**
     * Returns the location the player was before the teleport.
     *
     * @return The location the player was before the teleport.
     */
    public Location getFrom() {
        return this.teleportee.getLocation();
    }

    /**
     * Gets the {@link CommandSender} who requested the Teleport.
     *
     * @return The {@link CommandSender} who requested the Teleport
     */
    public CommandSender getTeleporter() {
        return this.teleporter;
    }

    /**
     * Returns the destination that the player will spawn at.
     *
     * @return The destination the player will spawn at.
     */
    public DestinationInstance<?, ?> getDestination() {
        return this.dest;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
