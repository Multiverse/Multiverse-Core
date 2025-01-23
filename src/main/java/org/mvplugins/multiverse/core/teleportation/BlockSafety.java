package org.mvplugins.multiverse.core.teleportation;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.config.MVCoreConfig;

/**
 * Used to check get or find block/location-related information.
 */
@Service
public final class BlockSafety {

    private final MVCoreConfig config;
    private final LocationManipulation locationManipulation;

    @Inject
    BlockSafety(@NotNull MVCoreConfig config, @NotNull LocationManipulation locationManipulation) {
        this.config = config;
        this.locationManipulation = locationManipulation;
    }

    /**
     * Function to check if a block is above air.
     *
     * @param location  The location to check.
     * @return True if the block at that {@link Location} is above air.
     */
    public boolean isBlockAboveAir(Location location) {
        return location.getBlock().getRelative(0, -1, 0).getType().isAir();
    }

    /**
     * Checks if an entity would be on track at the specified {@link Location}.
     *
     * @param location  The location to check.
     * @return True if an entity would be on tracks at the specified {@link Location}.
     */
    public boolean isEntityOnTrack(Location location) {
        return location.getBlock().getBlockData() instanceof Rail;
    }

    /**
     * Gets the location of the highest spawnable block (i.e. y-axis) at the specified {@link Location}.
     *
     * @param location  The location
     * @return The location if found, null of all blocks are unsafe.
     */
    public Location getTopBlock(Location location) {
        Location check = location.clone();
        int maxHeight = Option.of(location.getWorld()).map(World::getMaxHeight).getOrElse(127);
        check.setY(maxHeight);
        while (check.getY() > 0) {
            if (canSpawnAtLocationSafely(check)) {
                return check;
            }
            check.setY(check.getY() - 1);
        }
        return null;
    }

    /**
     * Gets the location of the lowest spawnable block (i.e. y-axis) at the specified {@link Location}.
     *
     * @param location  The location
     * @return The location if found, null of all blocks are unsafe.
     */
    public Location getBottomBlock(Location location) {
        Location check = location.clone();
        int minHeight = Option.of(location.getWorld()).map(World::getMinHeight).getOrElse(0);
        check.setY(minHeight);
        while (check.getY() < 127) { // SUPPRESS CHECKSTYLE: MagicNumberCheck
            if (canSpawnAtLocationSafely(check)) {
                return check;
            }
            check.setY(check.getY() + 1);
        }
        return null;
    }

    /**
     * Checks if the specified {@link Minecart} can spawn safely.
     *
     * @param cart The {@link Minecart}.
     * @return True if the minecart can spawn safely.
     */
    public boolean canSpawnCartSafely(Minecart cart) {
        if (isBlockAboveAir(cart.getLocation())) {
            return true;
        }
        return isEntityOnTrack(locationManipulation.getNextBlock(cart));
    }

    /**
     * Checks if the specified {@link Vehicle} can spawn safely.
     *
     * @param vehicle The {@link Vehicle}.
     * @return True if the vehicle can spawn safely.
     */
    public boolean canSpawnVehicleSafely(Vehicle vehicle) {
        return isBlockAboveAir(vehicle.getLocation());
    }

    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Lava/Fire/Air
     * etc. This also ensures there is enough space for a player to spawn!
     *
     * @param location  The {@link Location}
     * @return Whether the player can spawn safely at the given {@link Location}
     */
    public boolean canSpawnAtLocationSafely(@NotNull Location location) {
        return canSpawnAtBlockSafely(location.getBlock());
    }

    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Lava/Fire/Air
     * etc. This also ensures there is enough space for a player to spawn!
     *
     * @param block The {@link Block}
     * @return Whether the player can spawn safely at the given {@link Location}
     */
    public boolean canSpawnAtBlockSafely(@NotNull Block block) {
        Logging.finest("Checking spawn safety for location: %s, %s, %s", block.getX(), block.getY(), block.getZ());
        if (isUnsafeSpawnBody(block)) {
            // Player body will be stuck in solid
            Logging.finest("Unsafe location for player's body.");
            return false;
        }
        Block airBlockForHead = block.getRelative(0, 1, 0);
        if (isUnsafeSpawnBody(airBlockForHead)) {
            // Player's head will be stuck in solid
            Logging.finest("Unsafe location for player's head.");
            return false;
        }
        Block standingOnBlock = block.getRelative(0, -1, 0);
        if (isUnsafeSpawnPlatform(standingOnBlock)) {
            // Player will drop down
            Logging.finest("Unsafe location due to invalid platform.");
            return false;
        }
        Logging.finest("Location is safe.");
        return true;
    }

    /**
     * Player's body must be in non-solid block that is non-harming.
     *
     * @param block The block
     * @return True if the block is unsafe
     */
    private boolean isUnsafeSpawnBody(@NotNull Block block) {
        Material blockMaterial = block.getType();
        return blockMaterial.isSolid() || blockMaterial == Material.FIRE;
    }

    /**
     * Player must stand on solid ground, or water that is only 1 block deep to prevent drowning.
     *
     * @param block The block
     * @return True if the block is unsafe
     */
    private boolean isUnsafeSpawnPlatform(@NotNull Block block) {
        return !block.getType().isSolid() || isDeepWater(block);
    }

    /**
     * Water that is 2 or more block deep
     *
     * @param block The block
     * @return True if the block is unsafe
     */
    private boolean isDeepWater(@NotNull Block block) {
        if (block.getType() != Material.WATER) {
            return false;
        }
        return block.getRelative(0, -1, 0).getType() == Material.WATER;
    }

    /**
     * Finds the closest possible safe location around the given location with the configured search radius.
     *
     * @param location  The target location to find
     * @return The safe location if found, otherwise null.
     */
    public @Nullable Location findSafeSpawnLocation(@NotNull Location location) {
        return findSafeSpawnLocation(
                location,
                config.getSafeLocationHorizontalSearchRadius(),
                config.getSafeLocationVerticalSearchRadius());
    }

    /**
     * Finds the closest possible safe location around the given location.
     *
     * @param location          The target location to find
     * @param horizontalRange   The radius around x,z of given location to search.
     * @param verticalRange     The height of how far up and down to search.
     * @return The safe location if found, otherwise null.
     */
    public @Nullable Location findSafeSpawnLocation(@NotNull Location location, int horizontalRange, int verticalRange) {
        Block safeBlock = findSafeSpawnBlock(location.getBlock(), horizontalRange, verticalRange);
        if (safeBlock == null) {
            return null;
        }
        return new Location(
                location.getWorld(),
                safeBlock.getX() + 0.5,
                safeBlock.getY(),
                safeBlock.getZ() + 0.5,
                location.getYaw(),
                location.getPitch());
    }

    /**
     * Finds the closest possible location around the given block with the configured search radius.
     *
     * @param block The target block to find
     * @return The safe block if found, otherwise null.
     */
    public @Nullable Block findSafeSpawnBlock(@NotNull Block block) {
        return findSafeSpawnBlock(
                block,
                config.getSafeLocationHorizontalSearchRadius(),
                config.getSafeLocationVerticalSearchRadius());
    }

    /**
     * Finds the closest possible location around the given block.
     *
     * @param block             The target block to find
     * @param horizontalRange   The radius around x,z of given block to search.
     * @param verticalRange     The height of how far up and down to search.
     * @return The safe block if found, otherwise null.
     */
    public @Nullable Block findSafeSpawnBlock(@NotNull Block block, int horizontalRange, int verticalRange) {
        Block searchResult = searchAroundXZ(block, horizontalRange);
        if (searchResult != null) {
            return searchResult;
        }
        int maxHeight = block.getWorld().getMaxHeight();
        int minHeight = block.getWorld().getMinHeight();
        for (int i = 1; i <= verticalRange; i++) {
            if (block.getY() + i < maxHeight) {
                searchResult = searchAroundXZ(block.getRelative(0, i, 0), horizontalRange);
                if (searchResult != null) {
                    return searchResult;
                }
            }
            if (block.getY() - i >= minHeight) {
                searchResult = searchAroundXZ(block.getRelative(0, -i, 0), horizontalRange);
                if (searchResult != null) {
                    return searchResult;
                }
            }
        }
        return null;
    }

    /**
     * Search a square from n - radius to n + radius for both x and z
     *
     * @param block     The block to be relative to
     * @param radius    The number of blocks +/- x and z to search
     * @return The safe block, or null
     */
    @Nullable
    private Block searchAroundXZ(Block block, int radius) {
        if (canSpawnAtBlockSafely(block)) {
            return block;
        }
        for (int r = 1; r <= radius; r++) {
            boolean radiusX = true;
            boolean incrementOffset = false;
            int offset = 0;
            int noOfIterations = r * 2 + 1;
            for (int i = 0; i < noOfIterations; i++) {
                Block searchResult = radiusX
                        ? searchPlusMinusPermutation(block, r, offset)
                        : searchPlusMinusPermutation(block, offset, r);
                if (searchResult != null) {
                    return searchResult;
                }
                if (incrementOffset) {
                    offset++;
                }
                radiusX = !radiusX;
                incrementOffset = !incrementOffset;
            }
        }
        return null;
    }

    /**
     * Search 4 relative blocks with the following offsets: (-x, -z) (-x, z) (x, -z) (x, z)
     *
     * @param block The block to be relative to
     * @param x     Amount to offset for the x axis
     * @param z     Amount to offset for the z axis
     * @return The safe block, or null
     */
    @Nullable
    private Block searchPlusMinusPermutation(Block block, int x, int z) {
        Block relative = block.getRelative(-x, 0, -z);
        if (canSpawnAtBlockSafely(relative)) {
            return relative;
        }
        if (z != 0) {
            relative = block.getRelative(-x, 0, z);
            if (canSpawnAtBlockSafely(relative)) {
                return relative;
            }
        }
        if (x != 0) {
            relative = block.getRelative(x, 0, -z);
            if (canSpawnAtBlockSafely(relative)) {
                return relative;
            }
            if (z != 0) {
                relative = block.getRelative(x, 0, z);
                if (canSpawnAtBlockSafely(relative)) {
                    return relative;
                }
            }
        }
        return null;
    }

    /**
     * Finds a portal-block next to the specified {@link Location}.
     *
     * @param location  The {@link Location}
     * @return The next portal-block's {@link Location} if found, otherwise null.
     */
    public @Nullable Location findPortalBlockNextTo(Location location) {
        if (location.getWorld() == null) {
            return null;
        }
        Block b = location.getWorld().getBlockAt(location);
        Location foundLocation = null;
        if (b.getType() == Material.NETHER_PORTAL) {
            return location;
        }
        if (b.getRelative(BlockFace.NORTH).getType() == Material.NETHER_PORTAL) {
            foundLocation = getCloserBlock(location, b.getRelative(BlockFace.NORTH).getLocation(), foundLocation);
        }
        if (b.getRelative(BlockFace.SOUTH).getType() == Material.NETHER_PORTAL) {
            foundLocation = getCloserBlock(location, b.getRelative(BlockFace.SOUTH).getLocation(), foundLocation);
        }
        if (b.getRelative(BlockFace.EAST).getType() == Material.NETHER_PORTAL) {
            foundLocation = getCloserBlock(location, b.getRelative(BlockFace.EAST).getLocation(), foundLocation);
        }
        if (b.getRelative(BlockFace.WEST).getType() == Material.NETHER_PORTAL) {
            foundLocation = getCloserBlock(location, b.getRelative(BlockFace.WEST).getLocation(), foundLocation);
        }
        return foundLocation;
    }

    private Location getCloserBlock(Location source, Location blockA, Location blockB) {
        // If B wasn't given, return a.
        if (blockB == null) {
            return blockA;
        }
        // Center our calculations
        blockA.add(.5, 0, .5);
        blockB.add(.5, 0, .5);

        // Retrieve the distance to the normalized blocks
        double testA = source.distance(blockA);
        double testB = source.distance(blockB);

        // Compare and return
        if (testA <= testB) {
            return blockA;
        }
        return blockB;
    }
}
