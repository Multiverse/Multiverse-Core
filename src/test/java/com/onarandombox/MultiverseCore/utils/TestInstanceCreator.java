/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.listeners.MVEntityListener;
import com.onarandombox.MultiverseCore.listeners.MVPlayerListener;
import com.onarandombox.MultiverseCore.listeners.MVWeatherListener;
import com.onarandombox.MultiverseCore.utils.file.FileUtils;
import com.onarandombox.MultiverseCore.world.SimpleMVWorldManager;
import junit.framework.Assert;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.mockito.internal.util.reflection.ReflectionMemberAccessor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TestInstanceCreator {
    private MultiverseCore core;
    private Server mockServer;
    private CommandSender commandSender;

    public static final File pluginDirectory = new File("bin/test/server/plugins/coretest");
    public static final File serverDirectory = new File("bin/test/server");
    public static final File worldsDirectory = new File("bin/test/server");

    public boolean setUp() {
        TestingMode.enable();
        try {
            pluginDirectory.mkdirs();
            assertTrue(pluginDirectory.exists());

            // Initialize the Mock server.
            mockServer = mock(Server.class);
            JavaPluginLoader mockPluginLoader = mock(JavaPluginLoader.class);
            new ReflectionMemberAccessor().set(JavaPluginLoader.class.getDeclaredField("server"), mockPluginLoader, mockServer);
            when(mockServer.getName()).thenReturn("TestBukkit");
            Logger.getLogger("Minecraft").setParent(Util.logger);
            when(mockServer.getLogger()).thenReturn(Util.logger);
            when(mockServer.getWorldContainer()).thenReturn(worldsDirectory);

            // Return a fake PDF file.
            PluginDescriptionFile pdf = spy(new PluginDescriptionFile("Multiverse-Core", "2.2-Test",
                    "com.onarandombox.MultiverseCore.MultiverseCore"));
            when(pdf.getAuthors()).thenReturn(new ArrayList<String>());
            core = spy(new MultiverseCore(mockPluginLoader, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));

            // Let's let all MV files go to bin/test
            doReturn(pluginDirectory).when(core).getDataFolder();

            doReturn(true).when(core).isEnabled();
            doReturn(Util.logger).when(core).getLogger();
            core.setServerFolder(serverDirectory);

            // Add Core to the list of loaded plugins
            JavaPlugin[] plugins = new JavaPlugin[] { core };

            // Mock the Plugin Manager
            PluginManager mockPluginManager = mock(PluginManager.class);
            when(mockPluginManager.getPlugins()).thenReturn(plugins);
            when(mockPluginManager.getPlugin("Multiverse-Core")).thenReturn(core);
            when(mockPluginManager.getPermission(anyString())).thenReturn(null);
            // Tell Buscript Vault is not available.
            when(mockPluginManager.getPermission("Vault")).thenReturn(null);

            // Make some fake folders to fool the fake MV into thinking these worlds exist
            File worldNormalFile = new File(core.getServerFolder(), "world");
            Util.log("Creating world-folder: " + worldNormalFile.getAbsolutePath());
            worldNormalFile.mkdirs();
            File worldNetherFile = new File(core.getServerFolder(), "world_nether");
            Util.log("Creating world-folder: " + worldNetherFile.getAbsolutePath());
            worldNetherFile.mkdirs();
            File worldSkylandsFile = new File(core.getServerFolder(), "world_the_end");
            Util.log("Creating world-folder: " + worldSkylandsFile.getAbsolutePath());
            worldSkylandsFile.mkdirs();

            // Give the server some worlds
            when(mockServer.getWorld(anyString())).thenAnswer(new Answer<World>() {
                @Override
                public World answer(InvocationOnMock invocation) throws Throwable {
                    String arg;
                    try {
                        arg = (String) invocation.getArguments()[0];
                    } catch (Exception e) {
                        return null;
                    }
                    return MockWorldFactory.getWorld(arg);
                }
            });

            when(mockServer.getWorld(any(UUID.class))).thenAnswer(new Answer<World>() {
                @Override
                public World answer(InvocationOnMock invocation) throws Throwable {
                    UUID arg;
                    try {
                        arg = (UUID) invocation.getArguments()[0];
                    } catch (Exception e) {
                        return null;
                    }
                    return MockWorldFactory.getWorld(arg);
                }
            });

            when(mockServer.getWorlds()).thenAnswer(new Answer<List<World>>() {
                @Override
                public List<World> answer(InvocationOnMock invocation) throws Throwable {
                    return MockWorldFactory.getWorlds();
                }
            });

            when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

            when(mockServer.createWorld(isA(WorldCreator.class))).thenAnswer(
                    new Answer<World>() {
                        @Override
                        public World answer(InvocationOnMock invocation) throws Throwable {
                            WorldCreator arg;
                            try {
                                arg = (WorldCreator) invocation.getArguments()[0];
                            } catch (Exception e) {
                                return null;
                            }
                            // Add special case for creating null worlds.
                            // Not sure I like doing it this way, but this is a special case
                            if (arg.name().equalsIgnoreCase("nullworld")) {
                                return MockWorldFactory.makeNewNullMockWorld(arg.name(), arg.environment(), arg.type());
                            }
                            return MockWorldFactory.makeNewMockWorld(arg.name(), arg.environment(), arg.type());
                        }
                    });

            when(mockServer.unloadWorld(anyString(), anyBoolean())).thenReturn(true);

            // add mock scheduler
            BukkitScheduler mockScheduler = mock(BukkitScheduler.class);
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class), anyLong())).
            thenAnswer(new Answer<Integer>() {
                @Override
                public Integer answer(InvocationOnMock invocation) throws Throwable {
                    Runnable arg;
                    try {
                        arg = (Runnable) invocation.getArguments()[1];
                    } catch (Exception e) {
                        return null;
                    }
                    arg.run();
                    return null;
                }});
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class))).
            thenAnswer(new Answer<Integer>() {
                @Override
                public Integer answer(InvocationOnMock invocation) throws Throwable {
                    Runnable arg;
                    try {
                        arg = (Runnable) invocation.getArguments()[1];
                    } catch (Exception e) {
                        return null;
                    }
                    arg.run();
                    return null;
                }});
            when(mockServer.getScheduler()).thenReturn(mockScheduler);

            // Set server
            Field serverfield = JavaPlugin.class.getDeclaredField("server");
            serverfield.setAccessible(true);
            serverfield.set(core, mockServer);

            // Set worldManager
            SimpleMVWorldManager wm = spy(new SimpleMVWorldManager(core));
            Field worldmanagerfield = MultiverseCore.class.getDeclaredField("worldManager");
            worldmanagerfield.setAccessible(true);
            worldmanagerfield.set(core, wm);

            // Set playerListener
            MVPlayerListener pl = spy(new MVPlayerListener(core));
            Field playerlistenerfield = MultiverseCore.class.getDeclaredField("playerListener");
            playerlistenerfield.setAccessible(true);
            playerlistenerfield.set(core, pl);

            // Set entityListener
            MVEntityListener el = spy(new MVEntityListener(core));
            Field entitylistenerfield = MultiverseCore.class.getDeclaredField("entityListener");
            entitylistenerfield.setAccessible(true);
            entitylistenerfield.set(core, el);

            // Set weatherListener
            MVWeatherListener wl = spy(new MVWeatherListener(core));
            Field weatherlistenerfield = MultiverseCore.class.getDeclaredField("weatherListener");
            weatherlistenerfield.setAccessible(true);
            weatherlistenerfield.set(core, wl);

            // Init our command sender
            final Logger commandSenderLogger = Logger.getLogger("CommandSender");
            commandSenderLogger.setParent(Util.logger);
            commandSender = mock(CommandSender.class);
            doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    commandSenderLogger.info(ChatColor.stripColor((String) invocation.getArguments()[0]));
                    return null;
                }}).when(commandSender).sendMessage(anyString());
            when(commandSender.getServer()).thenReturn(mockServer);
            when(commandSender.getName()).thenReturn("MockCommandSender");
            when(commandSender.isPermissionSet(anyString())).thenReturn(true);
            when(commandSender.isPermissionSet(isA(Permission.class))).thenReturn(true);
            when(commandSender.hasPermission(anyString())).thenReturn(true);
            when(commandSender.hasPermission(isA(Permission.class))).thenReturn(true);
            when(commandSender.addAttachment(core)).thenReturn(null);
            when(commandSender.isOp()).thenReturn(true);

            Bukkit.setServer(mockServer);

            // Load Multiverse Core
            core.onLoad();

            // Enable it.
            core.onEnable();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean tearDown() {
        List<MVWorld> worlds = new ArrayList<MVWorld>(core.getMVWorldManager()
                .getMVWorlds());
        for (MVWorld world : worlds) {
            core.getMVWorldManager().deleteWorld(world.getName());
        }

        try {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(Class.forName("org.bukkit.Bukkit"), null);
        } catch (Exception e) {
            Util.log(Level.SEVERE,
                    "Error while trying to unregister the server from Bukkit. Has Bukkit changed?");
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return false;
        }

        core.onDisable();

        FileUtils.deleteFolder(serverDirectory);
        MockWorldFactory.clearWorlds();

        return true;
    }

    public MultiverseCore getCore() {
        return this.core;
    }

    public Server getServer() {
        return this.mockServer;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }
}
