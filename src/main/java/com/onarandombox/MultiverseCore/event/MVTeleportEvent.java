/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.event;

import com.onarandombox.MultiverseCore.api.MVDestination;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Event that gets called when a player use the /mvtp command.
 */
public class MVTeleportEvent extends Event implements Cancellable {
    private Player teleportee;
    private CommandSender teleporter;
    private MVDestination dest;
    private boolean useSafeTeleport;
    private boolean isCancelled;

    public MVTeleportEvent(MVDestination dest, Player teleportee, CommandSender teleporter, boolean safeTeleport) {
        super("SafeTTeleporter");
        this.teleportee = teleportee;
        this.teleporter = teleporter;
        this.dest = dest;
        this.useSafeTeleport = safeTeleport;
    }

    /**
     * Returns the player who will be teleported by this event.
     *
     * @return The player who will be teleported by this event.
     */
    public Player getTeleportee() {
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
    public MVDestination getDestination() {
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
