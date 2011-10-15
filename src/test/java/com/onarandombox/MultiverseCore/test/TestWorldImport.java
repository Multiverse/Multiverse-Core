package com.onarandombox.MultiverseCore.test;

import com.onarandombox.MultiverseCore.MultiverseCore;
import junit.framework.Assert;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PluginManager.class, MultiverseCore.class, Permission.class})
public class TestWorldImport {
    ConsoleCommandSender mockCommandSender;
    private World mockWorld;
    private World mockNetherWorld;
    private List<World> worldList;
    private Server mockServer;
    private PluginManager mockPluginManager;
    private PluginLoader pluginLoader;
    MultiverseCore core;

    @Before
    public void setUp() throws Exception {
        // Initialize a fake console.
        this.mockCommandSender = mock(ConsoleCommandSender.class);

        // Initialize a fake world and world_nether.
        this.mockWorld = mock(World.class);
        when(this.mockWorld.getName()).thenReturn("world");
        when(this.mockWorld.getEnvironment()).thenReturn(World.Environment.NORMAL);

        this.mockNetherWorld = mock(World.class);
        when(this.mockNetherWorld.getName()).thenReturn("world_nether");
        when(this.mockNetherWorld.getEnvironment()).thenReturn(World.Environment.NETHER);

        // Initialize our fake worldlist.
        this.worldList = new ArrayList<World>();
        this.worldList.add(mockWorld);


        JavaPlugin[] plugins = new JavaPlugin[]{core};

        PowerMockito.whenNew(Permission.class).withArguments("Test").thenThrow(new Exception("Permission created exception"));
        Method addPermissionMethod = PowerMockito.method(PluginManager.class, "addPermission", Permission.class);
        Constructor permissionConst = PowerMockito.constructor(Permission.class, String.class, String.class, PermissionDefault.class);
        PowerMockito.suppress(permissionConst);
        Permission p = new Permission("Test", "test", PermissionDefault.OP);


        // Mock the Plugin Manager
        this.mockPluginManager = PowerMockito.mock(PluginManager.class);
        when(this.mockPluginManager.getPlugins()).thenReturn(plugins);
        when(this.mockPluginManager.getPlugin("Multiverse-Core")).thenReturn(core);

        mockPluginManager.addPermission(new Permission(""));


        // Initialize our server.
        this.mockServer = mock(Server.class);
        when(this.mockServer.getWorld("world")).thenReturn(mockWorld);
        when(this.mockServer.getWorlds()).thenReturn(worldList);
        when(this.mockServer.getPluginManager()).thenReturn(this.mockPluginManager);
        when(this.mockServer.getName()).thenReturn("FernCraft");


        PluginDescriptionFile pdf = new PluginDescriptionFile("Multiverse-Core", "2.1-Test", "com.onarandombox.MultiverseCore.MultiverseCore");
        this.core = PowerMockito.spy(new MultiverseCore());
        doReturn(this.mockServer).when(core).getServer();
        doReturn(new File(".")).when(core).getDataFolder();
        doReturn(pdf).when(core).getDescription();
        core.onLoad();
        // Enable it.
        core.onEnable();


    }

    @Test
    public void TestWorldImport() {


        // Start actual testing.
        // Create a core instance.
        Plugin plugin = this.mockServer.getPluginManager().getPlugin("Multiverse-Core");
        // Make sure Core is not null
        Assert.assertNotNull(plugin);
        // Make sure core is actually a multiverse core

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");
        String[] normalArgs = new String[]{"import", "world", "normal"};
        String[] netherArgs = new String[]{"import", "world_nether", "nether"};

        Assert.assertEquals(0, this.core.getMVWorldManager().getMVWorlds().size());

        // Import the first world.
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);

        Assert.assertEquals(1, this.core.getMVWorldManager().getMVWorlds().size());
    }
}
