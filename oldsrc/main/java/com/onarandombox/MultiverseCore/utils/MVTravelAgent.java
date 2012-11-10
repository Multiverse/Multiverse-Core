/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.destination.CannonDestination;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * The Multiverse-{@link TravelAgent}.
 */
public class MVTravelAgent implements TravelAgent {
    private MVDestination destination;
    private MultiverseCore core;
    private Player player;

    public MVTravelAgent(MultiverseCore multiverseCore, MVDestination d, Player p) {
        this.destination = d;
        this.core = multiverseCore;
        this.player = p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TravelAgent setSearchRadius(int radius) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSearchRadius() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TravelAgent setCreationRadius(int radius) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCreationRadius() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getCanCreatePortal() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCanCreatePortal(boolean create) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location findOrCreate(Location location) {
        return this.getSafeLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location findPortal(Location location) {
        return this.getSafeLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createPortal(Location location) {
        return false;
    }

    private Location getSafeLocation() {
        // At this time, these can never use the velocity.
        if (this.destination instanceof CannonDestination) {
            this.core.log(Level.FINE, "Using Stock TP method. This cannon will have 0 velocity");
        }
        SafeTTeleporter teleporter = this.core.getSafeTTeleporter();
        Location newLoc = this.destination.getLocation(this.player);
        if (this.destination.useSafeTeleporter()) {
            newLoc = teleporter.getSafeLocation(this.player, this.destination);
        }
        if (newLoc == null) {
            return this.player.getLocation();
        }
        return newLoc;

    }

}
