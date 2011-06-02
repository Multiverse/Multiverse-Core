package com.onarandombox.utils;

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
    public boolean blockIsAboveAir(World world, double x, double y, double z) {
        return (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1), (int) Math.floor(z)).getType() == Material.AIR);
    }
    
    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Laval/Fire/Air etc.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean blockIsNotSafe(World world, double x, double y, double z) {
        if (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)).getType() != Material.AIR || world.getBlockAt((int) Math.floor(x), (int) Math.floor(y + 1), (int) Math.floor(z)).getType() != Material.AIR)
            return true;
        
        if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1), (int) Math.floor(z)).getType() == Material.LAVA))
            return true;
        
        if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1), (int) Math.floor(z)).getType() == Material.STATIONARY_LAVA))
            return true;
        
        if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1), (int) Math.floor(z)).getType() == Material.FIRE))
            return true;
        
        if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)).getType() == Material.FIRE))
            return true;
        
        if (blockIsAboveAir(world, x, y, z))
            return true;
        
        return false;
    }
    
}
