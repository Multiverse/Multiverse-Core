/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test;


import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.test.utils.MVCoreFactory;
import com.onarandombox.MultiverseCore.test.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.utils.FileUtils;
import junit.framework.Assert;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MultiverseCore.class})
public class TestDebugMode {
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
    public void testEnableDebugMode() {
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

        // Make a fake server folder to fool MV into thinking a world folder exists.
        File serverDirectory = new File(creator.getCore().getServerFolder(), "world");
        serverDirectory.mkdirs();

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Assert debug mode is off
        Assert.assertEquals(0, MultiverseCore.GlobalDebug);

        // Send the debug command.
        String[] debugArgs = new String[]{"debug", "3"};
        plugin.onCommand(mockCommandSender, mockCommand, "", debugArgs);

        Assert.assertEquals(3, MultiverseCore.GlobalDebug);
    }
}
