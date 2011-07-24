package com.onarandombox.MultiverseCore;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.onarandombox.utils.BlockSafety;

public class MVTeleport {

    MultiverseCore plugin;

    BlockSafety bs = new BlockSafety();
    private static final Logger log = Logger.getLogger("Minecraft");

    public MVTeleport(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * This method will be specific to beds, and check on top of the bed then around it.
     *
     * @return
     */
    public Location getSafeBedDestination(Location bedLocation) {
        //System.out.print(bedLocation);
        Location idealLocation = bedLocation;
        idealLocation.setY(idealLocation.getY() + 1);
        idealLocation.setX(idealLocation.getX() + .5);
        idealLocation.setZ(idealLocation.getZ() + .5);
        //System.out.print(idealLocation);
        if (this.bs.playerCanSpawnHereSafely(idealLocation)) {
            //System.out.print(idealLocation);
            return bedLocation;
        }
        return null;
    }
    
    private Location getSafeLocation(Location l) {
        return null;
    }
    
    public boolean safelyTeleport(Entity e, Location l) {
        if(this.bs.playerCanSpawnHereSafely(l)) {
            e.teleport(l);
            System.out.print("The first location you gave me was safe!");
            return true;
        } else if (this.getSafeLocation(l) != null) {
            e.teleport(this.getSafeLocation(l));
            System.out.print("Had to look for a bit, but I found a safe place for ya!");
            return true;
        }
        System.out.print("Sorry champ, you're basically trying to teleport into a minefield. I should just kill you now.");
        return false;
    }

    /**
     * This function gets a safe place to teleport to.
     *
     * @param world
     * @param player
     * @return
     */
    @Deprecated
    private Location getSafeDestination(Location l) {
        double x = l.getX();
        double y = l.getY();
        double z = l.getZ();
        World w = l.getWorld();

        // To make things easier we'll start with the Y Coordinate on top of a Solid Block.
        // while (bs.blockIsAboveAir(w, x, y, z)) {
        // y--;
        // }

        double i = 0, r = 0, aux = -1;
        for (r = 0; r < 32; r++) {
            for (i = x - r; i <= x + r; i++) {
                if ((aux = safeColumn(w, i, y, z - r)) > -1) {
                    z = z - r;
                    break;
                }
                if ((aux = safeColumn(w, i, y, z + r)) > -1) {
                    z = z + r;
                    break;
                }
            }
            if (aux > -1) {
                x = i;
                break;
            }
            for (i = z - r + 1; i <= z + r - 1; i++) {
                if ((aux = safeColumn(w, x - r, y, i)) > -1) {
                    x = x - r;
                    break;
                }
                if ((aux = safeColumn(w, x + r, y, i)) > -1) {
                    x = x + r;
                    break;
                }
            }
            if (aux > -1) {
                z = i;
                break;
            }
        }

        if (aux == -1) {
            log.warning("Uh oh, no safe location.");
            return null;
        }

        // log.info("Target location (safe): " + x + ", " + aux + ", " + z);

        return new Location(w, x, aux, z);
    }

    /**
     * Check the Column given to see if there is an available safe spot.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    private double safeColumn(World world, double x, double y, double z) {
        for (double ny = 0; ny < 48; ny++) {
            if ((y + ny < 120) && !this.bs.blockIsNotSafe(world, x, y + ny, z)) {
                return y + ny;
            }
            if ((y - ny > 4) && !this.bs.blockIsNotSafe(world, x, y - ny, z)) {
                return y - ny;
            }
        }
        return -1;
    }
}
