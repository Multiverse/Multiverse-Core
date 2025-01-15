package org.mvplugins.multiverse.core.teleportation;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

@Service
public class AdvancedBlockSafety {

    // This will search a maximum of 7 * 6 * 7 = 294 blocks
    public static final int DEFAULT_HORIZONTAL_RANGE = 3;
    public static final int DEFAULT_VERTICAL_RANGE = 2;

    @Inject
    private AdvancedBlockSafety() {
    }

    public boolean playerCanSpawnSafelyAt(@NotNull Location location) {
        return playerCanSpawnSafelyAt(location.getBlock());
    }

    public boolean playerCanSpawnSafelyAt(@NotNull Block block) {
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

    @Nullable
    public Location adjustSafeSpawnLocation(@NotNull Location location) {
        return adjustSafeSpawnLocation(location, DEFAULT_HORIZONTAL_RANGE, DEFAULT_VERTICAL_RANGE);
    }

    @Nullable
    public Location adjustSafeSpawnLocation(@NotNull Location location, int horizontalRange, int verticalRange) {
        Block safeBlock = adjustSafeSpawnBlock(location.getBlock(), horizontalRange, verticalRange);
        if (safeBlock == null) {
            return null;
        }
        Location safeLocation = safeBlock.getLocation();
        // Adjust to center of block
        safeLocation.add(0.5, 0, 0.5);
        return safeLocation;
    }

    @Nullable
    public Block adjustSafeSpawnBlock(@NotNull Block block) {
        return adjustSafeSpawnBlock(block, DEFAULT_HORIZONTAL_RANGE, DEFAULT_VERTICAL_RANGE);
    }

    @Nullable
    public Block adjustSafeSpawnBlock(@NotNull Block block, int horizontalRange, int verticalRange) {
        Block searchResult = searchAroundXZ(block, horizontalRange);
        if (searchResult != null) {
            return searchResult;
        }
        for (int i = 1; i <= verticalRange; i++) {
            searchResult = searchAroundXZ(block.getRelative(0, i, 0), horizontalRange);
            if (searchResult != null) {
                return searchResult;
            }
            searchResult = searchAroundXZ(block.getRelative(0, -i, 0), horizontalRange);
            if (searchResult != null) {
                return searchResult;
            }
        }
        return null;
    }

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

    @Nullable
    private Block searchPlusMinusPermutation(Block block, int x, int z) {
        Block relative = block.getRelative(-x, 0, z);
        if (playerCanSpawnSafelyAt(relative)) {
            return relative;
        }
        if (x != 0) {
            relative = block.getRelative(-x, 0, -z);
            if (playerCanSpawnSafelyAt(relative)) {
                return relative;
            }
        }
        relative = block.getRelative(x, 0, z);
        if (playerCanSpawnSafelyAt(relative)) {
            return relative;
        }
        if (z != 0) {
            relative = block.getRelative(x, 0, -z);
            if (playerCanSpawnSafelyAt(relative)) {
                return relative;
            }
        }
        return null;
    }
}
