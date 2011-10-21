package com.onarandombox.MultiverseCore.test;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.FileUtils;
import junit.framework.Assert;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    public void testWorldImportWithNoFolder() {
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
    public void testWorldImport() {
        TestInstanceCreator creator = new TestInstanceCreator();
        Server mockServer = creator.setupDefaultServerInstance();
        CommandSender mockCommandSender = creator.getCommandSender();
        // Start actual testing.
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make a fake server folder to fool MV into thinking a world folder exists.
        File worldDirectory = new File(creator.getCore().getServerFolder(), "world");
        worldDirectory.mkdirs();
        File worldNetherDirectory = new File(creator.getCore().getServerFolder(), "world_nether");
        worldNetherDirectory.mkdirs();


        // Make sure Core is not null
        Assert.assertNotNull(plugin);

        // Make sure Core is enabled
        Assert.assertTrue(plugin.isEnabled());

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");
        String[] normalArgs = new String[]{"import", "world", "normal"};
        String[] netherArgs = new String[]{"import", "world_nether", "nether"};

        // Send the debug command.
        String[] debugArgs = new String[]{"debug", "3"};
        plugin.onCommand(mockCommandSender, mockCommand, "", debugArgs);

        Assert.assertEquals(0, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the first world.
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(mockCommandSender).sendMessage(ChatColor.AQUA + "Starting world import...");
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Complete!");

        // We should now have one world imported!
        Assert.assertEquals(1, creator.getCore().getMVWorldManager().getMVWorlds().size());

        // Import the second world.
        plugin.onCommand(mockCommandSender, mockCommand, "", netherArgs);
//        verify(mockCommandSender).sendMessage(ChatColor.AQUA + "Starting world import...");
//        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Complete!");

        // We should now have 2 worlds imported!
        Assert.assertEquals(2, creator.getCore().getMVWorldManager().getMVWorlds().size());
    }
}
