/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class MockWorldFactory {

    class LocationMatcher extends ArgumentMatcher<Location> {
        private Location l;

        public LocationMatcher(Location location) {
            this.l = location;
        }

        public boolean matches(Object creator) {
            return creator.equals(l);
        }
    }

    class LocationMatcherAbove extends LocationMatcher {

        public LocationMatcherAbove(Location location) {
            super(location);
        }

        public boolean matches(Object creator) {
            System.out.println("Checking above...");
            if (super.l == null || creator == null) {
                return false;
            }
            boolean equal = ((Location) creator).getBlockY() >= super.l.getBlockY();
            System.out.println("Checking equals/\\..." + equal);
            return equal;
        }
    }

    class LocationMatcherBelow extends LocationMatcher {

        public LocationMatcherBelow(Location location) {
            super(location);
        }

        public boolean matches(Object creator) {
            if (super.l == null || creator == null) {
                return false;
            }
            boolean equal = ((Location) creator).getBlockY() < super.l.getBlockY();
            System.out.println("Checking equals\\/..." + equal);
            return equal;
        }
    }

    public World makeNewMockWorld(String world, World.Environment env) {
        World mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn(world);
        when(mockWorld.getEnvironment()).thenReturn(env);
        when(mockWorld.getSpawnLocation()).thenReturn(new Location(mockWorld, 0, 0, 0));
        LocationMatcherAbove matchWorldAbove = new LocationMatcherAbove(new Location(mockWorld, 0, 0, 0));
        LocationMatcherBelow matchWorldBelow = new LocationMatcherBelow(new Location(mockWorld, 0, 0, 0));
        when(mockWorld.getBlockAt(Matchers.argThat(matchWorldAbove))).thenReturn(new MockBlock(new Location(mockWorld, 0, 0, 0), Material.AIR));
        when(mockWorld.getBlockAt(Matchers.argThat(matchWorldBelow))).thenReturn(new MockBlock(new Location(mockWorld, 0, 0, 0), Material.STONE));
        return mockWorld;
    }

    public void makeNewMockWorld(String world, World.Environment env, long seed, ChunkGenerator generator) {
        World w = this.makeNewMockWorld(world, env);
        when(w.getGenerator()).thenReturn(generator);
        when(w.getSeed()).thenReturn(seed);
    }
}
