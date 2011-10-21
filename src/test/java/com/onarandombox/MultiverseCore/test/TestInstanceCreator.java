/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class TestInstanceCreator {
    MultiverseCore core;
    private CommandSender commandSender;

    public Server setupServerInstance() {

        MockWorldFactory worldFactory = new MockWorldFactory();
        MVCoreFactory coreFactory = new MVCoreFactory();
        MockServerFactory serverFactory = new MockServerFactory();
        core = coreFactory.getNewCore();
        // Add Core to the list of loaded plugins
        JavaPlugin[] plugins = new JavaPlugin[]{core};

        // Mock the Plugin Manager
        PluginManager mockPluginManager = PowerMockito.mock(PluginManager.class);
        when(mockPluginManager.getPlugins()).thenReturn(plugins);
        when(mockPluginManager.getPlugin("Multiverse-Core")).thenReturn(core);

        // Initialize the Mock Worlds
        World mockWorld = worldFactory.makeNewMockWorld("world", World.Environment.NORMAL);
        World mockNetherWorld = worldFactory.makeNewMockWorld("world_nether", World.Environment.NETHER);
        List<World> worldList = new ArrayList<World>();
        worldList.add(mockWorld);
        worldList.add(mockNetherWorld);


        // Initialize the Mock server.
        Server mockServer = serverFactory.getMockServer();
        when(mockServer.getWorld("world")).thenReturn(mockWorld);
        when(mockServer.getWorld("world_nether")).thenReturn(mockNetherWorld);
        when(mockServer.getWorlds()).thenReturn(worldList);
        when(mockServer.getPluginManager()).thenReturn(mockPluginManager);
        // TODO: This needs to get moved somewhere specific.
        WorldCreatorMatcher matchWorld = new WorldCreatorMatcher(new WorldCreator("world"));
        when(mockServer.createWorld(Matchers.argThat(matchWorld))).thenReturn(mockWorld);

        // Override some methods that bukkit normally provides us with for Core
        doReturn(mockServer).when(core).getServer();

        // Init our command sender
        commandSender = spy(new TestCommandSender(mockServer));
        Bukkit.setServer(mockServer);
        // Load Multiverse Core
        core.onLoad();

        // Enable it.
        core.onEnable();
        return mockServer;
    }
    public MultiverseCore getCore() {
        return this.core;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }
}
