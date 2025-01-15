package org.mvplugins.multiverse.core.teleportation;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.api.LocationManipulation;

/**
 *
 */
@Service
public class AdvancedBlockSafety {

    // This will search a maximum of 7 * 6 * 7 = 294 blocks
    public static final int DEFAULT_HORIZONTAL_RANGE = 3;
    public static final int DEFAULT_VERTICAL_RANGE = 2;

    private final LocationManipulation locationManipulation;

    @Inject
    AdvancedBlockSafety(@NotNull LocationManipulation locationManipulation) {
        this.locationManipulation = locationManipulation;
    }

    public boolean playerCanSpawnSafelyAt(@NotNull Location location) {
        return playerCanSpawnSafelyAt(location.getBlock());
    }

    public boolean playerCanSpawnSafelyAt(@NotNull Block block) {
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
     * @param block
     * @return
     */
    private boolean isUnsafeSpawnBody(@NotNull Block block) {
        Material blockMaterial = block.getType();
        return blockMaterial.isSolid() || blockMaterial == Material.FIRE;
    }

    /**
     * Player must stand on solid ground, or water that is only 1 block deep to prevent drowning.
     *
     * @param block
     * @return
     */
    private boolean isUnsafeSpawnPlatform(@NotNull Block block) {
        return !block.getType().isSolid() || isDeepWater(block);
    }

    /**
     * Water that is 2 or more block deep
     *
     * @param block
     * @return
     */
    private boolean isDeepWater(@NotNull Block block) {
        if (block.getType() != Material.WATER) {
            return false;
        }
        return block.getRelative(0, -1, 0).getType() == Material.WATER;
    }

    /**
     *
     *
     * @param location
     * @return The safe location, or null
     */
    @Nullable
    public Location adjustSafeSpawnLocation(@NotNull Location location) {
        return adjustSafeSpawnLocation(location, DEFAULT_HORIZONTAL_RANGE, DEFAULT_VERTICAL_RANGE);
    }

    /**
     *
     * @param location
     * @param horizontalRange
     * @param verticalRange
     * @return The safe location, or null
     */
    @Nullable
    public Location adjustSafeSpawnLocation(@NotNull Location location, int horizontalRange, int verticalRange) {
        Block safeBlock = adjustSafeSpawnBlock(location.getBlock(), horizontalRange, verticalRange);
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
     *
     * @param block
     * @return The safe block, or null
     */
    @Nullable
    public Block adjustSafeSpawnBlock(@NotNull Block block) {
        return adjustSafeSpawnBlock(block, DEFAULT_HORIZONTAL_RANGE, DEFAULT_VERTICAL_RANGE);
    }

    /**
     *
     * @param block
     * @param horizontalRange
     * @param verticalRange
     * @return The safe block, or null
     */
    @Nullable
    public Block adjustSafeSpawnBlock(@NotNull Block block, int horizontalRange, int verticalRange) {
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
     * @param block
     * @param radius
     * @return The safe block, or null
     */
    @Nullable
    private Block searchAroundXZ(Block block, int radius) {
        if (playerCanSpawnSafelyAt(block)) {
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
        if (playerCanSpawnSafelyAt(relative)) {
            return relative;
        }
        if (z != 0) {
            relative = block.getRelative(-x, 0, z);
            if (playerCanSpawnSafelyAt(relative)) {
                return relative;
            }
        }
        if (x != 0) {
            relative = block.getRelative(x, 0, -z);
            if (playerCanSpawnSafelyAt(relative)) {
                return relative;
            }
            if (z != 0) {
                relative = block.getRelative(x, 0, z);
                if (playerCanSpawnSafelyAt(relative)) {
                    return relative;
                }
            }
        }
        return null;
    }

    public boolean isBlockAboveAir(Location location) {
        return location.getBlock().getRelative(0, -1, 0).getType().isAir();
    }

    /**
     *
     * @param location
     * @return
     */
    public boolean isEntityOnTrack(Location location) {
        return location.getBlock().getBlockData() instanceof Rail;
    }

    /**
     *
     * @param cart
     * @return
     */
    public boolean canSpawnCartSafely(Minecart cart) {
        if (isBlockAboveAir(cart.getLocation())) {
            return true;
        }
        return isEntityOnTrack(locationManipulation.getNextBlock(cart));
    }

    /**
     *
     * @param vehicle
     * @return
     */
    public boolean canSpawnVehicleSafely(Vehicle vehicle) {
        return isBlockAboveAir(vehicle.getLocation());
    }
}
