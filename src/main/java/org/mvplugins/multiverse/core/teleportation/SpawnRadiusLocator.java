package org.mvplugins.multiverse.core.teleportation;

import java.util.concurrent.ThreadLocalRandom;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

/**
 * Finds a location around a world's spawn based on the respawn radius game rule, similar to how vanilla spreads
 * players around the world spawn when they respawn.
 */
@ApiStatus.AvailableSince("5.9")
@Service
public final class SpawnRadiusLocator {
    /**
     * How many locations to try when spreading around the spawn, same as vanilla.
     */
    private static final int MAX_CANDIDATES = 1024;

    /**
     * How many chunks around the spawn chunk may be loaded while looking for a location. Candidates are checked
     * synchronously, so candidates in other chunks are only used if those chunks are already loaded.
     */
    private static final int LOADABLE_CHUNK_RANGE = 1;

    private final BlockSafety blockSafety;
    private final LocationManipulation locationManipulation;

    @Inject
    SpawnRadiusLocator(
            @NotNull BlockSafety blockSafety,
            @NotNull LocationManipulation locationManipulation) {
        this.blockSafety = blockSafety;
        this.locationManipulation = locationManipulation;
    }

    /**
     * Gets a random safe location within the respawn radius game rule of the given spawn location. Same as vanilla,
     * no spreading is done if the radius is 0, the game mode is adventure or the world has no sky light (e.g. the
     * nether and the end).
     *
     * @param spawnLocation The spawn location to spread around.
     * @param gameMode      The game mode of the world.
     * @return A safe location within the respawn radius of the spawn, or the spawn location itself if none is found.
     */
    @ApiStatus.AvailableSince("5.9")
    public @NotNull Location getLocationWithinSpawnRadius(@NotNull Location spawnLocation, @NotNull GameMode gameMode) {
        World world = spawnLocation.getWorld();
        if (world == null) {
            return spawnLocation;
        }
        int respawnRadius = getRespawnRadius(world);
        if (respawnRadius <= 0 || gameMode == GameMode.ADVENTURE || !world.hasSkyLight()) {
            Logging.finer("Not spreading spawn location for world '%s' with respawn radius %d, game mode %s"
                    + " and sky light %s.", world.getName(), respawnRadius, gameMode, world.hasSkyLight());
            return spawnLocation;
        }
        int spawnX = spawnLocation.getBlockX();
        int spawnZ = spawnLocation.getBlockZ();
        int radius = Math.max(1, Math.min(respawnRadius, getDistanceToBorder(world.getWorldBorder(), spawnX, spawnZ)));
        Logging.finer("Spreading spawn location for world '%s' with radius %d.", world.getName(), radius);

        // Go through the candidates in a random order without repeats. A random step spreads the attempts over the
        // whole area, so they are not limited to a few rows when the radius is large.
        int sideLength = radius * 2 + 1;
        long candidateCount = (long) sideLength * sideLength;
        long step = getRandomCoprime(candidateCount);
        long offset = ThreadLocalRandom.current().nextLong(candidateCount);
        long attempts = Math.min(candidateCount, MAX_CANDIDATES);
        for (long i = 0; i < attempts; i++) {
            long index = (offset + i * step) % candidateCount;
            int x = spawnX + (int) (index % sideLength) - radius;
            int z = spawnZ + (int) (index / sideLength) - radius;
            if (!canCheckCandidate(world, spawnX, spawnZ, x, z)) {
                continue;
            }
            Location candidate = getCandidate(world, spawnLocation, x, z);
            if (candidate != null) {
                Logging.fine("Using location '%s' within radius %d of spawn for world '%s'.",
                        locationManipulation.locationToString(candidate), radius, world.getName());
                return candidate;
            }
        }
        Logging.finer("No safe location found within radius %d of spawn for world '%s'.", radius, world.getName());
        return spawnLocation;
    }

    private boolean canCheckCandidate(World world, int spawnX, int spawnZ, int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        return (Math.abs(chunkX - (spawnX >> 4)) <= LOADABLE_CHUNK_RANGE
                && Math.abs(chunkZ - (spawnZ >> 4)) <= LOADABLE_CHUNK_RANGE)
                || world.isChunkLoaded(chunkX, chunkZ);
    }

    private @Nullable Location getCandidate(World world, Location spawnLocation, int x, int z) {
        int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING) + 1;
        if (y + 1 >= world.getMaxHeight()) {
            return null;
        }
        Location location = new Location(world, x + 0.5, y, z + 0.5,
                spawnLocation.getYaw(), spawnLocation.getPitch());
        return blockSafety.canSpawnAtLocationSafely(location) && !isInLiquid(location) ? location : null;
    }

    private boolean isInLiquid(Location location) {
        Block block = location.getBlock();
        return block.isLiquid() || block.getRelative(0, 1, 0).isLiquid();
    }

    private long getRandomCoprime(long number) {
        if (number <= 2) {
            return 1;
        }
        long coprime = ThreadLocalRandom.current().nextLong(1, number);
        while (gcd(coprime, number) != 1) {
            coprime = coprime % (number - 1) + 1;
        }
        return coprime;
    }

    private long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private int getRespawnRadius(World world) {
        // Older servers only have the spawnRadius game rule, while newer ones renamed it to respawn_radius.
        GameRule<?> respawnRadiusRule = GameRule.getByName("respawn_radius");
        if (respawnRadiusRule == null) {
            respawnRadiusRule = GameRule.getByName("spawnRadius");
        }
        if (respawnRadiusRule == null) {
            return 0;
        }
        Object value = world.getGameRuleValue(respawnRadiusRule);
        return value instanceof Integer ? Math.max(0, (Integer) value) : 0;
    }

    private int getDistanceToBorder(WorldBorder worldBorder, int x, int z) {
        double halfSize = worldBorder.getSize() / 2.0;
        double minX = worldBorder.getCenter().getX() - halfSize;
        double maxX = worldBorder.getCenter().getX() + halfSize;
        double minZ = worldBorder.getCenter().getZ() - halfSize;
        double maxZ = worldBorder.getCenter().getZ() + halfSize;
        double distance = Math.min(
                Math.min(x - minX, maxX - x),
                Math.min(z - minZ, maxZ - z));
        return (int) Math.floor(distance);
    }
}
