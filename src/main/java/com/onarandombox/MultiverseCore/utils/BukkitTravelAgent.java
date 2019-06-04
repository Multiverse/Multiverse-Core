package com.onarandombox.MultiverseCore.utils;

import java.util.logging.Level;

import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.destination.CannonDestination;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.event.player.PlayerPortalEvent;

public class BukkitTravelAgent implements TravelAgent {
    private final MVTravelAgent agent;

    public BukkitTravelAgent(MVTravelAgent agent) {
        this.agent = agent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BukkitTravelAgent setSearchRadius(int radius) {
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
    public BukkitTravelAgent setCreationRadius(int radius) {
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
        if (agent.destination instanceof CannonDestination) {
            agent.core.log(Level.FINE, "Using Stock TP method. This cannon will have 0 velocity");
        }
        SafeTTeleporter teleporter = agent.core.getSafeTTeleporter();
        Location newLoc = agent.destination.getLocation(agent.player);
        if (agent.destination.useSafeTeleporter()) {
            newLoc = teleporter.getSafeLocation(agent.player, agent.destination);
        }
        if (newLoc == null) {
            return agent.player.getLocation();
        }
        return newLoc;

    }

    public void setPortalEventTravelAgent(PlayerPortalEvent event) {
        event.setPortalTravelAgent(this);
        event.useTravelAgent(true);
    }
}
