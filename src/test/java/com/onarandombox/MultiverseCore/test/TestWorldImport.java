package com.onarandombox.MultiverseCore.test;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.FileUtils;
import junit.framework.Assert;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PluginManager.class, MultiverseCore.class, Permission.class, Bukkit.class})
public class TestWorldImport {
    class WorldCreatorMatcher extends ArgumentMatcher<WorldCreator> {
        private WorldCreator worldCreator;

        public WorldCreatorMatcher(WorldCreator creator) {
            this.worldCreator = creator;
        }

        public boolean matches(Object creator) {
            System.out.println("Checking world creators.");
            if (!((WorldCreator) creator).name().equals(this.worldCreator.name())) {
                System.out.println("Checking Names...");
                return false;
            } else if (!((WorldCreator) creator).environment().equals(this.worldCreator.environment())) {
                System.out.println("Checking Environments...");
                return false;
            }
            // Don't check the seed by default, as it's randomized.
// else if (((WorldCreator) creator).seed() != this.worldCreator.seed()) {
//                System.out.print("Checking Seeds...");
//                return false;
//            }
//            else if (!((WorldCreator) creator).generator().equals(this.worldCreator.generator())) {
//                System.out.print("Checking Gens...");
//                return false;
//            }
            System.out.println("Creators matched!!!");
            return true;
        }
    }

    class LocationMatcher extends ArgumentMatcher<Location> {
        private Location l;

        public LocationMatcher(Location location) {
            this.l = location;
        }

        public boolean matches(Object creator) {
            return creator.equals(l);
        }
    }

    class LocationMatcherAbove extends LocationMatcher {

        public LocationMatcherAbove(Location location) {
            super(location);
        }

        public boolean matches(Object creator) {
            System.out.println("Checking above...");
            if (super.l == null || creator == null) {
                return false;
            }
            boolean equal = ((Location) creator).getBlockY() > super.l.getBlockY();
            System.out.println("Checking equals/\\..." + equal);
            return equal;
        }
    }

    class LocationMatcherBelow extends LocationMatcher {

        public LocationMatcherBelow(Location location) {
            super(location);
        }

        public boolean matches(Object creator) {
            if (super.l == null || creator == null) {
                return false;
            }
            boolean equal = ((Location) creator).getBlockY() <= super.l.getBlockY();
            System.out.println("Checking equals\\/..." + equal);
            return equal;
        }
    }

    CommandSender mockCommandSender;
    private World mockWorld;
    private World mockNetherWorld;
    private List<World> worldList;
    private Server mockServer;
    private PluginManager mockPluginManager;
    private PluginLoader pluginLoader;
    private Bukkit mockBukkit;
    MultiverseCore core;
    File pluginDirectory;
    File serverDirectory;


    public TestWorldImport() {
        // Initialize a fake world and world_nether.
        this.mockWorld = mock(World.class);
        when(this.mockWorld.getName()).thenReturn("world");
        when(this.mockWorld.getEnvironment()).thenReturn(World.Environment.NORMAL);
        when(this.mockWorld.getSpawnLocation()).thenReturn(new Location(this.mockWorld, 0, 0, 0));
        // Build a small platform that a player can stand on:
        LocationMatcherAbove matchWorldAbove = new LocationMatcherAbove(new Location(this.mockWorld, 0, 0, 0));
        LocationMatcherBelow matchWorldBelow = new LocationMatcherBelow(new Location(this.mockWorld, 0, 0, 0));
        when(this.mockWorld.getBlockAt(Matchers.argThat(matchWorldAbove))).thenReturn(new MockBlock(new Location(this.mockWorld, 0, 0, 0), Material.AIR));
        when(this.mockWorld.getBlockAt(Matchers.argThat(matchWorldBelow))).thenReturn(new MockBlock(new Location(this.mockWorld, 0, 0, 0), Material.STONE));

        this.mockNetherWorld = mock(World.class);
        when(this.mockNetherWorld.getName()).thenReturn("world_nether");
        when(this.mockNetherWorld.getEnvironment()).thenReturn(World.Environment.NETHER);

        // Initialize our fake worldlist.
        this.worldList = new ArrayList<World>();
        this.worldList.add(mockWorld);
        this.worldList.add(mockNetherWorld);

        // Initialize Core. We must do it up here, so that the PluginManager won't return a null ref.
        this.core = PowerMockito.spy(new MultiverseCore());

        // Add Core to the list of loaded plugins
        JavaPlugin[] plugins = new JavaPlugin[]{this.core};

        // Mock the Plugin Manager
        this.mockPluginManager = PowerMockito.mock(PluginManager.class);
        when(this.mockPluginManager.getPlugins()).thenReturn(plugins);
        when(this.mockPluginManager.getPlugin("Multiverse-Core")).thenReturn(this.core);

        // Initialize the Mock server.
        this.mockServer = mock(Server.class);
        when(this.mockServer.getWorld("world")).thenReturn(mockWorld);
        when(this.mockServer.getWorld("worldNether")).thenReturn(mockNetherWorld);
        when(this.mockServer.getWorlds()).thenReturn(worldList);
        when(this.mockServer.getPluginManager()).thenReturn(this.mockPluginManager);
        when(this.mockServer.getName()).thenReturn("FernCraft");

        WorldCreatorMatcher matchWorld = new WorldCreatorMatcher(new WorldCreator("world"));
        when(this.mockServer.createWorld(Matchers.argThat(matchWorld))).thenReturn(this.mockWorld);
        Logger logger = Logger.getLogger("Multiverse-Core-Test");
        when(this.mockServer.getLogger()).thenReturn(logger);


        // Initialize a fake console.
        this.mockCommandSender = PowerMockito.spy(new TestCommandSender(this.mockServer));

        // Override some methods that bukkit normally provides us with for Core
        doReturn(this.mockServer).when(this.core).getServer();

        // Let's let all MV files go to bin/test
        pluginDirectory = new File("bin/test/server/plugins/coretest");
        serverDirectory = new File("bin/test/server");

        doReturn(pluginDirectory).when(this.core).getDataFolder();

        PluginDescriptionFile pdf = new PluginDescriptionFile("Multiverse-Core", "2.1-Test", "com.onarandombox.MultiverseCore.MultiverseCore");
        doReturn(pdf).when(this.core).getDescription();

        // Wish there was a good/easy way to teardown Bukkit
    }


    @After
    public void tearDown() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(Class.forName("org.bukkit.Bukkit"), null);
        if (serverDirectory.exists()) {
            serverDirectory.delete();
            FileUtils.deleteFolder(serverDirectory);
        }
    }

    @Before
    public void setUp() throws Exception {
        if (!serverDirectory.exists()) {
            serverDirectory.mkdirs();
        }

        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }
        Bukkit.setServer(this.mockServer);
        // Load Multiverse Core
        this.core.onLoad();

        // Enable it.
        this.core.onEnable();

        // Make sure it knows it's enabled.
        doReturn(true).when(this.core).isEnabled();
    }

    @Test
    public void testWorldImportWithNoFolder() {
        // Start actual testing.
        // Pull a core instance from the server.
        Plugin plugin = this.mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        Assert.assertNotNull(plugin);

        // Make sure Core is enabled
        Assert.assertTrue(plugin.isEnabled());
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");
        String[] normalArgs = new String[]{"import", "world", "normal"};

        // Ensure we have a fresh copy of MV, 0 worlds.
        Assert.assertEquals(0, this.core.getMVWorldManager().getMVWorlds().size());

        // Import the first world. The world folder does not exist.
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(this.mockCommandSender).sendMessage(ChatColor.RED + "FAILED.");
        verify(this.mockCommandSender).sendMessage("That world folder does not exist...");

        // We should still have no worlds.
        Assert.assertEquals(0, this.core.getMVWorldManager().getMVWorlds().size());
    }

    @Test
    public void testWorldImport() {
        // Start actual testing.
        // Pull a core instance from the server.
        Plugin plugin = this.mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make a fake server folder to fool MV into thinking a world folder exists.
        File serverDirectory = new File(this.core.getServerFolder(), "world");
        serverDirectory.mkdirs();


        // Make sure Core is not null
        Assert.assertNotNull(plugin);

        // Make sure Core is enabled
        Assert.assertTrue(plugin.isEnabled());

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");
        String[] normalArgs = new String[]{"import", "world", "normal"};
        String[] netherArgs = new String[]{"import", "world_nether", "nether"};

        // Ensure we have a fresh copy of MV, 0 worlds.
        Assert.assertEquals(0, this.core.getMVWorldManager().getMVWorlds().size());

        // Import the first world.
        plugin.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(this.mockCommandSender).sendMessage(ChatColor.AQUA + "Starting world import...");
        verify(this.mockCommandSender).sendMessage(ChatColor.GREEN + "Complete!");

        // We should now have one world imported!
        Assert.assertEquals(1, this.core.getMVWorldManager().getMVWorlds().size());
    }
}
