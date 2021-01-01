/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import com.onarandombox.MultiverseCore.utils.MockWorldFactory;
import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.utils.WorldCreatorMatcher;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, MultiverseCore.class, Permission.class, Bukkit.class, WorldManager.class,
        PluginDescriptionFile.class, JavaPluginLoader.class })
@PowerMockIgnore("javax.script.*")
public class TestWorldStuff {

    private TestInstanceCreator creator;
    private Server mockServer;
    private CommandSender mockCommandSender;

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

        // Ensure we have a fresh copy of MV, 0 worlds.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world. The world folder does not exist.
        assertTrue(creator.dispatch(mockCommandSender, "mv import world normal"));
        verify(mockCommandSender).sendMessage(ChatColor.RED + "Error: World folder 'world' does not exist.");

        // We should still have no worlds.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());
    }

    @Test
    public void testWorldImport() {
        MockWorldFactory.createWorldDirectory("world");
        MockWorldFactory.createWorldDirectory("world_nether");
        MockWorldFactory.createWorldDirectory("world_the_end");

        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world.
        assertTrue(creator.dispatch(mockCommandSender, "mv import world normal"));

        // We should now have one world imported!
        assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the second world.
        assertTrue(creator.dispatch(mockCommandSender, "mv import world_nether nether"));

        // We should now have 2 worlds imported!
        assertEquals(2, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the third world.
        assertTrue(creator.dispatch(mockCommandSender, "mv import world_the_end end"));

        // We should now have 2 worlds imported!
        assertEquals(3, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify that the commandSender has been called 3 times.
        verify(mockCommandSender).sendMessage("Starting import of world 'world'...");
        verify(mockCommandSender).sendMessage("Starting import of world 'world_nether'...");
        verify(mockCommandSender).sendMessage("Starting import of world 'world_the_end'...");
        verify(mockCommandSender, VerificationModeFactory.times(3)).sendMessage(ChatColor.GREEN + "Complete!");
    }

    @Test
    public void testWorldCreation() {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Create the world
        assertTrue(creator.dispatch(mockCommandSender, "mv create newworld normal"));

        // We should now have one world!
        assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify
        verify(mockCommandSender).sendMessage("Starting creation of world 'newworld'...");
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Complete!");

        WorldCreatorMatcher matcher = new WorldCreatorMatcher(new WorldCreator("newworld"));
        verify(mockServer).createWorld(ArgumentMatchers.argThat(matcher));
    }

    @Test
    public void testWorldCreateInvalidGenerator() {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Create the world
        assertTrue(creator.dispatch(mockCommandSender, "mv create newworld normal -g BogusGen"));

        // This command should halt, not creating any worlds
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify
        verify(mockCommandSender).sendMessage(ChatColor.RED + "Invalid generator string 'BogusGen'.");
    }

    @Test
    public void testNullWorld() {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Create the NULL world
        // The safe check is now BALLS SLOW. Use the -n to skip checking.
        assertTrue(creator.dispatch(mockCommandSender, "mv create nullworld normal -n"));

        // We should now have one world!
        assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify
        verify(mockCommandSender).sendMessage("Starting creation of world 'nullworld'...");
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Complete!");

        WorldCreatorMatcher matcher = new WorldCreatorMatcher(new WorldCreator("nullworld"));
        verify(mockServer).createWorld(ArgumentMatchers.argThat(matcher));
    }

    @Test
    // TODO Migrate this to TestWorldProperties
    public void testModifyGameMode() {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());
        this.createInitialWorlds();

        // Ensure that the default worlds have been created.
        assertEquals(3, creator.getCore().getMVWorldManager().getMVWorlds().size());
        MultiverseWorld mainWorld = creator.getCore().getMVWorldManager().getMVWorld("world");

        // Ensure that the default mode was normal.
        assertEquals(GameMode.SURVIVAL, mainWorld.getGameMode());

        // Set the mode to creative in world.
        assertTrue(creator.dispatch(mockCommandSender, "mv modify set mode creative world"));
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Success! " + ChatColor.WHITE + "Property " + ChatColor.AQUA + "mode " + ChatColor.WHITE + "was set to " + ChatColor.GREEN + "creative" + ChatColor.WHITE + ".");
        // Ensure the world is now a creative world
        assertEquals(GameMode.CREATIVE, mainWorld.getGameMode());

        // More tests, with alternate syntax.
        assertTrue(creator.dispatch(mockCommandSender, "mv modify set gamemode 0 world"));
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Success! " + ChatColor.WHITE + "Property " + ChatColor.AQUA + "gamemode " + ChatColor.WHITE + "was set to " + ChatColor.GREEN + "0" + ChatColor.WHITE + ".");
        assertEquals(GameMode.SURVIVAL, mainWorld.getGameMode());

        // Now fail one.
        assertTrue(creator.dispatch(mockCommandSender, "mv modify set mode fish world"));
        try {
            verify(mockCommandSender).sendMessage(ChatColor.RED + mainWorld.getPropertyHelp("mode"));
        } catch (PropertyDoesNotExistException e) {
            fail("Mode property did not exist.");
        }

        assertTrue(creator.dispatch(mockCommandSender, "mv modify set blah fish world"));
        verify(mockCommandSender).sendMessage(ChatColor.RED + "Sorry, You can't set '" + ChatColor.GRAY + "blah" + ChatColor.RED + "'!");
    }

    private void createInitialWorlds() {
        MockWorldFactory.createWorldDirectory("world");
        MockWorldFactory.createWorldDirectory("world_nether");
        MockWorldFactory.createWorldDirectory("world_the_end");
        assertTrue(creator.dispatch(mockCommandSender, "mv import world normal"));
        assertTrue(creator.dispatch(mockCommandSender, "mv import world_nether nether"));
        assertTrue(creator.dispatch(mockCommandSender, "mv import world_the_end end"));
        verify(mockCommandSender).sendMessage("Starting import of world 'world'...");
        verify(mockCommandSender).sendMessage("Starting import of world 'world_nether'...");
        verify(mockCommandSender).sendMessage("Starting import of world 'world_the_end'...");
        verify(mockCommandSender, times(3)).sendMessage(ChatColor.GREEN + "Complete!");
    }
}
