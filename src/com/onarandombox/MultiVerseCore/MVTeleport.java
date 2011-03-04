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
    
    public Location target = null;
    
	/**
	 * This function gets a safe place to teleport to.
	 * 
	 * @param world
	 * @param player
	 * @return
	 */
	public Location getDestination(World world, Player player, Location location) {
		
		MultiVerseCore.log.info(player.getName() + " wants to go to " + world.getName() + ". He's now at " + player.getLocation().toString());
		
	    double x, y, z;
	    if(location==null){
	        location = player.getLocation();
		
	        double srcComp = plugin.worlds.get(player.getWorld().getName()).compression;
	        double trgComp = plugin.worlds.get(world.getName()).compression;
	        
	        MultiVerseCore.log.info(player.getWorld().getName() + "(" + srcComp + ") -> " + world.getName() + "(" + trgComp + ")");
	        
            // If the Targets Compression is 0 then we teleport them to the Spawn of the World.
            if(trgComp==0.0){
                x = world.getSpawnLocation().getX();
                y = world.getSpawnLocation().getY();
                z = world.getSpawnLocation().getZ();
            } else {
                x = location.getX() / (srcComp != 0 ? srcComp : 1) * trgComp + 0.5;
                y = location.getY();
                z = location.getZ() / (srcComp != 0 ? srcComp : 1) * trgComp + 0.5;
            }
             
	    } else {
	        x = location.getX();
	        y = location.getY();
	        z = location.getZ();
	    }
	    
		if (y < 1 && world.getEnvironment() == Environment.NORMAL)
			y = 1;

		while (this.blockIsAboveAir(world, x, y, z)) {
			y--;
		}
				
		double i = 0, r = 0, aux = -1;

		for (r = 0; r < 32; r++) {
			for (i = x - r; i <= x + r; i++) {
				if ((aux = safeColumn(world, i, y, z - r)) > -1) {z = z - r; break;}
				if ((aux = safeColumn(world, i, y, z + r)) > -1) {z = z + r; break;}
			}
			if (aux > -1) {x = i; break;}
			for (i = z - r + 1; i <= z + r - 1; i++) {
				if ((aux = safeColumn(world, x - r, y, i)) > -1) {x = x - r; break;}
				if ((aux = safeColumn(world, x + r, y, i)) > -1) {x = x + r; break;}
			}
			if (aux > -1) {z = i; break;}
		}
		
		if (aux == -1) return null;
		
		MultiVerseCore.log.info("Target location (safe): " + x + ", " + aux + ", " + z);
		
		return new Location(world, x, aux, z, location.getYaw(),location.getPitch());
	}
	
	private double safeColumn(World world, double x, double y, double z) {
		double ny; boolean res = false;
        for (ny = y; ny < 120 && ny < y + 48; ny++)
        	if (!this.blockIsNotSafe(world, x, y, z)) {res = true; break;}
        for (ny = y; ny > 1 && ny > y - 48; ny--)
        	if (!this.blockIsNotSafe(world, x, y, z)) {res = true; break;}
        if (res) return y;
        else return -1;
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
		return (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1), (int) Math.floor(z)).getType() == Material.AIR);
	}

	/**
	 * This function checks whether the block at the coordinates given is safe
	 * or not by checking for Lava/Fire/Air etc.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private boolean blockIsNotSafe(World world, double x, double y, double z) {
		if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1),(int) Math.floor(z)).getType() == Material.LAVA))
			return true;
		
		if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1),(int) Math.floor(z)).getType() == Material.STATIONARY_LAVA))
			return true;
		
		if ((world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1),(int) Math.floor(z)).getType() == Material.FIRE))
			return true;
		
		if (world.getBlockAt((int) Math.floor(x), (int) Math.floor(y),(int) Math.floor(z)).getType() != Material.AIR 
		        || world.getBlockAt((int) Math.floor(x),(int) Math.floor(y + 1), (int) Math.floor(z)).getType() != Material.AIR)
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
	
	/**
     * Check if the Player has the permissions to enter this world.
     * 
     * @param p
     * @param w
     * @return
     */
	// TODO: To be sorted when Permissions is introduced.
    /*public Boolean canEnterWorld(Player p, World w) {
        List<String> whiteList = this.plugin.MVWorlds.get(w.getName()).joinWhitelist;
        List<String> blackList = this.plugin.MVWorlds.get(w.getName()).joinBlacklist;
        String group = MultiVerseCore.Permissions.getGroup(p.getName());
        
        boolean returnValue = true;

        if (whiteList.size() > 0)
            returnValue = false;

        for (int i = 0; i < whiteList.size(); i++){
            if (whiteList.get(i).contains("g:") && group.equalsIgnoreCase(whiteList.get(i).split(":")[1])) {
                returnValue = true;
                break;
            }
        }

        for (int i = 0; i < blackList.size(); i++){
            if (blackList.get(i).contains("g:") && group.equalsIgnoreCase(blackList.get(i).split(":")[1])) {
                returnValue = false;
                break;
            }
        }

        for (int i = 0; i < whiteList.size(); i++){
            if (whiteList.get(i).equalsIgnoreCase(p.getName())) {
                returnValue = true;
                break;
            }
        }

        for (int i = 0; i < blackList.size(); i++){
            if (blackList.get(i).equalsIgnoreCase(p.getName())) {
                returnValue = false;
                break;
            }
        }
        return returnValue;
    }*/
	
	/**
	 * This is to be used to travel to exact coordinates without Compression being taken into effect.
	 * EG: Portal to Portal teleportation
	 * EG: Portal to Specific Location
	 */
	public boolean teleport(World w, Player p, Location location) {
		if (canTravelToWorld(w, p)) {
			Location target = getDestination(w, p, location);
			if (target != null) {
				this.target = target;
				p.teleportTo(target);
				return true;
			} else {
			    return false;
			}
		} else {
		    return false;
		}
	}
	
	/**
	 * This is to be used when we wan't Compression to be used.
	 */
	public boolean teleport(World w, Player p) {
        return teleport(w, p, null);
    }
}
