package com.onarandombox.MultiVerseCore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import com.onarandombox.utils.BlockSafety;

public class MVTeleport {

	MultiVerseCore plugin;
    
	BlockSafety bs = new BlockSafety();
	
    public MVTeleport(MultiVerseCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * TODO: Sort out JavaDoc
     * @param l
     * @param w
     * @return
     */
    public Location getCompressedLocation(Player p, World w){
        Location l = p.getLocation();
        // Check if they are the same world, might as well skip any calculations.
        if(l.getWorld().getName().equalsIgnoreCase(w.getName())){
            return l;
        }
        
        double x,y,z;
        
        // Grab the Compression value for each world.
        double srcComp = plugin.worlds.get(l.getWorld().getName()).compression;
        double trgComp = plugin.worlds.get(w.getName()).compression;

        MultiVerseCore.debugMsg(p.getName() + " -> " + p.getWorld().getName() + "(" + srcComp + ") -> " + w.getName() + "(" + trgComp + ")");
        
        // If the Targets Compression is 0 then we teleport them to the Spawn of the World.
        if(trgComp==0.0){
            x = w.getSpawnLocation().getX();
            y = w.getSpawnLocation().getY();
            z = w.getSpawnLocation().getZ();
        } else {
            x = l.getX() / (srcComp != 0 ? srcComp : 1) * trgComp;
            y = l.getY();
            z = l.getZ() / (srcComp != 0 ? srcComp : 1) * trgComp;
        }
        return new Location(w,x,y,z);
    }
    
	/**
	 * This function gets a safe place to teleport to.
	 * 
	 * @param world
	 * @param player
	 * @return
	 */
	public Location getSafeDestination(Location l) {
	    double x = l.getX();
	    double y = l.getY();
	    double z = l.getZ();
        World w = l.getWorld();
        
        // To make things easier we'll start with the Y Coordinate on top of a Solid Block.
		//while (bs.blockIsAboveAir(w, x, y, z)) {
			//y--;
		//}
       
		double i = 0, r = 0, aux = -1;
		for (r = 0; r < 32; r++) {
			for (i = x - r; i <= x + r; i++) {
				if ((aux = safeColumn(w, i, y, z - r)) > -1) {z = z - r; break;}
				if ((aux = safeColumn(w, i, y, z + r)) > -1) {z = z + r; break;}
			}
			if (aux > -1) {x = i; break;}
			for (i = z - r + 1; i <= z + r - 1; i++) {
				if ((aux = safeColumn(w, x - r, y, i)) > -1) {x = x - r; break;}
				if ((aux = safeColumn(w, x + r, y, i)) > -1) {x = x + r; break;}
			}
			if (aux > -1) {z = i; break;}
		}
		
		if (aux == -1) {
		    MultiVerseCore.debugMsg("Uh oh, no safe location.");
		    return null;
		}
		
		MultiVerseCore.debugMsg("Target location (safe): " + x + ", " + aux + ", " + z);
		
		return new Location(w, x, aux, z);
	}
	
	/**
	 * Check the Column given to see if there is an available safe spot.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private double safeColumn(World world, double x, double y, double z) {
	    for (double ny=0; ny<48;ny++){
	        if ((y+ny<120) && !bs.blockIsNotSafe(world, x, y+ny, z)) { return y+ny; }
	        if ((y-ny>4) && !bs.blockIsNotSafe(world, x, y-ny, z)) { return y-ny; }
	    }
	    return -1;
	}
	
	/**
	 * Find a portal around the given location and return a new location.
	 * @param location
	 * @return
	 */
    public Location findPortal(Location location){
	    World world = location.getWorld();
	    // Get list of columns in a circle around the block
        ArrayList<Block> columns = new ArrayList<Block>();
        //for (int x = location.getBlockX() - 16; x <= location.getBlockX() + 16; ++x) {
        for (int x = location.getBlockX() - 8; x <= location.getBlockX() + 8; ++x) {
            //for (int z = location.getBlockZ() - 16; z <= location.getBlockZ() + 16; ++z) {
            for (int z = location.getBlockZ() - 8; z <= location.getBlockZ() + 8; ++z) {
                int dx = location.getBlockX() - x, dz = location.getBlockZ() - z;
                if (dx * dx + dz * dz <= 256) {
                    columns.add(world.getBlockAt(x, 0, z));
                }
            }
        }

        // For each column try to find a portal block
        for (Block col : columns) {
            for (int y = 0; y <= 127; y++) {
                Block b = world.getBlockAt(col.getX(), y, col.getZ());
                if (b.getType().equals(Material.PORTAL)) {
                        if (b.getWorld().getBlockAt(b.getX() + 1, b.getY(), b.getZ()).getType().equals(Material.PORTAL) ||
                                b.getWorld().getBlockAt(b.getX() - 1, b.getY(), b.getZ()).getType().equals(Material.PORTAL)) {
                            // portal is in X direction
                            //return new Location(b.getWorld(), b.getX() + 0.5,b.getY(), b.getZ() + 1.5 - 2 * Math.round(Math.random()));
                            return new Location(b.getWorld(), b.getX() + 0.5,b.getY(), b.getZ() + 1.5);
                            //return new Location(b.getWorld(),b.getX(),b.getY(),b.getZ());
                        } else {
                            // portal is in Z direction
                            //return new Location(b.getWorld(), b.getX() + 1.5 - 2 * Math.round(Math.random()),b.getY(), b.getZ() + 0.5);
                            //MultiVerseCore.log.info("This - " + 2* Math.round(Math.random()));
                            //MultiVerseCore.log.info("This - " + 2* Math.round(Math.random()));
                            return new Location(b.getWorld(), b.getX() + 1.5,b.getY(), b.getZ() + 0.5);
                            //return new Location(b.getWorld(),b.getX(),b.getY(),b.getZ());
                        }
                    }
                    //return new Location(world,b.getX(),b.getY(),b.getZ());
                }
        }
        return null;
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
		/*if (canTravelToWorld(w, p)) {
			Location target = getDestination(w, p, location);
			if (target != null) {
			    this.plugin.getPlayerSession(p).message(ChatColor.RED + "Teleporting, hopefully you won't lose a limb.");
				this.target = target;
				p.teleportTo(target);
				return true;
			} else {
			    this.plugin.getPlayerSession(p).message(ChatColor.RED + "Cannot find a safe location, try another portal/location.");
			    return false;
			}
		} else {
		    this.plugin.getPlayerSession(p).message(ChatColor.RED + "You cannot travel to this World.");
		    return false;
		}*/
	    return false;
	}
	
	/**
	 * This is to be used when we wan't Compression to be used.
	 */
	public boolean teleport(World w, Player p) {
        return teleport(w, p, null);
    }
}
