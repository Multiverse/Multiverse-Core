/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test.utils;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class TestInstanceCreator {
    MultiverseCore core;
    private CommandSender commandSender;

    public Server setupDefaultServerInstance() {

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

        // Make some fake folders to fool the fake MV into thinking these worlds exist
        new File(core.getServerFolder(), "world").mkdirs();
        new File(core.getServerFolder(), "world_nether").mkdirs();
        new File(core.getServerFolder(), "world_skylands").mkdirs();

        // Initialize the Mock Worlds
        World mockWorld = worldFactory.makeNewMockWorld("world", World.Environment.NORMAL);
        World mockNetherWorld = worldFactory.makeNewMockWorld("world_nether", World.Environment.NETHER);
        World mockSkyWorld = worldFactory.makeNewMockWorld("world_skylands", World.Environment.SKYLANDS);

        List<World> worldList = new ArrayList<World>();
        worldList.add(mockWorld);
        worldList.add(mockNetherWorld);
        worldList.add(mockSkyWorld);

        // Initialize the Mock server.
        Server mockServer = serverFactory.getMockServer();

        // Give the server some worlds
        when(mockServer.getWorld("world")).thenReturn(mockWorld);
        when(mockServer.getWorld("world_nether")).thenReturn(mockNetherWorld);
        when(mockServer.getWorld("world_skylands")).thenReturn(mockNetherWorld);
        when(mockServer.getWorlds()).thenReturn(worldList);
        when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

        // Initialize some worldCreatorMatchers (so we can see when a specific creator is called)
        WorldCreatorMatcher matchWorld = new WorldCreatorMatcher(new WorldCreator("world"));
        WorldCreator netherCreator = new WorldCreator("world_nether");
        netherCreator.environment(World.Environment.NETHER);
        WorldCreatorMatcher matchNetherWorld = new WorldCreatorMatcher(netherCreator);

        WorldCreator skyCreator = new WorldCreator("world_skylands");
        skyCreator.environment(World.Environment.SKYLANDS);
        WorldCreatorMatcher matchSkyWorld = new WorldCreatorMatcher(skyCreator);

        // If a specific creator is called, return the appropreate world.
        when(mockServer.createWorld(Matchers.argThat(matchWorld))).thenReturn(mockWorld);
        when(mockServer.createWorld(Matchers.argThat(matchNetherWorld))).thenReturn(mockNetherWorld);
        when(mockServer.createWorld(Matchers.argThat(matchSkyWorld))).thenReturn(mockSkyWorld);

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
