package com.onarandombox.MultiVerseCore;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.World.Environment;
import org.bukkit.Material;

public class MVTeleport {

	MultiVerseCore plugin;
    
    public MVTeleport(MultiVerseCore plugin) {
        this.plugin = plugin;
    }
    
	/**
	 * This function gets a safe place to teleport to.
	 * 
	 * @param world
	 * @param player
	 * @return
	 */
	public Location getDestination(World world, Player player) {
		
		Location location = player.getLocation();
		
		double srcComp = plugin.worlds.get(player.getWorld().getName()).compression;
		double trgComp = plugin.worlds.get(world.getName()).compression;
		
		double x = location.getX() / (srcComp != 0 ? srcComp : 1) * trgComp + 0.5;
		double y = location.getY();
		double z = location.getZ() / (srcComp != 0 ? srcComp : 1) * trgComp + 0.5;

		if (y < 1 && world.getEnvironment() == Environment.NORMAL)
			y = 1;

		while (this.blockIsAboveAir(world, x, y, z)) {
			y--;
		}
		while (this.blockIsNotSafe(world, x, y, z)) {
			y++;
			if (y == 110) {
				y = 1;
				x = x + 1;
				z = z + 1;
			}
		}
		return new Location(world, x, y, z, location.getYaw(),
				location.getPitch());
	}

	/**
	 * This function checks whether the block at the given coordinates are above
	 * air or not.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private boolean blockIsAboveAir(World world, double x, double y, double z) {
		return (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1),
				(int) Math.floor(z)).getType() == Material.AIR);
	}

	/**
	 * This function checks whether the block at the coordinates given is safe
	 * or not by checking for Laval/Fire/Air etc.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private boolean blockIsNotSafe(World world, double x, double y, double z) {
		if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1),
				(int) Math.floor(z)).getType() == Material.LAVA))
			return true;
		if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1),
				(int) Math.floor(z)).getType() == Material.STATIONARY_LAVA))
			return true;
		if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1),
				(int) Math.floor(z)).getType() == Material.FIRE))
			return true;
		if (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y),
				(int) Math.floor(z)).getType() != Material.AIR
				|| world.getBlockAt((int) Math.floor(x),
						(int) Math.floor(y + 1), (int) Math.floor(z)).getType() != Material.AIR)
			return true;
		if (blockIsAboveAir(world, x, y, z))
			return true;

		return false;
	}

	/**
	 * Check if a Player can teleport to the Destination world from there
	 * current world. This checks against the Worlds Blacklist
	 * 
	 * @param p
	 * @param w
	 * @return
	 */
	public Boolean canTravelToWorld(World w, Player p) {
		List<String> blackList = this.plugin.worlds.get(w.getName()).worldBlacklist;

		boolean returnValue = true;

		if (blackList.size() == 0)
			returnValue = true;

		for (int i = 0; i < blackList.size(); i++)
			if (blackList.get(i).equalsIgnoreCase(p.getWorld().getName())) {
				returnValue = false;
				break;
			}

		return returnValue;
	}
	
	public boolean teleport(World w, Player p) {
		if (canTravelToWorld(w, p)) {
			p.teleportTo(getDestination(w, p));
			return true;
		} else return false;
	}
    
}
