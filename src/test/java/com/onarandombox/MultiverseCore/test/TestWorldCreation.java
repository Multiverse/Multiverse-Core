package com.onarandombox.MultiverseCore.test;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.test.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MultiverseCore.class, PluginDescriptionFile.class  })
public class TestWorldCreation {

    private TestInstanceCreator creator;
    private MultiverseCore core;
    private CommandSender mockCommandSender;

    @Before
    public void setUp() throws Exception {
        this.creator = new TestInstanceCreator();
        assertTrue(this.creator.setUp());
        this.core = this.creator.getCore();
        this.mockCommandSender = this.creator.getCommandSender();
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    @Ignore
    public void test() {
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Try to create a world that exists
        String[] normalArgs = new String[] { "create", "world", "normal" };
        core.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(mockCommandSender).sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
        verify(mockCommandSender).sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");

        // Try to create a world that is new
        String[] newArgs = new String[] { "create", "world2", "normal" };
        core.onCommand(mockCommandSender, mockCommand, "", newArgs);
        verify(mockCommandSender).sendMessage("Starting creation of world 'world2'...");

        String[] dottedWorld = new String[] { "create", "fish.world", "normal" };
        core.onCommand(mockCommandSender, mockCommand, "", dottedWorld);
        verify(mockCommandSender).sendMessage("Starting creation of world 'fish.world'...");
        verify(mockCommandSender, VerificationModeFactory.times(2)).sendMessage("Complete!");

        // Grab the Config
        Field worldConfigField = null;
        ConfigurationSection worldsSection = null;
        try {
            worldConfigField = WorldManager.class.getDeclaredField("configWorlds");
            worldConfigField.setAccessible(true);
            Configuration rootConfig = (Configuration) worldConfigField.get(this.core.getMVWorldManager());
            worldsSection = rootConfig.getConfigurationSection("worlds");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // Verify that the world was added to the configs
        // TODO: Expand this.
        assertNotNull(worldsSection);
        assertEquals(2, worldsSection.getKeys(false).size());
        assertTrue(worldsSection.getKeys(false).contains("world2"));
        // TODO: Uncomment once this is fixed!!!
        //assertTrue(worldsSection.getKeys(false).contains("'fish.world'"));

        // Worlds with .s are a special case, verify that they work.
    }
}
