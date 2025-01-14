package org.mvplugins.multiverse.core.teleportation;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

@Service
public class AdvancedBlockSafety {

    @Inject
    private AdvancedBlockSafety() {
    }

    private boolean playerCanSpawnHereSafely(@NotNull Location location) {
        if (isUnsafeSpawnBody(location)) {
            // Player body will be stuck in solid
            Logging.finest("Unsafe location for player's body.");
            return false;
        }
        Location airBlockForHead = offsetLocation(location, 0, 1, 0);
        if (isUnsafeSpawnBody(airBlockForHead)) {
            // Player's head will be stuck in solid
            Logging.finest("Unsafe location for player's head.");
            return false;
        }
        Location standingOnBlock = offsetLocation(location, 0, -1, 0);
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
     * @param location
     * @return
     */
    private boolean isUnsafeSpawnBody(@NotNull Location location) {
        Material blockMaterial = location.getBlock().getType();
        return blockMaterial.isSolid() || blockMaterial == Material.FIRE;
    }

    /**
     * Player must stand on solid ground, or water that is only 1 block deep to prevent drowning.
     *
     * @param location
     * @return
     */
    private boolean isUnsafeSpawnPlatform(@NotNull Location location) {
        return !location.getBlock().getType().isSolid() || isDeepWater(location);
    }

    /**
     * Water that is 2 or more block deep
     *
     * @param location
     * @return
     */
    private boolean isDeepWater(@NotNull Location location) {
        if (location.getBlock().getType() != Material.WATER) {
            return false;
        }
        return offsetLocation(location, 0, -1, 0).getBlock().getType() == Material.WATER;
    }

    @Nullable
    public Location adjustSafeSpawnLocation(@NotNull Location location) {
        return adjustSafeSpawnLocation(location, 3, 2);
    }

    @Nullable
    public Location adjustSafeSpawnLocation(@NotNull Location location, int horizontal, int vertical) {
        int[] horizontalSpan = rangeSpan(horizontal);
        int[] verticalSpan = rangeSpan(vertical);

        for(int y : verticalSpan) {
            for (int x : horizontalSpan) {
                for (int z : horizontalSpan) {
                    Logging.finest("Checking offset: %s, %s, %s", x, y, z);
                    Location offsetLocation = offsetLocation(location, x, y, z);
                    if (playerCanSpawnHereSafely(offsetLocation)) {
                        // Set location to the center of the block
                        offsetLocation.setX(offsetLocation.getBlockX() + 0.5);
                        offsetLocation.setZ(offsetLocation.getBlockZ() + 0.5);
                        return offsetLocation;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Plus minus range from 0, starting with the closer offset.
     * E.g. 0, 1, -1, 2, -2...
     *
     * @param number
     * @return
     */
    private int[] rangeSpan(int number) {
        int[] numArray = new int[number * 2 + 1];
        numArray[0] = 0;
        for (int i = 1; i <= number; i++) {
            numArray[i * 2 - 1] = i;
            numArray[i * 2] = -i;
        }
        return numArray;
    }

    /**
     * Clones and creates a new location with the given offset.
     *
     * @param location
     * @param x
     * @param y
     * @param z
     * @return
     */
    @NotNull
    private Location offsetLocation(@NotNull Location location, double x, double y, double z) {
        Location newLocation = location.clone();
        newLocation.add(x, y, z);
        return newLocation;
    }
}
