/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.destination.InvalidDestination;
import com.onarandombox.MultiverseCore.enums.TeleportResult;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

import java.util.logging.Level;

/**
 * The default-implementation of {@link SafeTTeleporter}.
 */
public class SimpleSafeTTeleporter implements SafeTTeleporter {
    private MultiverseCore plugin;

    public SimpleSafeTTeleporter(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    private static final int DEFAULT_TOLERANCE = 6;
    private static final int DEFAULT_RADIUS = 9;

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getSafeLocation(Location l) {
        return this.getSafeLocation(l, DEFAULT_TOLERANCE, DEFAULT_RADIUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getSafeLocation(Location l, int tolerance, int radius) {
        // Check around the player first in a configurable radius:
        // TODO: Make this configurable
        Location safe = checkAboveAndBelowLocation(l, tolerance, radius);
        if (safe != null) {
            safe.setX(safe.getBlockX() + .5); // SUPPRESS CHECKSTYLE: MagicNumberCheck
            safe.setZ(safe.getBlockZ() + .5); // SUPPRESS CHECKSTYLE: MagicNumberCheck
            this.plugin.log(Level.FINE, "Hey! I found one: " + plugin.getLocationManipulation().strCoordsRaw(safe));
        } else {
            this.plugin.log(Level.FINE, "Uh oh! No safe place found!");
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
        this.plugin.log(Level.FINER, "Given Location of: " + plugin.getLocationManipulation().strCoordsRaw(l));
        this.plugin.log(Level.FINER, "Checking +-" + tolerance + " with a radius of " + radius);

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

    /*
     * For my crappy algorithm, radius MUST be odd.
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
        if (plugin.getBlockSafety().playerCanSpawnHereSafely(checkLoc)) {
            return true;
        }
        // Now we go to the right that adjustedCircle many
        for (int i = 0; i < adjustedCircle; i++) {
            checkLoc.add(0, 0, 1);
            if (plugin.getBlockSafety().playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then down adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(-1, 0, 0);
            if (plugin.getBlockSafety().playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then left adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(0, 0, -1);
            if (plugin.getBlockSafety().playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then up Then left adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(1, 0, 0);
            if (plugin.getBlockSafety().playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then finish up by doing adjustedCircle - 1
        for (int i = 0; i < adjustedCircle - 1; i++) {
            checkLoc.add(0, 0, 1);
            if (plugin.getBlockSafety().playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TeleportResult safelyTeleport(CommandSender teleporter, Entity teleportee, MVDestination d) {
        if (d instanceof InvalidDestination) {
            this.plugin.log(Level.FINER, "Entity tried to teleport to an invalid destination");
            return TeleportResult.FAIL_INVALID;
        }
        Player teleporteePlayer = null;
        if (teleportee instanceof Player) {
            teleporteePlayer = ((Player) teleportee);
        } else if (teleportee.getPassenger() instanceof Player) {
            teleporteePlayer = ((Player) teleportee.getPassenger());
        }

        if (teleporteePlayer == null) {
            return TeleportResult.FAIL_INVALID;
        }
        MultiverseCore.addPlayerToTeleportQueue(teleporter.getName(), teleporteePlayer.getName());

        Location safeLoc = d.getLocation(teleportee);
        if (d.useSafeTeleporter()) {
            safeLoc = this.getSafeLocation(teleportee, d);
        }

        if (safeLoc != null) {
            if (teleportee.teleport(safeLoc)) {
                if (!d.getVelocity().equals(new Vector(0, 0, 0))) {
                    teleportee.setVelocity(d.getVelocity());
                }
                return TeleportResult.SUCCESS;
            }
            return TeleportResult.FAIL_OTHER;
        }
        return TeleportResult.FAIL_UNSAFE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TeleportResult safelyTeleport(CommandSender teleporter, Entity teleportee, Location location, boolean safely) {
        if (safely) {
            location = this.getSafeLocation(location);
        }

        if (location != null) {
            if (teleportee.teleport(location)) {
                return TeleportResult.SUCCESS;
            }
            return TeleportResult.FAIL_OTHER;
        }
        return TeleportResult.FAIL_UNSAFE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getSafeLocation(Entity e, MVDestination d) {
        Location l = d.getLocation(e);
        if (plugin.getBlockSafety().playerCanSpawnHereSafely(l)) {
            plugin.log(Level.FINE, "The first location you gave me was safe.");
            return l;
        }
        if (e instanceof Minecart) {
            Minecart m = (Minecart) e;
            if (!plugin.getBlockSafety().canSpawnCartSafely(m)) {
                return null;
            }
        } else if (e instanceof Vehicle) {
            Vehicle v = (Vehicle) e;
            if (!plugin.getBlockSafety().canSpawnVehicleSafely(v)) {
                return null;
            }
        }
        Location safeLocation = this.getSafeLocation(l);
        if (safeLocation != null) {
            // Add offset to account for a vehicle on dry land!
            if (e instanceof Minecart && !plugin.getBlockSafety().isEntitiyOnTrack(safeLocation)) {
                safeLocation.setY(safeLocation.getBlockY() + .5);
                this.plugin.log(Level.FINER, "Player was inside a minecart. Offsetting Y location.");
            }
            this.plugin.log(Level.FINE, "Had to look for a bit, but I found a safe place for ya!");
            return safeLocation;
        }
        if (e instanceof Player) {
            Player p = (Player) e;
            this.plugin.getMessaging().sendMessage(p, "No safe locations found!", false);
            this.plugin.log(Level.FINER, "No safe location found for " + p.getName());
        } else if (e.getPassenger() instanceof Player) {
            Player p = (Player) e.getPassenger();
            this.plugin.getMessaging().sendMessage(p, "No safe locations found!", false);
            this.plugin.log(Level.FINER, "No safe location found for " + p.getName());
        }
        this.plugin.log(Level.FINE, "Sorry champ, you're basically trying to teleport into a minefield. I should just kill you now.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location findPortalBlockNextTo(Location l) {
        Block b = l.getWorld().getBlockAt(l);
        Location foundLocation = null;
        if (b.getType() == Material.PORTAL) {
            return l;
        }
        if (b.getRelative(BlockFace.NORTH).getType() == Material.PORTAL) {
            foundLocation = getCloserBlock(l, b.getRelative(BlockFace.NORTH).getLocation(), foundLocation);
        }
        if (b.getRelative(BlockFace.SOUTH).getType() == Material.PORTAL) {
            foundLocation = getCloserBlock(l, b.getRelative(BlockFace.SOUTH).getLocation(), foundLocation);
        }
        if (b.getRelative(BlockFace.EAST).getType() == Material.PORTAL) {
            foundLocation = getCloserBlock(l, b.getRelative(BlockFace.EAST).getLocation(), foundLocation);
        }
        if (b.getRelative(BlockFace.WEST).getType() == Material.PORTAL) {
            foundLocation = getCloserBlock(l, b.getRelative(BlockFace.WEST).getLocation(), foundLocation);
        }
        return foundLocation;
    }

    private static Location getCloserBlock(Location source, Location blockA, Location blockB) {
        // If B wasn't given, return a.
        if (blockB == null) {
            return blockA;
        }
        // Center our calculations
        blockA.add(.5, 0, .5);
        blockB.add(.5, 0, .5);

        // Retrieve the distance to the normalized blocks
        double testA = source.distance(blockA);
        double testB = source.distance(blockB);

        // Compare and return
        if (testA <= testB) {
            return blockA;
        }
        return blockB;
    }

    @Override
    public TeleportResult teleport(final CommandSender teleporter, final Player teleportee, final MVDestination destination) {
        return this.safelyTeleport(teleporter, teleportee, destination);
    }
}
