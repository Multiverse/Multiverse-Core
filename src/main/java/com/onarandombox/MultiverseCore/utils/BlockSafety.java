/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;

/**
 * Used to determine block/location-related facts.
 *
 * @deprecated Use instead: {@link com.onarandombox.MultiverseCore.api.BlockSafety} and {@link SimpleBlockSafety}.
 */
@Deprecated
public class BlockSafety {

    /**
     * This function checks whether the block at the given coordinates are above air or not.
     * @param l The {@link Location} of the block.
     * @return True if the block at that {@link Location} is above air.
     */
    public boolean isBlockAboveAir(Location l) {
        Location downOne = l.clone();
        downOne.setY(downOne.getY() - 1);
        return (downOne.getBlock().getType() == Material.AIR);
    }

    // TODO maybe remove this?
    private boolean blockIsNotSafe(World world, double x, double y, double z) {
        return !playerCanSpawnHereSafely(world, x, y, z);
    }

    /**
     * Checks if a player can spawn safely at the given coordinates.
     * @param world The {@link World}.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @return True if a player can spawn safely at the given coordinates.
     */
    public boolean playerCanSpawnHereSafely(World world, double x, double y, double z) {
        Location l = new Location(world, x, y, z);
        return playerCanSpawnHereSafely(l);
    }

    /**
     * This function checks whether the block at the coordinates given is safe or not by checking for Lava/Fire/Air
     * etc. This also ensures there is enough space for a player to spawn!
     *
     * @param l The {@link Location}
     * @return Whether the player can spawn safely at the given {@link Location}
     */
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

        if (this.isSolidBlock(world.getBlockAt(actual).getType())
                || this.isSolidBlock(upOne.getBlock().getType())) {
            Logging.finest("Error Here (Actual)? (%s)[%s]", actual.getBlock().getType(),
                    this.isSolidBlock(actual.getBlock().getType()));
            Logging.finest("Error Here (upOne)? (%s)[%s]", upOne.getBlock().getType(),
                    this.isSolidBlock(upOne.getBlock().getType()));
            return false;
        }

        if (downOne.getBlock().getType() == Material.LAVA || downOne.getBlock().getType().toString().equalsIgnoreCase("STATIONARY_LAVA")) {
            Logging.finest("Error Here (downOne)? (%s)[%s]", downOne.getBlock().getType(),
                    this.isSolidBlock(downOne.getBlock().getType()));
            return false;
        }

        if (downOne.getBlock().getType() == Material.FIRE) {
            Logging.finest("There's fire below! (%s)[%s]", actual.getBlock().getType(),
                    this.isSolidBlock(actual.getBlock().getType()));
            return false;
        }

        if (isBlockAboveAir(actual)) {
            Logging.finest("Is block above air [%s]", isBlockAboveAir(actual));
            Logging.finest("Has 2 blocks of water below [%s]", this.hasTwoBlocksofWaterBelow(actual));
            return this.hasTwoBlocksofWaterBelow(actual);
        }
        return true;
    }

    /**
     * Gets the location of the top block at the specified {@link Location}.
     * @param l Any {@link Location}.
     * @return The {@link Location} of the top-block.
     */
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
     * Gets the location of the top block at the specified {@link Location}.
     * @param l Any {@link Location}.
     * @return The {@link Location} of the top-block.
     */
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
    private boolean isSolidBlock(Material type) {
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
            default:
                return true;
        }
    }

    /**
     * Checks if an entity would be on track at the specified {@link Location}.
     * @param l The {@link Location}.
     * @return True if an entity would be on tracks at the specified {@link Location}.
     */
    public boolean isEntitiyOnTrack(Location l) {
        Material currentBlock = l.getBlock().getType();
        return (currentBlock == Material.POWERED_RAIL || currentBlock == Material.DETECTOR_RAIL || currentBlock == XMaterial.RAIL.parseMaterial());
    }

    // TODO maybe remove this?
    private void showDangers(Location l) {
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
        if (oneBelow.getBlock().getType() == Material.WATER) { // || oneBelow.getBlock().getType() == Material.STATIONARY_WATER) {
            Location twoBelow = oneBelow.clone();
            twoBelow.subtract(0, 1, 0);
            return (oneBelow.getBlock().getType() == Material.WATER); // || oneBelow.getBlock().getType() == Material.STATIONARY_WATER);
        }
        if (oneBelow.getBlock().getType() != Material.AIR) {
            return false;
        }
        return hasTwoBlocksofWaterBelow(oneBelow);
    }

    /**
     * Checks if the specified {@link Minecart} can spawn safely.
     * @param cart The {@link Minecart}.
     * @return True if the minecart can spawn safely.
     */
    public boolean canSpawnCartSafely(Minecart cart) {
        if (this.isBlockAboveAir(cart.getLocation())) {
            return true;
        }
        if (this.isEntitiyOnTrack(LocationManipulation.getNextBlock(cart))) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the specified {@link Vehicle} can spawn safely.
     * @param vehicle The {@link Vehicle}.
     * @return True if the vehicle can spawn safely.
     */
    public boolean canSpawnVehicleSafely(Vehicle vehicle) {
        if (this.isBlockAboveAir(vehicle.getLocation())) {
            return true;
        }
        return false;
    }

}
