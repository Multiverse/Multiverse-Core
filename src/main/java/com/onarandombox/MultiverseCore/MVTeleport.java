package com.onarandombox.MultiverseCore;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.onarandombox.utils.BlockSafety;

public class MVTeleport {

    MultiverseCore plugin;

    BlockSafety bs = new BlockSafety();
    private static final Logger log = Logger.getLogger("Minecraft");

    public MVTeleport(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * TODO: Sort out JavaDoc
     *
     * @param l
     * @param w
     * @return
     */
    public Location getCompressedLocation(Player p, World w) {
        Location l = p.getLocation();
        // Check if they are the same world, might as well skip any calculations.
        if (l.getWorld().getName().equalsIgnoreCase(w.getName())) {
            return l;
        }

        double x, y, z;

        // Grab the Scaling value for each world.
        double srcComp = this.plugin.getMVWorld(l.getWorld().getName()).getScaling();
        double trgComp = this.plugin.getMVWorld(w.getName()).getScaling();

        // MultiverseCore.debugMsg(p.getName() + " -> " + p.getWorld().getName() + "(" + srcComp + ") -> " + w.getName() + "(" + trgComp + ")");

        // If the Targets Compression is 0 then we teleport them to the Spawn of the World.
        if (trgComp == 0.0) {
            x = w.getSpawnLocation().getX();
            y = w.getSpawnLocation().getY();
            z = w.getSpawnLocation().getZ();
        } else {
            x = l.getX() / (srcComp != 0 ? srcComp : 1) * trgComp;
            y = l.getY();
            z = l.getZ() / (srcComp != 0 ? srcComp : 1) * trgComp;
        }
        return new Location(w, x, y, z);
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

    /**
     * Find a portal around the given location and return a new location.
     *
     * @param location
     * @return
     */
    public Location findPortal(Location location) {
        World world = location.getWorld();
        // Get list of columns in a circle around the block
        ArrayList<Block> columns = new ArrayList<Block>();
        for (int x = location.getBlockX() - 8; x <= location.getBlockX() + 8; ++x) {
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
                    if (b.getWorld().getBlockAt(b.getX() + 1, b.getY(), b.getZ()).getType().equals(Material.PORTAL) || b.getWorld().getBlockAt(b.getX() - 1, b.getY(), b.getZ()).getType().equals(Material.PORTAL)) {
                        // portal is in X direction
                        return new Location(b.getWorld(), b.getX() + 0.5, b.getY(), b.getZ() + 1.5);
                    } else {
                        // portal is in Z direction
                        return new Location(b.getWorld(), b.getX() + 1.5, b.getY(), b.getZ() + 0.5);
                    }
                }
            }
        }
        return null;
    }
}
