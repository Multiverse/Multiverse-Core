package com.onarandombox.MultiverseCore.api;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.onarandombox.MultiverseCore.enums.TeleportResult;

/**
 * Used to safely teleport people.
 */
public interface SafeTTeleporter extends Teleporter {

    /**
     * Gets the next safe location around the given location.
     * @param l A {@link Location}.
     * @return A safe {@link Location}.
     */
    Location getSafeLocation(Location l);

    /**
     * Gets the next safe location around the given location.
     * @param l A {@link Location}.
     * @param tolerance The tolerance.
     * @param radius The radius.
     * @return A safe {@link Location}.
     */
    Location getSafeLocation(Location l, int tolerance, int radius);

    /**
     * Safely teleport the entity to the MVDestination. This will perform checks to see if the place is safe, and if
     * it's not, will adjust the final destination accordingly.
     *
     * @param teleporter Person who performed the teleport command.
     * @param teleportee Entity to teleport
     * @param d          Destination to teleport them to
     * @return true for success, false for failure
     */
    TeleportResult safelyTeleport(CommandSender teleporter, Entity teleportee, MVDestination d);

    /**
     * Safely teleport the entity to the Location. This may perform checks to
     * see if the place is safe, and if
     * it's not, will adjust the final destination accordingly.
     *
     * @param teleporter Person who issued the teleport command.
     * @param teleportee Entity to teleport.
     * @param location   Location to teleport them to.
     * @param safely     Should the destination be checked for safety before teleport?
     * @return true for success, false for failure.
     */
    TeleportResult safelyTeleport(CommandSender teleporter, Entity teleportee, Location location,
            boolean safely);

    /**
     * Returns a safe location for the entity to spawn at.
     *
     * @param e The entity to spawn
     * @param d The MVDestination to take the entity to.
     * @return A new location to spawn the entity at.
     */
    Location getSafeLocation(Entity e, MVDestination d);

    /**
     * Finds a portal-block next to the specified {@link Location}.
     * @param l The {@link Location}
     * @return The next portal-block's {@link Location}.
     */
    Location findPortalBlockNextTo(Location l);

}
