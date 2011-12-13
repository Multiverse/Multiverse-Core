/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;

/**
 * Multiverse 2
 */
public class MockBlock implements Block{
    private Material type;
    private Location location;


    public MockBlock(Location l, Material type) {
        this.type = type;
        this.location = l;
    }

    /**
     * Gets the metadata for this block
     *
     * @return block specific metadata
     */
    @Override
    public byte getData() {
        return 0;
    }

    /** @deprecated use {@link #getRelative(org.bukkit.block.BlockFace face)} */
    @Override
    public Block getFace(BlockFace face) {
        return null;
    }

    /** @deprecated use {@link #getRelative(org.bukkit.block.BlockFace face, int distance)} */
    @Override
    public Block getFace(BlockFace face, int distance) {
        return null;
    }

    /**
     * Gets the block at the given offsets
     *
     * @param modX X-coordinate offset
     * @param modY Y-coordinate offset
     * @param modZ Z-coordinate offset
     *
     * @return Block at the given offsets
     */
    @Override
    public Block getRelative(int modX, int modY, int modZ) {
        return null;
    }

    /**
     * Gets the block at the given face<br />
     * <br />
     * This method is equal to getRelative(face, 1)
     *
     * @param face Face of this block to return
     *
     * @return Block at the given face
     *
     * @see #getRelative(org.bukkit.block.BlockFace, int)
     */
    @Override
    public Block getRelative(BlockFace face) {
        return null;
    }

    /**
     * Gets the block at the given distance of the given face<br />
     * <br />
     * For example, the following method places water at 100,102,100; two blocks
     * above 100,100,100.
     * <pre>
     * Block block = world.getBlockAt(100,100,100);
     * Block shower = block.getFace(BlockFace.UP, 2);
     * shower.setType(Material.WATER);
     * </pre>
     *
     * @param face     Face of this block to return
     * @param distance Distance to get the block at
     *
     * @return Block at the given face
     */
    @Override
    public Block getRelative(BlockFace face, int distance) {
        return null;
    }

    /**
     * Gets the type of this block
     *
     * @return block type
     */
    @Override
    public Material getType() {
        return this.type;
    }

    /**
     * Gets the type-id of this block
     *
     * @return block type-id
     */
    @Override
    public int getTypeId() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the light level between 0-15
     *
     * @return light level
     */
    @Override
    public byte getLightLevel() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the world which contains this Block
     *
     * @return World containing this block
     */
    @Override
    public World getWorld() {
        return this.location.getWorld();
    }

    /**
     * Gets the x-coordinate of this block
     *
     * @return x-coordinate
     */
    @Override
    public int getX() {
        return this.location.getBlockX();
    }

    /**
     * Gets the y-coordinate of this block
     *
     * @return y-coordinate
     */
    @Override
    public int getY() {
        return this.location.getBlockY();
    }

    /**
     * Gets the z-coordinate of this block
     *
     * @return z-coordinate
     */
    @Override
    public int getZ() {
        return this.location.getBlockZ();
    }

    /**
     * Gets the Location of the block
     *
     * @return Location of block
     */
    @Override
    public Location getLocation() {
        return this.location;
    }

    /**
     * Gets the chunk which contains this block
     *
     * @return Containing Chunk
     */
    @Override
    public Chunk getChunk() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Sets the metadata for this block
     *
     * @param data New block specific metadata
     */
    @Override
    public void setData(byte data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setData(byte data, boolean applyPhyiscs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Sets the type of this block
     *
     * @param type Material to change this block to
     */
    @Override
    public void setType(Material type) {
        this.type = type;
    }

    /**
     * Sets the type-id of this block
     *
     * @param type Type-Id to change this block to
     *
     * @return whether the block was changed
     */
    @Override
    public boolean setTypeId(int type) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setTypeId(int type, boolean applyPhysics) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setTypeIdAndData(int type, byte data, boolean applyPhyiscs) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the face relation of this block compared to the given block<br />
     * <br />
     * For example:
     * <pre>
     * Block current = world.getBlockAt(100, 100, 100);
     * Block target = world.getBlockAt(100, 101, 100);
     *
     * current.getFace(target) == BlockFace.Up;
     * </pre>
     * <br />
     * If the given block is not connected to this block, null may be returned
     *
     * @param block Block to compare against this block
     *
     * @return BlockFace of this block which has the requested block, or null
     */
    @Override
    public BlockFace getFace(Block block) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Captures the current state of this block. You may then cast that state
     * into any accepted type, such as Furnace or Sign.
     * <p>
     * The returned object will never be updated, and you are not guaranteed that
     * (for example) a sign is still a sign after you capture its state.
     *
     * @return BlockState with the current state of this block.
     */
    @Override
    public BlockState getState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the biome that this block resides in
     *
     * @return Biome type containing this block
     */
    @Override
    public Biome getBiome() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns true if the block is being powered by Redstone.
     *
     * @return True if the block is powered.
     */
    @Override
    public boolean isBlockPowered() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns true if the block is being indirectly powered by Redstone.
     *
     * @return True if the block is indirectly powered.
     */
    @Override
    public boolean isBlockIndirectlyPowered() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns true if the block face is being powered by Redstone.
     *
     * @param face The block face
     *
     * @return True if the block face is powered.
     */
    @Override
    public boolean isBlockFacePowered(BlockFace face) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns true if the block face is being indirectly powered by Redstone.
     *
     * @param face The block face
     *
     * @return True if the block face is indirectly powered.
     */
    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the redstone power being provided to this block face
     *
     * @param face the face of the block to query or BlockFace.SELF for the block itself
     *
     * @return The power level.
     */
    @Override
    public int getBlockPower(BlockFace face) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the redstone power being provided to this block
     *
     * @return The power level.
     */
    @Override
    public int getBlockPower() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Checks if this block is empty.
     * <p>
     * A block is considered empty when {@link #getType()} returns {@link org.bukkit.Material#AIR}.
     *
     * @return true if this block is empty
     */
    @Override
    public boolean isEmpty() {
        return this.type == Material.AIR;
    }

    /**
     * Checks if this block is liquid.
     * <p>
     * A block is considered liquid when {@link #getType()} returns {@link org.bukkit.Material#WATER}, {@link
     * org.bukkit.Material#STATIONARY_WATER}, {@link org.bukkit.Material#LAVA} or {@link
     * org.bukkit.Material#STATIONARY_LAVA}.
     *
     * @return true if this block is liquid
     */
    @Override
    public boolean isLiquid() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the temperature of the biome of this block
     *
     * @return Temperature of this block
     */
    @Override
    public double getTemperature() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the humidity of the biome of this block
     *
     * @return Humidity of this block
     */
    @Override
    public double getHumidity() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the reaction of the block when moved by a piston
     *
     * @return reaction
     */
    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
