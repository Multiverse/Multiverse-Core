/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.Core;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;

import java.util.logging.Level;

/**
 * The default-implementation of {@link BlockSafety}.
 */
public class SimpleBlockSafety implements BlockSafety {
    private final Core plugin;

    public SimpleBlockSafety(Core plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockAboveAir(Location l) {
        Location downOne = l.clone();
        downOne.setY(downOne.getY() - 1);
        return (downOne.getBlock().getType() == Material.AIR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playerCanSpawnHereSafely(World world, double x, double y, double z) {
        Location l = new Location(world, x, y, z);
        return playerCanSpawnHereSafely(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playerCanSpawnHereSafely(Location l) {
        if (l == null) {
            // Can't safely spawn at a null location!
            return false;
        }

        World world = l.getWorld();
        Location actual = l.clone();
        Location upOne = l.clone();
        Location downOne = l.clone();
        upOne.setY(upOne.getY() + 1);
        downOne.setY(downOne.getY() - 1);

        if (isSolidBlock(world.getBlockAt(actual).getType())
                || isSolidBlock(upOne.getBlock().getType())) {
            MultiverseCore.staticLog(Level.FINER, "Error Here (Actual)? ("
                + actual.getBlock().getType() + ")[" + isSolidBlock(actual.getBlock().getType()) + "]");
            MultiverseCore.staticLog(Level.FINER, "Error Here (upOne)? ("
                + upOne.getBlock().getType() + ")[" + isSolidBlock(upOne.getBlock().getType()) + "]");
            return false;
        }

        if (downOne.getBlock().getType() == Material.LAVA || downOne.getBlock().getType() == Material.STATIONARY_LAVA) {
            MultiverseCore.staticLog(Level.FINER, "Error Here (downOne)? ("
                + downOne.getBlock().getType() + ")[" + isSolidBlock(downOne.getBlock().getType()) + "]");
            return false;
        }

        if (downOne.getBlock().getType() == Material.FIRE) {
            MultiverseCore.staticLog(Level.FINER, "There's fire below! ("
                + actual.getBlock().getType() + ")[" + isSolidBlock(actual.getBlock().getType()) + "]");
            return false;
        }

        if (isBlockAboveAir(actual)) {
            MultiverseCore.staticLog(Level.FINER, "Is block above air [" + isBlockAboveAir(actual) + "]");
            MultiverseCore.staticLog(Level.FINER, "Has 2 blocks of water below [" + this.hasTwoBlocksofWaterBelow(actual) + "]");
            return this.hasTwoBlocksofWaterBelow(actual);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getTopBlock(Location l) {
        Location check = l.clone();
        check.setY(127); // SUPPRESS CHECKSTYLE: MagicNumberCheck
        while (check.getY() > 0) {
            if (this.playerCanSpawnHereSafely(check)) {
                return check;
            }
            check.setY(check.getY() - 1);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getBottomBlock(Location l) {
        Location check = l.clone();
        check.setY(0);
        while (check.getY() < 127) { // SUPPRESS CHECKSTYLE: MagicNumberCheck
            if (this.playerCanSpawnHereSafely(check)) {
                return check;
            }
            check.setY(check.getY() + 1);
        }
        return null;
    }

    /*
     * If someone has a better way of this... Please either tell us, or submit a pull request!
     */
    private static boolean isSolidBlock(Material type) {
        switch (type) {
            case AIR:
                return false;
            case SNOW:
                return false;
            case TRAP_DOOR:
                return false;
            case TORCH:
                return false;
            case YELLOW_FLOWER:
                return false;
            case RED_ROSE:
                return false;
            case RED_MUSHROOM:
                return false;
            case BROWN_MUSHROOM:
                return false;
            case REDSTONE:
                return false;
            case REDSTONE_WIRE:
                return false;
            case RAILS:
                return false;
            case POWERED_RAIL:
                return false;
            case REDSTONE_TORCH_ON:
                return false;
            case REDSTONE_TORCH_OFF:
                return false;
            case DEAD_BUSH:
                return false;
            case SAPLING:
                return false;
            case STONE_BUTTON:
                return false;
            case LEVER:
                return false;
            case LONG_GRASS:
                return false;
            case PORTAL:
                return false;
            case STONE_PLATE:
                return false;
            case WOOD_PLATE:
                return false;
            case SEEDS:
                return false;
            case SUGAR_CANE_BLOCK:
                return false;
            case WALL_SIGN:
                return false;
            case SIGN_POST:
                return false;
            case WOODEN_DOOR:
                return false;
            case STATIONARY_WATER:
                return false;
            case WATER:
                return false;
            default:
                return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEntitiyOnTrack(Location l) {
        Material currentBlock = l.getBlock().getType();
        return (currentBlock == Material.POWERED_RAIL || currentBlock == Material.DETECTOR_RAIL || currentBlock == Material.RAILS);
    }

    /**
     * Checks recursively below a {@link Location} for 2 blocks of water.
     *
     * @param l The {@link Location}
     * @return Whether there are 2 blocks of water
     */
    private boolean hasTwoBlocksofWaterBelow(Location l) {
        if (l.getBlockY() < 0) {
            return false;
        }
        Location oneBelow = l.clone();
        oneBelow.subtract(0, 1, 0);
        if (oneBelow.getBlock().getType() == Material.WATER || oneBelow.getBlock().getType() == Material.STATIONARY_WATER) {
            Location twoBelow = oneBelow.clone();
            twoBelow.subtract(0, 1, 0);
            return (oneBelow.getBlock().getType() == Material.WATER || oneBelow.getBlock().getType() == Material.STATIONARY_WATER);
        }
        if (oneBelow.getBlock().getType() != Material.AIR) {
            return false;
        }
        return hasTwoBlocksofWaterBelow(oneBelow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSpawnCartSafely(Minecart cart) {
        if (this.isBlockAboveAir(cart.getLocation())) {
            return true;
        }
        if (this.isEntitiyOnTrack(plugin.getLocationManipulation().getNextBlock(cart))) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSpawnVehicleSafely(Vehicle vehicle) {
        if (this.isBlockAboveAir(vehicle.getLocation())) {
            return true;
        }
        return false;
    }

}
