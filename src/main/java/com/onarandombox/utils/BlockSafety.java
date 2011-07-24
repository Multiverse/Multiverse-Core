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

        if (/*actual.getBlock().getType() != Material.AIR || */upOne.getBlock().getType() != Material.AIR) {
            System.out.print("Air!");
            return false;
        }

        if (downOne.getBlock().getType() == Material.LAVA) {
            System.out.print("Lava!");
            return false;
        }

        if (downOne.getBlock().getType() == Material.STATIONARY_LAVA) {
            System.out.print("Lava!!");
            return false;
        }

        if (downOne.getBlock().getType() == Material.FIRE) {
            System.out.print("Fire Below!");
            return false;
        }

        if (actual.getBlock().getType() == Material.FIRE) {
            System.out.print("Fire!");
            return false;
        }

        if (blockIsAboveAir(actual)) {
            System.out.print("Above Air!");
            // FOR NOW THIS IS OK
            // TODO: Take out once the other one is fixed.
            return true;
        }

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
