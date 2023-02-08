/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import com.onarandombox.MultiverseCore.utils.MockWorldFactory;
import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.utils.WorldCreatorMatcher;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.internal.verification.VerificationModeFactory;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");
        String[] normalArgs = new String[]{ "import", "world", "normal" };

        // Ensure we have a fresh copy of MV, 0 worlds.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world. The world folder does not exist.
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(mockCommandSender).sendMessage(ChatColor.RED + "FAILED.");
        verify(mockCommandSender).sendMessage("That world folder does not exist. These look like worlds to me:");

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

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world.
        String[] normalArgs = new String[]{ "import", "world", "normal" };
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        // We should now have one world imported!
        assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the second world.
        String[] netherArgs = new String[]{ "import", "world_nether", "nether" };
        plugin.onCommand(mockCommandSender, mockCommand, "", netherArgs);

        // We should now have 2 worlds imported!
        assertEquals(2, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the third world.
        String[] skyArgs = new String[]{ "import", "world_the_end", "end" };
        plugin.onCommand(mockCommandSender, mockCommand, "", skyArgs);

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

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Create the world
        String[] normalArgs = new String[]{ "create", "newworld", "normal" };
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        // We should now have one world!
        assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify
        verify(mockCommandSender).sendMessage("Starting creation of world 'newworld'...");
        verify(mockCommandSender).sendMessage("Complete!");

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

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Create the world
        String[] normalArgs = new String[]{ "create", "newworld", "normal", "-g", "BogusGen"};
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        // This command should halt, not creating any worlds
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify
        verify(mockCommandSender).sendMessage("Invalid generator! 'BogusGen'. " + ChatColor.RED + "Aborting world creation.");
    }

    @Test
    public void testNullWorld() {
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

        // Create the NULL world
        // The safe check is now BALLS SLOW. Use the -n to skip checking.
        String[] normalArgs = new String[]{ "create", "nullworld", "normal", "-n" };
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        // We should now have one world!
        assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify
        verify(mockCommandSender).sendMessage("Starting creation of world 'nullworld'...");
        verify(mockCommandSender).sendMessage("Complete!");

        WorldCreatorMatcher matcher = new WorldCreatorMatcher(new WorldCreator("nullworld"));
        verify(mockServer).createWorld(ArgumentMatchers.argThat(matcher));
    }

    @Test
    // TODO Migrate this to TestWorldProperties
    public void testModifyGameMode() {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Ensure that there are no worlds imported. This is a fresh setup.
        assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());
        this.createInitialWorlds(plugin, mockCommand);

        // Ensure that the default worlds have been created.
        assertEquals(3, creator.getCore().getMVWorldManager().getMVWorlds().size());
        MVWorld mainWorld = creator.getCore().getMVWorldManager().getMVWorld("world");

        // Ensure that the default mode was normal.
        assertEquals(GameMode.SURVIVAL, mainWorld.getGameMode());

        // Set the mode to creative in world.
        plugin.onCommand(mockCommandSender, mockCommand, "", new String[]{ "modify", "set", "mode", "creative", "world" });
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Success!" + ChatColor.WHITE + " Property " + ChatColor.AQUA + "mode" + ChatColor.WHITE + " was set to " + ChatColor.GREEN + "creative");
        // Ensure the world is now a creative world
        assertEquals(GameMode.CREATIVE, mainWorld.getGameMode());

        // More tests, with alternate syntax.
        plugin.onCommand(mockCommandSender, mockCommand, "", new String[]{ "modify", "set", "gamemode", "0", "world" });
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Success!" + ChatColor.WHITE + " Property " + ChatColor.AQUA + "gamemode" + ChatColor.WHITE + " was set to " + ChatColor.GREEN + "0");
        assertEquals(GameMode.SURVIVAL, mainWorld.getGameMode());

        // Now fail one.
        plugin.onCommand(mockCommandSender, mockCommand, "", new String[]{ "modify", "set", "mode", "fish", "world" });
        try {
            verify(mockCommandSender).sendMessage(ChatColor.RED + mainWorld.getPropertyHelp("mode"));
        } catch (PropertyDoesNotExistException e) {
            fail("Mode property did not exist.");
        }

        plugin.onCommand(mockCommandSender, mockCommand, "", new String[]{ "modify", "set", "blah", "fish", "world" });
        verify(mockCommandSender).sendMessage(ChatColor.RED + "Sorry, You can't set: '" + ChatColor.GRAY + "blah" + ChatColor.RED + "'");
    }

    private void createInitialWorlds(Plugin plugin, Command command) {
        MockWorldFactory.createWorldDirectory("world");
        MockWorldFactory.createWorldDirectory("world_nether");
        MockWorldFactory.createWorldDirectory("world_the_end");
        plugin.onCommand(mockCommandSender, command, "", new String[]{ "import", "world", "normal" });
        plugin.onCommand(mockCommandSender, command, "", new String[]{ "import", "world_nether", "nether" });
        plugin.onCommand(mockCommandSender, command, "", new String[]{ "import", "world_the_end", "end" });
        verify(mockCommandSender).sendMessage("Starting import of world 'world'...");
        verify(mockCommandSender).sendMessage("Starting import of world 'world_nether'...");
        verify(mockCommandSender).sendMessage("Starting import of world 'world_the_end'...");
        verify(mockCommandSender, times(3)).sendMessage(ChatColor.GREEN + "Complete!");
    }
}
