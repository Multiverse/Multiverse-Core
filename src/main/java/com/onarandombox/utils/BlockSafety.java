package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class BlockSafety {
    /**
     * This function checks whether the block at the given coordinates are above air or not.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    private boolean blockIsAboveAir(Location l) {
        Location downOne = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
        downOne.setY(downOne.getY() - 1);
        return (downOne.getBlock().getType() == Material.AIR);
    }

    public boolean blockIsNotSafe(World world, double x, double y, double z) {
        Location l = new Location(world, x, y, z);
        return !playerCanSpawnHereSafely(l);
    }

    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Laval/Fire/Air etc. This also ensures there is enough space for a player to spawn!
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean playerCanSpawnHereSafely(Location l) {
        Location actual = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
        Location upOne = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
        Location downOne = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
        upOne.setY(upOne.getY() + 1);
        downOne.setY(downOne.getY() - 1);

        if (this.isNotSolidBlock(actual.getBlock().getType()) || this.isNotSolidBlock(upOne.getBlock().getType())) {
            return false;
        }

        if (downOne.getBlock().getType() == Material.LAVA) {
            return false;
        }

        if (downOne.getBlock().getType() == Material.STATIONARY_LAVA) {
            return false;
        }

        if (downOne.getBlock().getType() == Material.FIRE) {
            return false;
        }

        if (actual.getBlock().getType() == Material.FIRE) {
            return false;
        }

        if (blockIsAboveAir(actual)) {
            return false;
        }

        return true;
    }

    /**
     * If someone has a better way of this... Please either tell us, or submit a pull request!
     * 
     * @param type
     * @return
     */
    private boolean isNotSolidBlock(Material type) {
        switch (type) {
            case AIR:
                return true;
            case TRAP_DOOR:
                return true;
            case TORCH:
                return true;
            case YELLOW_FLOWER:
                return true;
            case RED_ROSE:
                return true;
            case RED_MUSHROOM:
                return true;
            case BROWN_MUSHROOM:
                return true;
            case REDSTONE:
                return true;
            case REDSTONE_WIRE:
                return true;
            case RAILS:
                return true;
            case POWERED_RAIL:
                return true;
            case REDSTONE_TORCH_ON:
                return true;
            case REDSTONE_TORCH_OFF:
                return true;
            case DEAD_BUSH:
                return true;
            case SAPLING:
                return true;
            case STONE_BUTTON:
                return true;
            case LEVER:
                return true;
            case LONG_GRASS:
                return true;
            case PORTAL:
                return true;
            case STONE_PLATE:
                return true;
            case WOOD_PLATE:
                return true;
            case SEEDS:
                return true;
            case SUGAR_CANE_BLOCK:
                return true;
            case WALL_SIGN:
                return true;
            case SIGN_POST:
                return true;
        }
        return false;
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

}
