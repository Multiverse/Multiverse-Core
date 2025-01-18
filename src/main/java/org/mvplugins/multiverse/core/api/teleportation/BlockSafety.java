package org.mvplugins.multiverse.core.api.teleportation;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Contract;

/**
 * Used to check get or find block/location-related information.
 *
 * @since 5.0
 */
@Contract
public interface BlockSafety {
    /**
     * Function to check if a block is above air.
     *
     * @param location  The location to check.
     * @return True if the block at that {@link Location} is above air.
     * @since 5.0
     */
    boolean isBlockAboveAir(Location location);

    /**
     * Checks if an entity would be on track at the specified {@link Location}.
     *
     * @param location  The location to check.
     * @return True if an entity would be on tracks at the specified {@link Location}.
     * @since 5.0
     */
    boolean isEntityOnTrack(Location location);

    /**
     * Gets the location of the highest spawnable block (i.e. y-axis) at the specified {@link Location}.
     *
     * @param location  The location
     * @return The location if found, null of all blocks are unsafe.
     * @since 5.0
     */
    Location getTopBlock(Location location);

    /**
     * Gets the location of the lowest spawnable block (i.e. y-axis) at the specified {@link Location}.
     *
     * @param location  The location
     * @return The location if found, null of all blocks are unsafe.
     * @since 5.0
     */
    Location getBottomBlock(Location location);

    /**
     * Checks if the specified {@link Minecart} can spawn safely.
     *
     * @param cart The {@link Minecart}.
     * @return True if the minecart can spawn safely.
     * @since 5.0
     */
    boolean canSpawnCartSafely(Minecart cart);

    /**
     * Checks if the specified {@link Vehicle} can spawn safely.
     *
     * @param vehicle The {@link Vehicle}.
     * @return True if the vehicle can spawn safely.
     * @since 5.0
     */
    boolean canSpawnVehicleSafely(Vehicle vehicle);

    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Lava/Fire/Air
     * etc. This also ensures there is enough space for a player to spawn!
     *
     * @param location  The {@link Location}
     * @return Whether the player can spawn safely at the given {@link Location}
     * @since 5.0
     */
    boolean canSpawnAtLocationSafely(@NotNull Location location);

    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Lava/Fire/Air
     * etc. This also ensures there is enough space for a player to spawn!
     *
     * @param block The {@link Block}
     * @return Whether the player can spawn safely at the given {@link Location}
     * @since 5.0
     */
    boolean canSpawnAtBlockSafely(@NotNull Block block);

    /**
     * Finds the closest possible safe location around the given location with the configured search radius.
     *
     * @param location  The target location to find
     * @return The safe location if found, otherwise null.
     * @since 5.0
     */
    @Nullable Location findSafeSpawnLocation(@NotNull Location location);

    /**
     * Finds the closest possible safe location around the given location.
     *
     * @param location          The target location to find
     * @param horizontalRange   The radius around x,z of given location to search.
     * @param verticalRange     The height of how far up and down to search.
     * @return The safe location if found, otherwise null.
     * @since 5.0
     */
    @Nullable Location findSafeSpawnLocation(@NotNull Location location, int horizontalRange, int verticalRange);

    /**
     * Finds the closest possible location around the given block with the configured search radius.
     *
     * @param block The target block to find
     * @return The safe block if found, otherwise null.
     * @since 5.0
     */
    @Nullable Block findSafeSpawnBlock(@NotNull Block block);

    /**
     * Finds the closest possible location around the given block.
     *
     * @param block             The target block to find
     * @param horizontalRange   The radius around x,z of given block to search.
     * @param verticalRange     The height of how far up and down to search.
     * @return The safe block if found, otherwise null.
     * @since 5.0
     */
    @Nullable Block findSafeSpawnBlock(@NotNull Block block, int horizontalRange, int verticalRange);

    /**
     * Finds a portal-block next to the specified {@link Location}.
     *
     * @param location  The {@link Location}
     * @return The next portal-block's {@link Location} if found, otherwise null.
     * @since 5.0
     */
    @Nullable Location findPortalBlockNextTo(Location location);
}
