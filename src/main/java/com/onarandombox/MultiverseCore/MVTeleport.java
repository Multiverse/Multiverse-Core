package com.onarandombox.MultiverseCore;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.onarandombox.utils.BlockSafety;

public class MVTeleport {

    MultiverseCore plugin;

    BlockSafety bs = new BlockSafety();

    public MVTeleport(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * This method will be specific to beds, and check on top of the bed then around it.
     * 
     * @return
     */
    public Location getSafeBedDestination(Location bedLocation) {
        // System.out.print(bedLocation);
        Location idealLocation = bedLocation;
        idealLocation.setY(idealLocation.getY() + 1);
        idealLocation.setX(idealLocation.getX() + .5);
        idealLocation.setZ(idealLocation.getZ() + .5);
        // System.out.print(idealLocation);
        if (this.bs.playerCanSpawnHereSafely(idealLocation)) {
            // System.out.print(idealLocation);
            return bedLocation;
        }
        return null;
    }

    private Location getSafeLocation(Location l) {

        // Check around the player first in a configurable radius:
        // TODO: Make this configurable
        Location safe = checkAboveAndBelowLocation(l, 6, 9);
        if (safe != null) {
            safe.setX(safe.getBlockX() + .5);
            safe.setZ(safe.getBlockZ() + .5);
        }
        return safe;
    }

    private Location checkAboveAndBelowLocation(Location l, int tolerance, int radius) {
        // Tolerance must be an even number:
        if (tolerance % 2 != 0) {
            tolerance += 1;
        }
        // We want half of it, so we can go up and down
        tolerance /= 2;

        // For now this will just do a straight up block.
        Location locToCheck = l.clone();
        // Check the main level
        Location safe = this.checkAroundLocation(locToCheck, radius);
        if (safe != null) {
            return safe;
        }
        // We've already checked zero right above this.
        int currentLevel = 1;
        while (currentLevel <= tolerance) {
            // Check above
            locToCheck = l.clone();
            locToCheck.add(0, currentLevel, 0);
            safe = this.checkAroundLocation(locToCheck, radius);
            if (safe != null) {
                return safe;
            }

            // Check below
            locToCheck = l.clone();
            locToCheck.subtract(0, currentLevel, 0);
            safe = this.checkAroundLocation(locToCheck, radius);
            if (safe != null) {
                return safe;
            }
            currentLevel++;
        }

        return null;
    }

    /**
     * For my crappy algorithm, radius MUST be odd
     * 
     * @param l
     * @param radius
     * @return
     */
    private Location checkAroundLocation(Location l, int diameter) {
        if (diameter % 2 == 0) {
            diameter += 1;
        }
        Location checkLoc = l.clone();

        // Start at 3, the min diameter around a block
        int loopcounter = 3;
        while (loopcounter <= diameter) {
            boolean foundSafeArea = checkAroundSpecificDiameter(checkLoc, loopcounter);
            // If a safe area was found:
            if (foundSafeArea) {
                // Return the checkLoc, it is the safe location.
                return checkLoc;
            }
            // Otherwise, let's reset our location
            checkLoc = l.clone();
            // And increment the radius
            loopcounter += 2;
        }
        return null;
    }

    private boolean checkAroundSpecificDiameter(Location checkLoc, int circle) {
        // Adjust the circle to get how many blocks to step out.
        // A radius of 3 makes the block step 1
        // A radius of 5 makes the block step 2
        // A radius of 7 makes the block step 3
        // ...
        int adjustedCircle = ((circle - 1) / 2);
        checkLoc.add(adjustedCircle, 0, 0);
        if (this.bs.playerCanSpawnHereSafely(checkLoc)) {
            return true;
        }
        // Now we go to the right that adjustedCircle many
        for (int i = 0; i < adjustedCircle; i++) {
            checkLoc.add(0, 0, 1);
            if (this.bs.playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then down adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(-1, 0, 0);
            if (this.bs.playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then left adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(0, 0, -1);
            if (this.bs.playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then up Then left adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(1, 0, 0);
            if (this.bs.playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then finish up by doing adjustedCircle - 1
        for (int i = 0; i < adjustedCircle - 1; i++) {
            checkLoc.add(0, 0, 1);
            if (this.bs.playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }
        return false;
    }

    public boolean safelyTeleport(Entity e, Location l) {
        if (this.bs.playerCanSpawnHereSafely(l)) {
            l.setX(l.getBlockX() + .5);
            l.setZ(l.getBlockZ() + .5);
            e.teleport(l);
            // System.out.print("The first location you gave me was safe!");
            return true;
        }
        Location safeLocation = this.getSafeLocation(l);
        if (safeLocation != null) {
            // Add offset to account for a vehicle on dry land!
            if (!this.bs.isEntitiyOnTrack(e, safeLocation)) {
                safeLocation.setY(safeLocation.getBlockY() + .5);
            }
            e.teleport(safeLocation);
            // System.out.print("Had to look for a bit, but I found a safe place for ya!" + safeLocation);
            return true;
        }
        if (e instanceof Player) {
            Player p = (Player) e;
            p.sendMessage("No safe locations found!");
        }
        else if (e.getPassenger() instanceof Player) {
            Player p = (Player) e.getPassenger();
            p.sendMessage("No safe locations found!");
        }
        this.plugin.log(Level.WARNING, "Sorry champ, you're basically trying to teleport into a minefield. I should just kill you now.");
        return false;
    }

}
