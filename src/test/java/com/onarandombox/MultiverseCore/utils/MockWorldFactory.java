/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.generator.ChunkGenerator;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockWorldFactory {

    private static final Map<String, World> createdWorlds = new LinkedHashMap<String, World>();
    private static final Map<UUID, World> worldUIDS = new HashMap<UUID, World>();

    private static final Map<World, Boolean> pvpStates = new WeakHashMap<World, Boolean>();
    private static final Map<World, Boolean> keepSpawnInMemoryStates = new WeakHashMap<World, Boolean>();
    private static final Map<World, Difficulty> difficultyStates = new WeakHashMap<World, Difficulty>();

    private MockWorldFactory() {
    }

    private static void registerWorld(World world) {
        createdWorlds.put(world.getName(), world);
        worldUIDS.put(world.getUID(), world);
        createWorldDirectory(world.getName());
    }

    public static void createWorldDirectory(String worldName) {
        File worldFolder = new File(TestInstanceCreator.worldsDirectory, worldName);
        worldFolder.mkdir();
        try {
            new File(worldFolder, "level.dat").createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static World basics(String world, World.Environment env, WorldType type) {
        World mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn(world);
        when(mockWorld.getPVP()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                World w = (World) invocation.getMock();
                if (!pvpStates.containsKey(w))
                    pvpStates.put(w, true); // default value
                return pvpStates.get(w);
            }
        });
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                pvpStates.put((World) invocation.getMock(), (Boolean) invocation.getArguments()[0]);
                return null;
            }
        }).when(mockWorld).setPVP(anyBoolean());
        when(mockWorld.getKeepSpawnInMemory()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                World w = (World) invocation.getMock();
                if (!keepSpawnInMemoryStates.containsKey(w))
                    keepSpawnInMemoryStates.put(w, true); // default value
                return keepSpawnInMemoryStates.get(w);
            }
        });
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                keepSpawnInMemoryStates.put((World) invocation.getMock(), (Boolean) invocation.getArguments()[0]);
                return null;
            }
        }).when(mockWorld).setKeepSpawnInMemory(anyBoolean());
        when(mockWorld.getDifficulty()).thenAnswer(new Answer<Difficulty>() {
            @Override
            public Difficulty answer(InvocationOnMock invocation) throws Throwable {
                World w = (World) invocation.getMock();
                if (!difficultyStates.containsKey(w))
                    difficultyStates.put(w, Difficulty.NORMAL); // default value
                return difficultyStates.get(w);
            }
        });
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                difficultyStates.put((World) invocation.getMock(), (Difficulty) invocation.getArguments()[0]);
                return null;
            }
        }).when(mockWorld).setDifficulty(any(Difficulty.class));
        when(mockWorld.getEnvironment()).thenReturn(env);
        when(mockWorld.getWorldType()).thenReturn(type);
        when(mockWorld.getSpawnLocation()).thenReturn(new Location(mockWorld, 0, 64, 0));
        when(mockWorld.getWorldFolder()).thenAnswer(new Answer<File>() {
            @Override
            public File answer(InvocationOnMock invocation) throws Throwable {
                if (!(invocation.getMock() instanceof World))
                    return null;

                World thiss = (World) invocation.getMock();
                return new File(TestInstanceCreator.serverDirectory, thiss.getName());
            }
        });
        when(mockWorld.getBlockAt(any(Location.class))).thenAnswer(new Answer<Block>() {
            @Override
            public Block answer(InvocationOnMock invocation) throws Throwable {
                Location loc;
                try {
                    loc = (Location) invocation.getArguments()[0];
                } catch (Exception e) {
                    return null;
                }
                Material blockType = Material.AIR;
                Block mockBlock = mock(Block.class);
                if (loc.getBlockY() < 64) {
                    blockType = Material.DIRT;
                }

                when(mockBlock.getType()).thenReturn(blockType);
                when(mockBlock.getWorld()).thenReturn(loc.getWorld());
                when(mockBlock.getX()).thenReturn(loc.getBlockX());
                when(mockBlock.getY()).thenReturn(loc.getBlockY());
                when(mockBlock.getZ()).thenReturn(loc.getBlockZ());
                when(mockBlock.getLocation()).thenReturn(loc);
                when(mockBlock.isEmpty()).thenReturn(blockType == Material.AIR);
                return mockBlock;
            }
        });
        when(mockWorld.getUID()).thenReturn(UUID.randomUUID());
        return mockWorld;
    }

    private static World nullWorld(String world, World.Environment env, WorldType type) {
        World mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn(world);
        when(mockWorld.getEnvironment()).thenReturn(env);
        when(mockWorld.getWorldType()).thenReturn(type);
        when(mockWorld.getSpawnLocation()).thenReturn(new Location(mockWorld, 0, 64, 0));
        when(mockWorld.getWorldFolder()).thenAnswer(new Answer<File>() {
            @Override
            public File answer(InvocationOnMock invocation) throws Throwable {
                if (!(invocation.getMock() instanceof World))
                    return null;

                World thiss = (World) invocation.getMock();
                return new File(TestInstanceCreator.serverDirectory, thiss.getName());
            }
        });
        when(mockWorld.getBlockAt(any(Location.class))).thenAnswer(new Answer<Block>() {
            @Override
            public Block answer(InvocationOnMock invocation) throws Throwable {
                Location loc;
                try {
                    loc = (Location) invocation.getArguments()[0];
                } catch (Exception e) {
                    return null;
                }

                Block mockBlock = mock(Block.class);
                Material blockType = Material.AIR;

                when(mockBlock.getType()).thenReturn(blockType);
                when(mockBlock.getWorld()).thenReturn(loc.getWorld());
                when(mockBlock.getX()).thenReturn(loc.getBlockX());
                when(mockBlock.getY()).thenReturn(loc.getBlockY());
                when(mockBlock.getZ()).thenReturn(loc.getBlockZ());
                when(mockBlock.getLocation()).thenReturn(loc);
                when(mockBlock.isEmpty()).thenReturn(blockType == Material.AIR);
                return mockBlock;
            }
        });
        when(mockWorld.getUID()).thenReturn(UUID.randomUUID());
        return mockWorld;
    }

    public static World makeNewMockWorld(String world, World.Environment env, WorldType type) {
        World w = basics(world, env, type);
        registerWorld(w);
        return w;
    }

    public static World makeNewNullMockWorld(String world, World.Environment env, WorldType type) {
        World w = nullWorld(world, env, type);
        registerWorld(w);
        return w;
    }

    public static World makeNewMockWorld(String world, World.Environment env, WorldType type, long seed,
            ChunkGenerator generator) {
        World mockWorld = basics(world, env, type);
        when(mockWorld.getGenerator()).thenReturn(generator);
        when(mockWorld.getSeed()).thenReturn(seed);
        registerWorld(mockWorld);
        return mockWorld;
    }

    public static World getWorld(String name) {
        return createdWorlds.get(name);
    }

    public static World getWorld(UUID worldUID) {
        return worldUIDS.get(worldUID);
    }

    public static List<World> getWorlds() {
        return new ArrayList<World>(createdWorlds.values());
    }

    public static void clearWorlds() {
        for (String name : createdWorlds.keySet())
            new File(TestInstanceCreator.worldsDirectory, name).delete();
        createdWorlds.clear();
        worldUIDS.clear();
    }
}
