/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2012.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.configuration.SpawnLocation;
import com.onarandombox.MultiverseCore.listeners.MVAsyncPlayerChatListener;
import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, MultiverseCore.class, Permission.class, Bukkit.class,
        WeatherChangeEvent.class, ThunderChangeEvent.class, AsyncPlayerChatEvent.class,
        PlayerJoinEvent.class, PlayerRespawnEvent.class, EntityRegainHealthEvent.class,
        FoodLevelChangeEvent.class, WorldManager.class, PluginDescriptionFile.class, JavaPluginLoader.class })
@PowerMockIgnore("javax.script.*")
public class TestWorldProperties {
    private TestInstanceCreator creator;
    private MultiverseCore core;
    private CommandSender mockCommandSender;

    // Events
    private WeatherChangeEvent weatherChangeOffEvent;
    private WeatherChangeEvent weatherChangeOnEvent;
    private ThunderChangeEvent thunderChangeOffEvent;
    private ThunderChangeEvent thunderChangeOnEvent;
    private Player mockPlayer;
    private AsyncPlayerChatEvent playerChatEvent;
    private Player mockNewPlayer;
    private PlayerJoinEvent playerNewJoinEvent;
    private PlayerJoinEvent playerJoinEvent;
    private PlayerRespawnEvent playerRespawnBed;
    private PlayerRespawnEvent playerRespawnNormal;
    private HumanEntity mockHumanEntity;
    private EntityRegainHealthEvent entityRegainHealthEvent;
    private FoodLevelChangeEvent entityFoodLevelChangeEvent;
    private FoodLevelChangeEvent entityFoodLevelRiseEvent;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        core = creator.getCore();
        mockCommandSender = creator.getCommandSender();
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void test() throws Exception {
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Import the first world
        String[] normalArgs = new String[] { "import", "world", "normal" };
        core.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(mockCommandSender).sendMessage("Starting import of world 'world'...");
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Complete!");

        assertEquals(core.getServer().getWorlds().size(), 1);
        assertEquals(core.getServer().getWorlds().get(0).getName(), "world");
        assertEquals(core.getServer().getWorlds().get(0).getEnvironment(), World.Environment.NORMAL);
        assertEquals(core.getServer().getWorlds().get(0).getWorldType(), WorldType.NORMAL);


        // Import a second world
        String[] netherArgs = new String[] { "import", "world_nether", "nether" };
        core.onCommand(mockCommandSender, mockCommand, "", netherArgs);
        verify(mockCommandSender).sendMessage("Starting import of world 'world_nether'...");
        verify(mockCommandSender, VerificationModeFactory.times(2)).sendMessage(
                ChatColor.GREEN + "Complete!");

        assertEquals(core.getServer().getWorlds().size(), 2);
        assertEquals(core.getServer().getWorlds().get(0).getName(), "world");
        assertEquals(core.getServer().getWorlds().get(1).getName(), "world_nether");
        assertEquals(core.getServer().getWorlds().get(1).getEnvironment(), World.Environment.NETHER);
        assertEquals(core.getServer().getWorlds().get(1).getWorldType(), WorldType.NORMAL);

        // ////////////////////////////////////////////////
        // let's set some world-properties
        // we can test the API with this, too :D
        MVWorldManager worldManager = core.getMVWorldManager();
        assertNotNull(worldManager);

        MultiverseWorld mvWorld = worldManager.getMVWorld("world");
        MultiverseWorld netherWorld = worldManager.getMVWorld("world_nether");
        assertNotNull(mvWorld);
        assertNotNull(netherWorld);
        assertSame(mvWorld, worldManager.getFirstSpawnWorld());
        assertSame(mvWorld, worldManager.getSpawnWorld());

        /* ***************************** *
         *        Check defaults
         * ***************************** */
        assertFalse(mvWorld.isHidden());
        assertEquals(mvWorld.getName(), mvWorld.getAlias());
        assertEquals(ChatColor.WHITE, mvWorld.getColor());
        assertTrue(mvWorld.isPVPEnabled());
        assertEquals(1D, mvWorld.getScaling(), 0);
        assertNull(mvWorld.getRespawnToWorld());
        assertTrue(mvWorld.isWeatherEnabled());
        assertEquals(Difficulty.NORMAL, mvWorld.getDifficulty());
        assertTrue(mvWorld.canAnimalsSpawn());
        assertTrue(mvWorld.canMonstersSpawn());
        assertNull(mvWorld.getCurrency());
        assertEquals(0, mvWorld.getPrice(), 0);
        assertTrue(mvWorld.getHunger());
        assertTrue(mvWorld.getAutoHeal());
        assertTrue(mvWorld.getAdjustSpawn());
        assertEquals(GameMode.SURVIVAL, mvWorld.getGameMode());
        assertTrue(mvWorld.isKeepingSpawnInMemory());
        assertTrue(mvWorld.getBedRespawn());
        assertTrue(mvWorld.getAutoLoad());
        assertEquals(new SpawnLocation(0, 64, 0), mvWorld.getSpawnLocation());

        /* ****************************************** *
         *    Call some events and verify behavior
         * ****************************************** */
        createEvents(mvWorld);

        // call both weather change events
        core.getWeatherListener().weatherChange(weatherChangeOffEvent);
        assertFalse(weatherChangeOffEvent.isCancelled());
        core.getWeatherListener().weatherChange(weatherChangeOnEvent);
        assertFalse(weatherChangeOnEvent.isCancelled());

        // call both thunder change events
        core.getWeatherListener().thunderChange(thunderChangeOffEvent);
        assertFalse(thunderChangeOffEvent.isCancelled());
        core.getWeatherListener().thunderChange(thunderChangeOnEvent);
        assertFalse(thunderChangeOnEvent.isCancelled());

        // call player chat event
        core.getMVConfig().setPrefixChat(true);
        ((MVAsyncPlayerChatListener) core.getChatListener()).playerChat(playerChatEvent);
        verify(playerChatEvent).setFormat("[" + mvWorld.getColoredWorldString() + "]" + "format");
        core.getMVConfig().setPrefixChat(false);
        ((MVAsyncPlayerChatListener) core.getChatListener()).playerChat(playerChatEvent);
        verify(playerChatEvent, times(1)).setFormat(anyString()); // only ONE TIME (not the 2nd time!)

        // call player join events
        core.getPlayerListener().playerJoin(playerJoinEvent);
        verify(mockPlayer, never()).teleport(any(Location.class));
        core.getPlayerListener().playerJoin(playerNewJoinEvent);
        verify(mockNewPlayer).teleport(worldManager.getFirstSpawnWorld().getSpawnLocation());

        // call player respawn events
        core.getPlayerListener().playerRespawn(playerRespawnBed);
        // bedrespawn is on so nothing should happen
        verify(playerRespawnBed, never()).setRespawnLocation(any(Location.class));
        core.getPlayerListener().playerRespawn(playerRespawnNormal);
        verify(playerRespawnNormal).setRespawnLocation(mvWorld.getSpawnLocation());

        // call entity regain health event
        core.getEntityListener().entityRegainHealth(entityRegainHealthEvent);
        // autoheal is on so nothing should happen
        verify(entityRegainHealthEvent, never()).setCancelled(true);


        /* ************************ *
         *     Modify & Verify
         * ************************ */
        mvWorld.setHidden(true);
        assertEquals(true, mvWorld.isHidden());
        mvWorld.setAlias("alias");
        assertEquals("alias", mvWorld.getAlias());
        assertTrue(mvWorld.setColor("BLACK"));
        assertFalse(mvWorld.setColor("INVALID COLOR"));
        assertEquals(ChatColor.BLACK, mvWorld.getColor());
        assertEquals(ChatColor.BLACK.toString() + "alias" + ChatColor.WHITE.toString(), mvWorld.getColoredWorldString());
        mvWorld.setPVPMode(false);
        assertEquals(false, mvWorld.isPVPEnabled());
        assertTrue(mvWorld.setScaling(2D));
        assertEquals(2D, mvWorld.getScaling(), 0);
        assertFalse(mvWorld.setRespawnToWorld("INVALID WORLD"));
        assertTrue(mvWorld.setRespawnToWorld("world_nether"));
        assertSame(worldManager.getMVWorld("world_nether").getCBWorld(),
                mvWorld.getRespawnToWorld());
        mvWorld.setEnableWeather(false);
        assertEquals(false, mvWorld.isWeatherEnabled());
        assertTrue(mvWorld.setDifficulty(Difficulty.PEACEFUL));
        assertEquals(Difficulty.PEACEFUL, mvWorld.getDifficulty());
        mvWorld.setAllowAnimalSpawn(false);
        assertEquals(false, mvWorld.canAnimalsSpawn());
        mvWorld.setAllowMonsterSpawn(false);
        assertEquals(false, mvWorld.canMonstersSpawn());
        mvWorld.setCurrency(Material.STONE);
        assertEquals(Material.STONE, mvWorld.getCurrency());
        mvWorld.setPrice(1D);
        assertEquals(1D, mvWorld.getPrice(), 0);
        mvWorld.setHunger(false);
        assertEquals(false, mvWorld.getHunger());
        mvWorld.setAutoHeal(false);
        assertEquals(false, mvWorld.getAutoHeal());
        mvWorld.setAdjustSpawn(false);
        assertEquals(false, mvWorld.getAdjustSpawn());
        assertTrue(mvWorld.setGameMode(GameMode.CREATIVE));
        assertEquals(GameMode.CREATIVE, mvWorld.getGameMode());
        mvWorld.setKeepSpawnInMemory(false);
        assertEquals(false, mvWorld.isKeepingSpawnInMemory());
        mvWorld.setBedRespawn(false);
        assertEquals(false, mvWorld.getBedRespawn());
        mvWorld.setAutoLoad(false);
        assertEquals(false, mvWorld.getAutoLoad());
        mvWorld.setSpawnLocation(new Location(mvWorld.getCBWorld(), 1, 1, 1));
        assertEquals(new SpawnLocation(1, 1, 1), mvWorld.getSpawnLocation());


        /* ****************************************** *
         *    Call some events and verify behavior
         * ****************************************** */
        // We have to recreate the events and the mock-objects
        createEvents(mvWorld);

        // call both weather change events
        core.getWeatherListener().weatherChange(weatherChangeOffEvent);
        assertFalse(weatherChangeOffEvent.isCancelled());
        core.getWeatherListener().weatherChange(weatherChangeOnEvent);
        assertTrue(weatherChangeOnEvent.isCancelled());

        // call both thunder change events
        core.getWeatherListener().thunderChange(thunderChangeOffEvent);
        assertFalse(thunderChangeOffEvent.isCancelled());
        core.getWeatherListener().thunderChange(thunderChangeOnEvent);
        assertTrue(thunderChangeOnEvent.isCancelled());

        // call player chat event
        core.getMVConfig().setPrefixChat(true);
        ((MVAsyncPlayerChatListener) core.getChatListener()).playerChat(playerChatEvent);
        // never because it's hidden!
        verify(playerChatEvent, never()).setFormat(
                "[" + mvWorld.getColoredWorldString() + "]" + "format");
        mvWorld.setHidden(false);
        ((MVAsyncPlayerChatListener) core.getChatListener()).playerChat(playerChatEvent);
        verify(playerChatEvent).setFormat("[" + mvWorld.getColoredWorldString() + "]" + "format");
        core.getMVConfig().setPrefixChat(false);
        ((MVAsyncPlayerChatListener) core.getChatListener()).playerChat(playerChatEvent);
        verify(playerChatEvent, times(1)).setFormat(anyString()); // only ONE TIME (not the 2nd time!)
        mvWorld.setHidden(true); // reset hidden-state

        // call player join events
        core.getPlayerListener().playerJoin(playerJoinEvent);
        verify(mockPlayer, never()).teleport(any(Location.class));
        core.getPlayerListener().playerJoin(playerNewJoinEvent);
        verify(mockNewPlayer).teleport(new SpawnLocation(1, 1, 1));

        // call player respawn events
        core.getPlayerListener().playerRespawn(playerRespawnBed);
        // bedrespawn is off so something should happen (and we've set respawn to nether...)
        verify(playerRespawnBed).setRespawnLocation(netherWorld.getSpawnLocation());
        core.getPlayerListener().playerRespawn(playerRespawnNormal);
        verify(playerRespawnNormal).setRespawnLocation(netherWorld.getSpawnLocation());

        // call entity regain health event
        core.getEntityListener().entityRegainHealth(entityRegainHealthEvent);
        // autoheal is off so something should happen
        verify(entityRegainHealthEvent).setCancelled(true);


        /* ****************************************** *
         *           Test saving/loading
         * ****************************************** */
        assertTrue(core.saveMVConfigs());
        // change a value here
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(core.getDataFolder(), "worlds.yml"));
        WorldProperties worldObj = (WorldProperties) config.get("worlds.world");
        assertTrue(worldObj.setColor("GREEN"));
        config.set("worlds.world", worldObj);
        config.save(new File(core.getDataFolder(), "worlds.yml"));
        // load
        core.loadConfigs();

        mvWorld = worldManager.getMVWorld("world");
        assertEquals(true, mvWorld.isHidden());
        assertEquals("alias", mvWorld.getAlias());
        assertEquals(ChatColor.GREEN, mvWorld.getColor());
        assertEquals(ChatColor.GREEN.toString() + "alias" + ChatColor.WHITE.toString(), mvWorld.getColoredWorldString());
        assertEquals(false, mvWorld.isPVPEnabled());
        assertEquals(2D, mvWorld.getScaling(), 0);
        assertSame(worldManager.getMVWorld("world_nether").getCBWorld(),
                mvWorld.getRespawnToWorld());
        assertEquals(false, mvWorld.isWeatherEnabled());
        assertEquals(Difficulty.PEACEFUL, mvWorld.getDifficulty());
        assertEquals(false, mvWorld.canAnimalsSpawn());
        assertEquals(false, mvWorld.canMonstersSpawn());
        assertEquals(Material.STONE, mvWorld.getCurrency());
        assertEquals(1D, mvWorld.getPrice(), 0);
        assertEquals(false, mvWorld.getHunger());
        assertEquals(false, mvWorld.getAutoHeal());
        assertEquals(false, mvWorld.getAdjustSpawn());
        assertEquals(GameMode.CREATIVE, mvWorld.getGameMode());
        assertEquals(false, mvWorld.isKeepingSpawnInMemory());
        assertEquals(false, mvWorld.getBedRespawn());
        assertEquals(false, mvWorld.getAutoLoad());
        assertEquals(new SpawnLocation(1, 1, 1), mvWorld.getSpawnLocation());
    }

    public void createEvents(MultiverseWorld mvWorld) {
        final World world = mvWorld.getCBWorld();
        //// Weather events
        // weather change
        weatherChangeOffEvent = new WeatherChangeEvent(world, false);
        weatherChangeOnEvent = new WeatherChangeEvent(world, true);
        // thunder change
        thunderChangeOffEvent = new ThunderChangeEvent(world, false);
        thunderChangeOnEvent = new ThunderChangeEvent(world, true);
        //// Player events
        // player chat
        mockPlayer = mock(Player.class);
        when(mockPlayer.getWorld()).thenReturn(world);
        when(mockPlayer.hasPlayedBefore()).thenReturn(true);
        when(mockPlayer.hasPermission("multiverse.access.world")).thenReturn(true);
        when(mockPlayer.getName()).thenReturn("MultiverseMan");
        playerChatEvent = PowerMockito.mock(AsyncPlayerChatEvent.class);
        when(playerChatEvent.getPlayer()).thenReturn(mockPlayer);
        when(playerChatEvent.getFormat()).thenReturn("format");
        // player join
        mockNewPlayer = mock(Player.class);
        when(mockNewPlayer.hasPlayedBefore()).thenReturn(false);
        playerJoinEvent = PowerMockito.mock(PlayerJoinEvent.class);
        when(playerJoinEvent.getPlayer()).thenReturn(mockPlayer);
        playerNewJoinEvent = PowerMockito.mock(PlayerJoinEvent.class);
        when(playerNewJoinEvent.getPlayer()).thenReturn(mockNewPlayer);
        // player respawn
        playerRespawnBed = PowerMockito.mock(PlayerRespawnEvent.class);
        when(playerRespawnBed.getPlayer()).thenReturn(mockPlayer);
        when(playerRespawnBed.isBedSpawn()).thenReturn(true);
        playerRespawnNormal = PowerMockito.mock(PlayerRespawnEvent.class);
        when(playerRespawnNormal.getPlayer()).thenReturn(mockPlayer);
        when(playerRespawnNormal.isBedSpawn()).thenReturn(false);
        //// Entity events
        mockHumanEntity = mock(HumanEntity.class);
        // entity regain health
        entityRegainHealthEvent = PowerMockito.mock(EntityRegainHealthEvent.class);
        when(entityRegainHealthEvent.getRegainReason()).thenReturn(RegainReason.REGEN);
        when(mockHumanEntity.getLocation()).thenReturn(new Location(world, 0, 0, 0));
        when(entityRegainHealthEvent.getEntity()).thenReturn(mockHumanEntity);
        // entity food level change event
        entityFoodLevelChangeEvent = PowerMockito.mock(FoodLevelChangeEvent.class);
        // this won't do anything since we're not mocking a player,
        // but the plugin should be able to handle this!
        when(entityFoodLevelChangeEvent.getEntity()).thenReturn(mockHumanEntity);
        entityFoodLevelRiseEvent = PowerMockito.mock(FoodLevelChangeEvent.class);
        when(mockPlayer.getFoodLevel()).thenReturn(2);
        when(entityFoodLevelRiseEvent.getEntity()).thenReturn(mockPlayer);
        when(entityFoodLevelRiseEvent.getFoodLevel()).thenReturn(3);
    }
}
