/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.Core;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.material.Bed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The default-implementation of {@link BlockSafety}.
 */
public class SimpleBlockSafety implements BlockSafety {
    private final Core plugin;
    private static final Set<BlockFace> AROUND_BLOCK = EnumSet.noneOf(BlockFace.class);

    static {
        AROUND_BLOCK.add(BlockFace.NORTH);
        AROUND_BLOCK.add(BlockFace.NORTH_EAST);
        AROUND_BLOCK.add(BlockFace.EAST);
        AROUND_BLOCK.add(BlockFace.SOUTH_EAST);
        AROUND_BLOCK.add(BlockFace.SOUTH);
        AROUND_BLOCK.add(BlockFace.SOUTH_WEST);
        AROUND_BLOCK.add(BlockFace.WEST);
        AROUND_BLOCK.add(BlockFace.NORTH_WEST);
    }

    public SimpleBlockSafety(Core plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockAboveAir(Location l) {
        Location downOne = l.clone();
        downOne.setY(downOne.getY() - 1);
        return (downOne.getBlock().getType() == Material.AIR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playerCanSpawnHereSafely(World world, double x, double y, double z) {
        Location l = new Location(world, x, y, z);
        return playerCanSpawnHereSafely(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playerCanSpawnHereSafely(Location l) {
        if (l == null) {
            // Can't safely spawn at a null location!
            return false;
        }

        World world = l.getWorld();
        Location actual = l.clone();
        Location upOne = l.clone();
        Location downOne = l.clone();
        upOne.setY(upOne.getY() + 1);
        downOne.setY(downOne.getY() - 1);

        if (isSolidBlock(world.getBlockAt(actual).getType())
                || isSolidBlock(upOne.getBlock().getType())) {
            Logging.finer("Error Here (Actual)? (%s)[%s]", actual.getBlock().getType(),
                    isSolidBlock(actual.getBlock().getType()));
            Logging.finer("Error Here (upOne)? (%s)[%s]", upOne.getBlock().getType(),
                    isSolidBlock(upOne.getBlock().getType()));
            return false;
        }

        if (downOne.getBlock().getType() == Material.LAVA || downOne.getBlock().getType().toString().equalsIgnoreCase("STATIONARY_LAVA")) {
            Logging.finer("Error Here (downOne)? (%s)[%s]", downOne.getBlock().getType(), isSolidBlock(downOne.getBlock().getType()));
            return false;
        }

        if (downOne.getBlock().getType() == Material.FIRE) {
            Logging.finer("There's fire below! (%s)[%s]", actual.getBlock().getType(), isSolidBlock(actual.getBlock().getType()));
            return false;
        }
        
        if (downOne.getBlock().getType() == XMaterial.MAGMA_BLOCK.parseMaterial()) {
            Logging.finer("There's magma below! (%s)[%s]", actual.getBlock().getType(), isSolidBlock(actual.getBlock().getType()));
            return false;
        }

        if (isBlockAboveAir(actual)) {
            Logging.finer("Is block above air [%s]", isBlockAboveAir(actual));
            Logging.finer("Has 2 blocks of water below [%s]", this.hasTwoBlocksofWaterBelow(actual));
            return this.hasTwoBlocksofWaterBelow(actual);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getSafeBedSpawn(Location l) {
        // The passed location, may be null (if the bed is invalid)
        if (l == null) {
            return null;
        }
        final Location trySpawn = this.getSafeSpawnAroundABlock(l);
        if (trySpawn != null) {
            return trySpawn;
        }
        Location otherBlock = this.findOtherBedPiece(l);
        if (otherBlock == null) {
            return null;
        }
        // Now we have 2 locations, check around each, if the type is bed, skip it.
        return this.getSafeSpawnAroundABlock(otherBlock);
    }

    /**
     * Find a safe spawn around a location. (N,S,E,W,NE,NW,SE,SW)
     * @param l Location to check around
     * @return A safe location, or none if it wasn't found.
     */
    private Location getSafeSpawnAroundABlock(Location l) {
        Iterator<BlockFace> checkblock = AROUND_BLOCK.iterator();
        while (checkblock.hasNext()) {
            final BlockFace face = checkblock.next();
            if (this.playerCanSpawnHereSafely(l.getBlock().getRelative(face).getLocation())) {
                // Don't forget to center the player.
                return l.getBlock().getRelative(face).getLocation().add(.5, 0, .5);
            }
        }
        return null;
    }

    /**
     * Find the other bed block.
     * @param checkLoc The location to check for the other piece at
     * @return The location of the other bed piece, or null if it was a jacked up bed.
     */
    private Location findOtherBedPiece(Location checkLoc) {
    	if (!(checkLoc.getBlock().getState().getData() instanceof Bed)) {
            return null;
        }
        // Construct a bed object at this location
        final Bed b = new Bed(XMaterial.RED_BED.parseMaterial(), checkLoc.getBlock().getData());
        if (b.isHeadOfBed()) {
            return checkLoc.getBlock().getRelative(b.getFacing().getOppositeFace()).getLocation();
        }
        // We shouldn't ever be looking at the foot, but here's the code for it.
        return checkLoc.getBlock().getRelative(b.getFacing()).getLocation();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Location getTopBlock(Location l) {
        Location check = l.clone();
        check.setY(127); // SUPPRESS CHECKSTYLE: MagicNumberCheck
        while (check.getY() > 0) {
            if (this.playerCanSpawnHereSafely(check)) {
                return check;
            }
            check.setY(check.getY() - 1);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getBottomBlock(Location l) {
        Location check = l.clone();
        check.setY(0);
        while (check.getY() < 127) { // SUPPRESS CHECKSTYLE: MagicNumberCheck
            if (this.playerCanSpawnHereSafely(check)) {
                return check;
            }
            check.setY(check.getY() + 1);
        }
        return null;
    }

    /*
     * If someone has a better way of this... Please either tell us, or submit a pull request!
     */
    private static boolean isSolidBlock(Material type) {
    	List<Material> validMaterials = new ArrayList<>();
        Collections.addAll(validMaterials, XMaterial.CAVE_AIR.parseMaterial(), XMaterial.WALL_TORCH.parseMaterial(),
                XMaterial.ACACIA_TRAPDOOR.parseMaterial(), XMaterial.BIRCH_TRAPDOOR.parseMaterial(), XMaterial.DARK_OAK_TRAPDOOR.parseMaterial(),
                XMaterial.JUNGLE_TRAPDOOR.parseMaterial(), XMaterial.OAK_TRAPDOOR.parseMaterial(), XMaterial.SPRUCE_TRAPDOOR.parseMaterial(),
                XMaterial.ACACIA_PRESSURE_PLATE.parseMaterial(), XMaterial.BIRCH_PRESSURE_PLATE.parseMaterial(), XMaterial.DARK_OAK_PRESSURE_PLATE.parseMaterial(),
                XMaterial.JUNGLE_PRESSURE_PLATE.parseMaterial(), XMaterial.OAK_PRESSURE_PLATE.parseMaterial(), XMaterial.SPRUCE_PRESSURE_PLATE.parseMaterial(),
                XMaterial.STONE_PRESSURE_PLATE.parseMaterial(), XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.parseMaterial(), XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.parseMaterial(),
                XMaterial.RAIL.parseMaterial(), XMaterial.REDSTONE_TORCH.parseMaterial(), XMaterial.REDSTONE_WALL_TORCH.parseMaterial(),
                XMaterial.ACACIA_SAPLING.parseMaterial(), XMaterial.BIRCH_SAPLING.parseMaterial(), XMaterial.DARK_OAK_SAPLING.parseMaterial(),
                XMaterial.JUNGLE_SAPLING.parseMaterial(), XMaterial.OAK_SAPLING.parseMaterial(), XMaterial.SPRUCE_SAPLING.parseMaterial(),
                XMaterial.ACACIA_BUTTON.parseMaterial(), XMaterial.BIRCH_BUTTON.parseMaterial(), XMaterial.DARK_OAK_BUTTON.parseMaterial(),
                XMaterial.JUNGLE_BUTTON.parseMaterial(), XMaterial.OAK_BUTTON.parseMaterial(), XMaterial.SPRUCE_BUTTON.parseMaterial(),
                XMaterial.GRASS.parseMaterial(), XMaterial.NETHER_PORTAL.parseMaterial(), XMaterial.SUGAR_CANE.parseMaterial(),
                XMaterial.ALLIUM.parseMaterial(), XMaterial.AZURE_BLUET.parseMaterial(), XMaterial.BLUE_ORCHID.parseMaterial(),
                XMaterial.POPPY.parseMaterial(), XMaterial.DANDELION.parseMaterial(), XMaterial.OXEYE_DAISY.parseMaterial(),
                XMaterial.RED_TULIP.parseMaterial(), XMaterial.ORANGE_TULIP.parseMaterial(), XMaterial.PINK_TULIP.parseMaterial(),
                XMaterial.WHITE_TULIP.parseMaterial(), XMaterial.BLUE_CARPET.parseMaterial(), XMaterial.BLACK_CARPET.parseMaterial(),
                XMaterial.RED_CARPET.parseMaterial(), XMaterial.ORANGE_CARPET.parseMaterial(), XMaterial.YELLOW_CARPET.parseMaterial(),
                XMaterial.GREEN_CARPET.parseMaterial(), XMaterial.CYAN_CARPET.parseMaterial(), XMaterial.WHITE_CARPET.parseMaterial(),
                XMaterial.PURPLE_CARPET.parseMaterial(), XMaterial.PINK_CARPET.parseMaterial(), XMaterial.GRAY_CARPET.parseMaterial(),
                XMaterial.LIGHT_GRAY_CARPET.parseMaterial(), XMaterial.BROWN_CARPET.parseMaterial(), XMaterial.MAGENTA_CARPET.parseMaterial(),
                XMaterial.LIGHT_BLUE_CARPET.parseMaterial(), XMaterial.LIME_CARPET.parseMaterial(), 
                XMaterial.SIGN.parseMaterial(), XMaterial.WALL_SIGN.parseMaterial(), XMaterial.WATER.parseMaterial());
        
        if (validMaterials.contains(type)) {
        	return false;
        }
        switch (type) {
            case AIR:
                return false;
            case SNOW:
                return false;
            case TORCH:
                return false;
            case RED_MUSHROOM:
                return false;
            case BROWN_MUSHROOM:
                return false;
            case REDSTONE:
                return false;
            case REDSTONE_WIRE:
                return false;
            case POWERED_RAIL:
                return false;
            case DEAD_BUSH:
                return false;
            case STONE_BUTTON:
                return false;
            case LEVER:
                return false;
            case WALL_SIGN:
                return false;
            case WATER:
                return false;
            default:
                return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEntitiyOnTrack(Location l) {
        Material currentBlock = l.getBlock().getType();
        return (currentBlock == Material.POWERED_RAIL || currentBlock == Material.DETECTOR_RAIL || currentBlock == XMaterial.RAIL.parseMaterial());
    }

    /**
     * Checks recursively below a {@link Location} for 2 blocks of water.
     *
     * @param l The {@link Location}
     * @return Whether there are 2 blocks of water
     */
    private boolean hasTwoBlocksofWaterBelow(Location l) {
        if (l.getBlockY() < 0) {
            return false;
        }
        Location oneBelow = l.clone();
        oneBelow.subtract(0, 1, 0);
        if (oneBelow.getBlock().getType() == Material.WATER || oneBelow.getBlock().getType().toString().equalsIgnoreCase("STATIONARY_WATER")) {
            Location twoBelow = oneBelow.clone();
            twoBelow.subtract(0, 1, 0);
            return (oneBelow.getBlock().getType() == Material.WATER || oneBelow.getBlock().getType().toString().equalsIgnoreCase("STATIONARY_WATER"));
        }
        if (oneBelow.getBlock().getType() != Material.AIR) {
            return false;
        }
        return hasTwoBlocksofWaterBelow(oneBelow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSpawnCartSafely(Minecart cart) {
        if (this.isBlockAboveAir(cart.getLocation())) {
            return true;
        }
        if (this.isEntitiyOnTrack(plugin.getLocationManipulation().getNextBlock(cart))) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSpawnVehicleSafely(Vehicle vehicle) {
        if (this.isBlockAboveAir(vehicle.getLocation())) {
            return true;
        }
        return false;
    }

}
