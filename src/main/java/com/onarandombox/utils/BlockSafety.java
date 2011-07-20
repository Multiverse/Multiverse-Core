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

        if (actual.getBlock().getType() != Material.AIR || upOne.getBlock().getType() != Material.AIR)
            return false;

        if (downOne.getBlock().getType() == Material.LAVA)
            return false;

        if (downOne.getBlock().getType() == Material.STATIONARY_LAVA)
            return false;

        if (downOne.getBlock().getType() == Material.FIRE)
            return false;

        if (actual.getBlock().getType() == Material.FIRE)
            return false;

        if (blockIsAboveAir(actual))
            return false;

        return true;
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
