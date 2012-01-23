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
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

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
     * {@inheritDoc}
     */
    @Override
    public byte getData() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block getFace(BlockFace face) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block getFace(BlockFace face, int distance) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block getRelative(int modX, int modY, int modZ) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block getRelative(BlockFace face) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block getRelative(BlockFace face, int distance) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Material getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTypeId() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getLightLevel() {
        return 0;
    }

    @Override
    public byte getLightFromSky() {
        return 0;
    }

    @Override
    public byte getLightFromBlocks() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public World getWorld() {
        return this.location.getWorld();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getX() {
        return this.location.getBlockX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getY() {
        return this.location.getBlockY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getZ() {
        return this.location.getBlockZ();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getLocation() {
        return this.location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chunk getChunk() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setData(byte data) {
    }

    @Override
    public void setData(byte data, boolean applyPhyiscs) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setType(Material type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setTypeId(int type) {
        return false;
    }

    @Override
    public boolean setTypeId(int type, boolean applyPhysics) {
        return false;
    }

    @Override
    public boolean setTypeIdAndData(int type, byte data, boolean applyPhyiscs) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockFace getFace(Block block) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockState getState() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Biome getBiome() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockPowered() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockIndirectlyPowered() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockFacePowered(BlockFace face) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBlockPower(BlockFace face) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBlockPower() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return this.type == Material.AIR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLiquid() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTemperature() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHumidity() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return null;
    }

    @Override
    public boolean breakNaturally() {
        return false;
    }

    @Override
    public boolean breakNaturally(ItemStack itemStack) {
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return null;
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack itemStack) {
        return null;
    }
}
