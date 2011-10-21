/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test;


import com.onarandombox.MultiverseCore.MultiverseCore;
import junit.framework.Assert;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestDebugMode {
        @Test
    public void testEnableDebugMode() {
//        // Start actual testing.
//        // Pull a core instance from the server.
//        Plugin plugin = this.mockServer.getPluginManager().getPlugin("Multiverse-Core");
//
//        // Make sure Core is not null
//        Assert.assertNotNull(plugin);
//
//        // Make sure Core is enabled
//        Assert.assertTrue(plugin.isEnabled());
//
//        // Make a fake server folder to fool MV into thinking a world folder exists.
//        File serverDirectory = new File(this.core.getServerFolder(), "world");
//        serverDirectory.mkdirs();
//
//        // Initialize a fake command
//        Command mockCommand = mock(Command.class);
//        when(mockCommand.getName()).thenReturn("mv");
//
//        // Assert debug mode is off
//        Assert.assertEquals(0, MultiverseCore.GlobalDebug);
//
//        // Send the debug command.
//        String[] debugArgs = new String[]{"debug", "3"};
//        plugin.onCommand(mockCommandSender, mockCommand, "", debugArgs);
//
//        Assert.assertEquals(3, MultiverseCore.GlobalDebug);
    }
}
