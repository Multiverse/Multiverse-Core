package com.onarandombox.MultiverseCore.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface DestinationInstance {
    Location getLocation(Entity teleportee);

    Vector getVelocity(Entity teleportee);

    String getFinerPermissionSuffix();

    String serialise();
}
