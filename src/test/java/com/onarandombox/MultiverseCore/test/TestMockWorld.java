package com.onarandombox.MultiverseCore.test;

import junit.framework.Assert;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class TestMockWorld {
    private World mockWorld;
    private World mockNetherWorld;

    @Before
    public void setUp() throws Exception {
        // Initialize a fake world and world_nether.
        this.mockWorld = mock(World.class);
        when(this.mockWorld.getName()).thenReturn("world");
        when(this.mockWorld.getEnvironment()).thenReturn(World.Environment.NORMAL);

        this.mockNetherWorld = mock(World.class);
        when(this.mockNetherWorld.getName()).thenReturn("world_nether");
        when(this.mockNetherWorld.getEnvironment()).thenReturn(World.Environment.NETHER);
    }

    @Test
    public void testWorldInit() {
        Assert.assertNotNull(this.mockWorld);
        Assert.assertNotNull(this.mockNetherWorld);
    }

    @Test
    public void testWorldNames() {
        // Test the mock world objects
        Assert.assertEquals(this.mockWorld.getName(), "world");
        Assert.assertEquals(this.mockNetherWorld.getName(), "world_nether");
        verify(this.mockWorld).getName();
        verify(this.mockNetherWorld).getName();
    }

    @Test
    public void testWorldEnvironments() {
        // Test the environments
        Assert.assertEquals(this.mockWorld.getEnvironment(), World.Environment.NORMAL);
        Assert.assertEquals(this.mockNetherWorld.getEnvironment(), World.Environment.NETHER);
        verify(this.mockWorld).getEnvironment();
        verify(this.mockNetherWorld).getEnvironment();
    }
}
