/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;

import java.util.logging.Level;

public class BlockSafety {

    public BlockSafety() {
        // TODO Auto-generated constructor stub
    }

    /**
     * This function checks whether the block at the given coordinates are above air or not.
     */
    public boolean isBlockAboveAir(Location l) {
        Location downOne = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
        downOne.setY(downOne.getY() - 1);
        return (downOne.getBlock().getType() == Material.AIR);
    }

    public boolean blockIsNotSafe(World world, double x, double y, double z) {
        Location l = new Location(world, x, y, z);
        return !playerCanSpawnHereSafely(l);
    }

    public boolean playerCanSpawnHereSafely(World world, double x, double y, double z) {
        Location l = new Location(world, x, y, z);
        return playerCanSpawnHereSafely(l);
    }

    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Laval/Fire/Air
     * etc. This also ensures there is enough space for a player to spawn!
     *
     * @return
     */
    public boolean playerCanSpawnHereSafely(Location l) {
        World world = l.getWorld();
        Location actual = l.clone();
        Location upOne = l.clone();
        Location downOne = l.clone();
        upOne.setY(upOne.getY() + 1);
        downOne.setY(downOne.getY() - 1);

        if (this.isSolidBlock(world.getBlockAt(actual).getType()) ||
                this.isSolidBlock(upOne.getBlock().getType())) {
            MultiverseCore.staticLog(Level.FINER, "Error Here (Actual)? (" + actual.getBlock().getType() + ")[" + this.isSolidBlock(actual.getBlock().getType()) + "]");
            MultiverseCore.staticLog(Level.FINER, "Error Here (upOne)? (" + upOne.getBlock().getType() + ")[" + this.isSolidBlock(upOne.getBlock().getType()) + "]");
            return false;
        }

        if (downOne.getBlock().getType() == Material.LAVA || downOne.getBlock().getType() == Material.STATIONARY_LAVA) {
            MultiverseCore.staticLog(Level.FINER, "Error Here (downOne)? (" + downOne.getBlock().getType() + ")[" + this.isSolidBlock(downOne.getBlock().getType()) + "]");
            return false;
        }

        if (downOne.getBlock().getType() == Material.FIRE) {
            MultiverseCore.staticLog(Level.FINER, "There's fire below! (" + actual.getBlock().getType() + ")[" + this.isSolidBlock(actual.getBlock().getType()) + "]");
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
     * If someone has a better way of this... Please either tell us, or submit a pull request!
     *
     * @param type
     *
     * @return
     */
    private boolean isSolidBlock(Material type) {
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
        }
        return true;
    }

    public boolean isEntitiyOnTrack(Entity e, Location l) {
        Material currentBlock = l.getBlock().getType();
        return (currentBlock == Material.POWERED_RAIL || currentBlock == Material.DETECTOR_RAIL || currentBlock == Material.RAILS);
    }

    public void showDangers(Location l) {
        Location actual = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
        Location upOne = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
        Location downOne = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
        upOne.setY(upOne.getY() + 1);
        downOne.setY(downOne.getY() - 1);

        System.out.print("Location Up:   " + upOne.getBlock().getType());
        System.out.print("               " + upOne);
        System.out.print("Location:      " + actual.getBlock().getType());
        System.out.print("               " + actual);
        System.out.print("Location Down: " + downOne.getBlock().getType());
        System.out.print("               " + downOne);
    }

    /**
     * Checks recursively below location L for 2 blocks of water
     *
     * @param l
     *
     * @return
     */
    public boolean hasTwoBlocksofWaterBelow(Location l) {
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

    public boolean canSpawnCartSafely(Minecart cart) {

        if (this.isBlockAboveAir(cart.getLocation())) {
            return true;
        }
        if (this.isEntitiyOnTrack(cart, LocationManipulation.getNextBlock(cart))) {
            return true;
        }
        return false;
    }

    public boolean canSpawnVehicleSafely(Vehicle vehicle) {
        if (this.isBlockAboveAir(vehicle.getLocation())) {
            return true;
        }
        return false;
    }

}
