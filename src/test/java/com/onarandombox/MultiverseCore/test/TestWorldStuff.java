/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.test.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.test.utils.WorldCreatorMatcher;
import com.onarandombox.MultiverseCore.utils.WorldManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, MultiverseCore.class, Permission.class, Bukkit.class, WorldManager.class })
public class TestWorldStuff {

    TestInstanceCreator creator;
    Server mockServer;
    CommandSender mockCommandSender;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        mockServer = creator.getServer();
        mockCommandSender = creator.getCommandSender();
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void testWorldImportWithNoFolder() {
        // Make sure the world directory do NOT exist
        // (it was created by the TestInstanceCreator)
        File worldFile = new File(TestInstanceCreator.serverDirectory, "world");
        assertTrue(worldFile.exists());
        assertTrue(worldFile.delete());

        // Start actual testing.
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");
        String[] normalArgs = new String[] { "import", "world", "normal" };

        // Ensure we have a fresh copy of MV, 0 worlds.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world. The world folder does not exist.
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(mockCommandSender).sendMessage(ChatColor.RED + "FAILED.");
        verify(mockCommandSender).sendMessage("That world folder does not exist...");

        // We should still have no worlds.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());
    }

    @Test
    public void testWorldImport() {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world.
        String[] normalArgs = new String[] { "import", "world", "normal" };
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        // We should now have one world imported!
        assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the second world.
        String[] netherArgs = new String[] { "import", "world_nether", "nether" };
        plugin.onCommand(mockCommandSender, mockCommand, "", netherArgs);

        // We should now have 2 worlds imported!
        assertEquals(2, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the third world.
        String[] skyArgs = new String[] { "import", "world_skylands", "end" };
        plugin.onCommand(mockCommandSender, mockCommand, "", skyArgs);

        // We should now have 2 worlds imported!
        assertEquals(3, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify that the commandSender has been called 3 times.
        verify(mockCommandSender).sendMessage("Starting import of world 'world'...");
        verify(mockCommandSender).sendMessage("Starting import of world 'world_nether'...");
        verify(mockCommandSender).sendMessage("Starting import of world 'world_skylands'...");
        verify(mockCommandSender, VerificationModeFactory.times(3)).sendMessage("Complete!");
    }

    @Test
    public void testWorldCreation() {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Create the world
        String[] normalArgs = new String[] { "create", "newworld", "normal" };
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        // We should now have one world!
        assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify
        verify(mockCommandSender).sendMessage("Starting creation of world 'newworld'...");
        verify(mockCommandSender).sendMessage("Complete!");

        WorldCreatorMatcher matcher = new WorldCreatorMatcher(new WorldCreator("newworld"));
        verify(mockServer).createWorld(Matchers.argThat(matcher));
    }
}
