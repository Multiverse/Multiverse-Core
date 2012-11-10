package com.onarandombox.MultiverseCore.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;

/**
 * Used to get block/location-related information.
 */
public interface BlockSafety {
    /**
     * This function checks whether the block at the given coordinates are above air or not.
     * @param l The {@link Location} of the block.
     * @return True if the block at that {@link Location} is above air.
     */
    boolean isBlockAboveAir(Location l);

    /**
     * Checks if a player can spawn safely at the given coordinates.
     * @param world The {@link World}.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @return True if a player can spawn safely at the given coordinates.
     */
    boolean playerCanSpawnHereSafely(World world, double x, double y, double z);

    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Lava/Fire/Air
     * etc. This also ensures there is enough space for a player to spawn!
     *
     * @param l The {@link Location}
     * @return Whether the player can spawn safely at the given {@link Location}
     */
    boolean playerCanSpawnHereSafely(Location l);

    /**
     * Gets a safe bed spawn location OR null if the bed is invalid.
     * @param l The location of the bead head (block with the pillow on it).
     * @return Safe location around the bed or null if no location was found.
     */
    Location getSafeBedSpawn(Location l);

    /**
     * Gets the location of the top block at the specified {@link Location}.
     * @param l Any {@link Location}.
     * @return The {@link Location} of the top-block.
     */
    Location getTopBlock(Location l);

    /**
     * Gets the location of the top block at the specified {@link Location}.
     * @param l Any {@link Location}.
     * @return The {@link Location} of the top-block.
     */
    Location getBottomBlock(Location l);

    /**
     * Checks if an entity would be on track at the specified {@link Location}.
     * @param l The {@link Location}.
     * @return True if an entity would be on tracks at the specified {@link Location}.
     */
    boolean isEntitiyOnTrack(Location l);

    /**
     * Checks if the specified {@link Minecart} can spawn safely.
     * @param cart The {@link Minecart}.
     * @return True if the minecart can spawn safely.
     */
    boolean canSpawnCartSafely(Minecart cart);

    /**
     * Checks if the specified {@link Vehicle} can spawn safely.
     * @param vehicle The {@link Vehicle}.
     * @return True if the vehicle can spawn safely.
     */
    boolean canSpawnVehicleSafely(Vehicle vehicle);
}
