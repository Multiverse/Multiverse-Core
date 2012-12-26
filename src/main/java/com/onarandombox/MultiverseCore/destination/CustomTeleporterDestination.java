package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.api.Teleporter;
import com.onarandombox.MultiverseCore.api.MVDestination;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class CustomTeleporterDestination implements MVDestination {
    @Override
    public final Location getLocation(final Entity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Vector getVelocity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean useSafeTeleporter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract String toString();

    public abstract Teleporter getTeleporter();
}
