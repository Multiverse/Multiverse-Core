package com.onarandombox.MultiverseCore.test;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.test.utils.MVCoreFactory;
import com.onarandombox.MultiverseCore.test.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.test.utils.WorldCreatorMatcher;
import com.onarandombox.MultiverseCore.utils.FileUtils;
import junit.framework.Assert;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PluginManager.class, MultiverseCore.class, Permission.class, Bukkit.class})
public class TestWorldImport {

    @After
    public void tearDown() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(Class.forName("org.bukkit.Bukkit"), null);
        if (MVCoreFactory.serverDirectory.exists()) {
            MVCoreFactory.serverDirectory.delete();
            FileUtils.deleteFolder(MVCoreFactory.serverDirectory);
        }
    }

    @Before
    public void setUp() throws Exception {
        if (!MVCoreFactory.serverDirectory.exists()) {
            MVCoreFactory.serverDirectory.mkdirs();
        }

        if (!MVCoreFactory.pluginDirectory.exists()) {
            MVCoreFactory.pluginDirectory.mkdirs();
        }
    }

    @Test
    @Ignore
    public void testWorldImportWithNoFolder() {
        TestInstanceCreator creator = new TestInstanceCreator();
        Server mockServer = creator.setupDefaultServerInstance();
        CommandSender mockCommandSender = creator.getCommandSender();

        // Make sure the world directory do NOT exist
        if (new File(MVCoreFactory.serverDirectory, "world").exists()) {
            new File(MVCoreFactory.serverDirectory, "world").delete();
        }

        // Start actual testing.
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        Assert.assertNotNull(plugin);

        // Make sure Core is enabled
        Assert.assertTrue(plugin.isEnabled());
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");
        String[] normalArgs = new String[]{"import", "world", "normal"};

        // Ensure we have a fresh copy of MV, 0 worlds.
        Assert.assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world. The world folder does not exist.
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(mockCommandSender).sendMessage(ChatColor.RED + "FAILED.");
        verify(mockCommandSender).sendMessage("That world folder does not exist...");

        // We should still have no worlds.
        Assert.assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());
    }

    @Test
    @Ignore
    public void testWorldImport() {
        TestInstanceCreator creator = new TestInstanceCreator();
        Server mockServer = creator.setupDefaultServerInstance();
        CommandSender mockCommandSender = creator.getCommandSender();
        // Start actual testing.
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        Assert.assertNotNull(plugin);

        // Make sure Core is enabled
        Assert.assertTrue(plugin.isEnabled());

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Ensure that there are no worlds imported. This is a fresh setup.
        Assert.assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world.
        String[] normalArgs = new String[]{"import", "world", "normal"};
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        // We should now have one world imported!
        Assert.assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the second world.
        String[] netherArgs = new String[]{"import", "world_nether", "nether"};
        plugin.onCommand(mockCommandSender, mockCommand, "", netherArgs);

        // We should now have 2 worlds imported!
        Assert.assertEquals(2, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the third world.
        String[] skyArgs = new String[]{"import", "world_skylands", "skylands"};
        plugin.onCommand(mockCommandSender, mockCommand, "", skyArgs);

        // We should now have 2 worlds imported!
        Assert.assertEquals(3, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify that the commandSender has been called 3 times.
        verify(mockCommandSender, VerificationModeFactory.times(3)).sendMessage(ChatColor.AQUA + "Starting world import...");
        verify(mockCommandSender, VerificationModeFactory.times(3)).sendMessage(ChatColor.GREEN + "Complete!");
    }

    @Test
    @Ignore
    public void testWorldImportWithSeed() {
        TestInstanceCreator creator = new TestInstanceCreator();
        Server mockServer = creator.setupDefaultServerInstance();
        CommandSender mockCommandSender = creator.getCommandSender();
        // Start actual testing.
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        Assert.assertNotNull(plugin);

        // Make sure Core is enabled
        Assert.assertTrue(plugin.isEnabled());

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Ensure that there are no worlds imported. This is a fresh setup.
        Assert.assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Init a new WorldCreatorMatcher to match our seeded world
        WorldCreator seedCreator = new WorldCreator("world");
        seedCreator.environment(World.Environment.NORMAL);
        WorldCreatorMatcher seedMatcher = new WorldCreatorMatcher(seedCreator);

        // For this case, we're testing a seeded import, so we care about the world seed
        seedMatcher.careAboutSeeds(true);



        // Import the first world.
        String[] normalArgs = new String[]{"import", "world", "normal", "-s", "gargamel"};
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        // We should now have one world imported!
        Assert.assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Verify that the commandSender has been called 1 time.
        verify(mockCommandSender, VerificationModeFactory.times(1)).sendMessage(ChatColor.AQUA + "Starting world import...");
        verify(mockCommandSender, VerificationModeFactory.times(1)).sendMessage(ChatColor.GREEN + "Complete!");
    }
}
