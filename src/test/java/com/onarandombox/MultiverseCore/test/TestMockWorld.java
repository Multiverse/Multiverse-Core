package com.onarandombox.MultiverseCore.test;

import junit.framework.Assert;
import org.bukkit.World;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class TestMockWorld {
    private World mockWorld;
    private World mockNetherWorld;
    @Test
    public void testWorldInit() {
        // Initialize a fake world and world_nether.
        this.mockWorld = PowerMockito.mock(World.class);
        when(this.mockWorld.getName()).thenReturn("world");
        when(this.mockWorld.getEnvironment()).thenReturn(World.Environment.NORMAL);

        this.mockNetherWorld = PowerMockito.mock(World.class);
        when(this.mockNetherWorld.getName()).thenReturn("world_nether");
        when(this.mockNetherWorld.getEnvironment()).thenReturn(World.Environment.NETHER);

        // Test the mock world objects
        Assert.assertEquals(this.mockWorld.getName(), "world");
        Assert.assertEquals(this.mockNetherWorld.getName(), "world_nether");
        verify(this.mockWorld).getName();
        verify(this.mockNetherWorld).getName();

        // Test the environments
        Assert.assertEquals(this.mockWorld.getEnvironment(), World.Environment.NORMAL);
        Assert.assertEquals(this.mockNetherWorld.getEnvironment(), World.Environment.NETHER);
        verify(this.mockWorld).getEnvironment();
        verify(this.mockNetherWorld).getEnvironment();
    }
}
