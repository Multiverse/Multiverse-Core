package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;

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
        Location actual = l.clone();
        Location upOne = l.clone();
        Location downOne = l.clone();
        upOne.setY(upOne.getY() + 1);
        downOne.setY(downOne.getY() - 1);

        if (this.isSolidBlock(actual.getBlock().getType()) || this.isSolidBlock(upOne.getBlock().getType())) {
            System.out.print("On or Above is not safe");
            return false;
        }

        if (downOne.getBlock().getType() == Material.LAVA || downOne.getBlock().getType() == Material.STATIONARY_LAVA) {
            System.out.print("Lava Below");
            return false;
        }

        if (downOne.getBlock().getType() == Material.FIRE) {
            System.out.print("Fire Below");
            return false;
        }

        if (blockIsAboveAir(actual)) {
            System.out.print("Above Air");
            return false;
        }
        System.out.print("All Good!");
        return true;
    }

    /**
     * If someone has a better way of this... Please either tell us, or submit a pull request!
     * 
     * @param type
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

}
