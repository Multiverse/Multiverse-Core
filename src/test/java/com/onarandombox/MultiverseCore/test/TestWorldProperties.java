package com.onarandombox.MultiverseCore.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.test.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.utils.WorldManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, MultiverseCore.class, Permission.class, Bukkit.class,
        WeatherChangeEvent.class, ThunderChangeEvent.class, PlayerChatEvent.class,
        PlayerJoinEvent.class, WorldManager.class })
public class TestWorldProperties {

    private TestInstanceCreator creator;
    private Server mockServer;
    private MultiverseCore core;
    private CommandSender mockCommandSender;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        mockServer = creator.getServer();
        core = creator.getCore();
        mockCommandSender = creator.getCommandSender();
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void test() {
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");

        // Import the first world
        String[] normalArgs = new String[] { "import", "world", "normal" };
        core.onCommand(mockCommandSender, mockCommand, "", normalArgs);
        verify(mockCommandSender).sendMessage("Starting import of world 'world'...");
        verify(mockCommandSender).sendMessage(ChatColor.GREEN + "Complete!");

        // Import a second world
        String[] netherArgs = new String[] { "import", "world_nether", "nether" };
        core.onCommand(mockCommandSender, mockCommand, "", netherArgs);
        verify(mockCommandSender).sendMessage("Starting import of world 'world_nether'...");
        verify(mockCommandSender, VerificationModeFactory.times(2)).sendMessage(ChatColor.GREEN + "Complete!");

        // ////////////////////////////////////////////////
        // let's set some world-properties
        // we can test the API with this, too :D
        MVWorldManager worldManager = core.getMVWorldManager();
        assertNotNull(worldManager);

        MultiverseWorld mvWorld = worldManager.getMVWorld("world");
        assertNotNull(mvWorld);
        assertSame(mvWorld, worldManager.getFirstSpawnWorld());
        assertSame(mvWorld, worldManager.getSpawnWorld());

        /* ****************************************** *
         *              Check defaults
         * ****************************************** */
        assertFalse(mvWorld.isHidden());
        assertEquals(mvWorld.getName(), mvWorld.getAlias());
        assertEquals(ChatColor.WHITE, mvWorld.getColor());
        assertTrue(mvWorld.isPVPEnabled());
        assertEquals((Object) 1D, (Object) mvWorld.getScaling()); // we're casting this to objects to use
        // assertEquals(Object,Object) instead of assertEquals(double,double)
        assertNull(mvWorld.getRespawnToWorld());
        assertTrue(mvWorld.isWeatherEnabled());
        // assertEquals(Difficulty.EASY, mvWorld.getDifficulty());
        assertTrue(mvWorld.canAnimalsSpawn());
        assertTrue(mvWorld.canMonstersSpawn());
        assertEquals(-1, mvWorld.getCurrency());
        assertEquals((Object) 0D, (Object) mvWorld.getPrice());
        assertTrue(mvWorld.getHunger());
        assertTrue(mvWorld.getAutoHeal());
        assertTrue(mvWorld.getAdjustSpawn());
        assertEquals(GameMode.SURVIVAL, mvWorld.getGameMode());
        assertTrue(mvWorld.isKeepingSpawnInMemory());
        assertTrue(mvWorld.getBedRespawn());
        assertTrue(mvWorld.getAutoLoad());
        assertEquals(new Location(mvWorld.getCBWorld(), 0, 0, 0), mvWorld.getSpawnLocation());

        /* ****************************************** *
         *    Call some events and verify behavior
         * ****************************************** */
        // weather change
        WeatherChangeEvent weatherChangeOffEvent = new WeatherChangeEvent(mvWorld.getCBWorld(), false);
        WeatherChangeEvent weatherChangeOnEvent = new WeatherChangeEvent(mvWorld.getCBWorld(), true);
        // thunder change
        ThunderChangeEvent thunderChangeOffEvent = new ThunderChangeEvent(mvWorld.getCBWorld(), false);
        ThunderChangeEvent thunderChangeOnEvent = new ThunderChangeEvent(mvWorld.getCBWorld(), true);
        // player chat
        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getWorld()).thenReturn(mvWorld.getCBWorld());
        when(mockPlayer.hasPlayedBefore()).thenReturn(true);
        PlayerChatEvent playerChatEvent = PowerMockito.mock(PlayerChatEvent.class);
        PowerMockito.when(playerChatEvent.getPlayer()).thenReturn(mockPlayer);
        PowerMockito.when(playerChatEvent.getFormat()).thenReturn("format");
        // player join
        Player mockNewPlayer = mock(Player.class);
        when(mockNewPlayer.hasPlayedBefore()).thenReturn(false);
        PlayerJoinEvent playerJoinEvent = PowerMockito.mock(PlayerJoinEvent.class);
        when(playerJoinEvent.getPlayer()).thenReturn(mockPlayer);
        PlayerJoinEvent playerNewJoinEvent = PowerMockito.mock(PlayerJoinEvent.class);
        when(playerNewJoinEvent.getPlayer()).thenReturn(mockNewPlayer);

        // call both weather change events
        core.getWeatherListener().onWeatherChange(weatherChangeOffEvent);
        assertFalse(weatherChangeOffEvent.isCancelled());
        core.getWeatherListener().onWeatherChange(weatherChangeOnEvent);
        assertFalse(weatherChangeOnEvent.isCancelled());

        // call both thunder change events
        core.getWeatherListener().onThunderChange(thunderChangeOffEvent);
        assertFalse(thunderChangeOffEvent.isCancelled());
        core.getWeatherListener().onThunderChange(thunderChangeOnEvent);
        assertFalse(thunderChangeOnEvent.isCancelled());

        // call player chat event
        core.getPlayerListener().onPlayerChat(playerChatEvent);
        verify(playerChatEvent).setFormat("[" + mvWorld.getColoredWorldString() + "]" + "format");

        // call player join events
        core.getPlayerListener().onPlayerJoin(playerJoinEvent);
        verify(mockPlayer, never()).teleport(any(Location.class));
        core.getPlayerListener().onPlayerJoin(playerNewJoinEvent);
        verify(mockNewPlayer).teleport(worldManager.getFirstSpawnWorld().getSpawnLocation());

        /* ****************************************** *
         *             Modify & Verify
         * ****************************************** */
        mvWorld.setHidden(true);
        assertEquals(true, mvWorld.isHidden());
        mvWorld.setAlias("alias");
        assertEquals("alias", mvWorld.getAlias());
        assertTrue(mvWorld.setColor("BLACK"));
        ChatColor oldColor = mvWorld.getColor();
        assertFalse(mvWorld.setColor("INVALID COLOR"));
        assertEquals(oldColor, mvWorld.getColor());
        mvWorld.setPVPMode(false);
        assertEquals(false, mvWorld.isPVPEnabled());
        assertTrue(mvWorld.setScaling(2D));
        assertEquals((Object) 2D, (Object) mvWorld.getScaling());
        assertFalse(mvWorld.setRespawnToWorld("INVALID WORLD"));
        assertTrue(mvWorld.setRespawnToWorld("world_nether"));
        assertSame(worldManager.getMVWorld("world_nether").getCBWorld(), mvWorld.getRespawnToWorld());
        mvWorld.setEnableWeather(false);
        assertEquals(false, mvWorld.isWeatherEnabled());
        assertTrue(mvWorld.setDifficulty("PEACEFUL"));
        Difficulty oldDifficulty = mvWorld.getDifficulty();
        assertFalse(mvWorld.setDifficulty("INVALID DIFFICULTY"));
        assertEquals(oldDifficulty, mvWorld.getDifficulty());
        mvWorld.setAllowAnimalSpawn(false);
        assertEquals(false, mvWorld.canAnimalsSpawn());
        mvWorld.setAllowMonsterSpawn(false);
        assertEquals(false, mvWorld.canMonstersSpawn());
        mvWorld.setCurrency(1);
        assertEquals(1, mvWorld.getCurrency());
        mvWorld.setPrice(1D);
        assertEquals((Object) 1D, (Object) mvWorld.getPrice());
        mvWorld.setHunger(false);
        assertEquals(false, mvWorld.getHunger());
        mvWorld.setAutoHeal(false);
        assertEquals(false, mvWorld.getAutoHeal());
        mvWorld.setAdjustSpawn(false);
        assertEquals(false, mvWorld.getAdjustSpawn());
        assertTrue(mvWorld.setGameMode("CREATIVE"));
        GameMode oldGamemode = mvWorld.getGameMode();
        assertFalse(mvWorld.setGameMode("INVALID GAMEMODE"));
        assertEquals(oldGamemode, mvWorld.getGameMode());
        mvWorld.setKeepSpawnInMemory(false);
        assertEquals(false, mvWorld.isKeepingSpawnInMemory());
        mvWorld.setBedRespawn(false);
        assertEquals(false, mvWorld.getBedRespawn());
        mvWorld.setAutoLoad(false);
        assertEquals(false, mvWorld.getAutoLoad());
        mvWorld.setSpawnLocation(new Location(mvWorld.getCBWorld(), 1, 1, 1));
        assertEquals(new Location(mvWorld.getCBWorld(), 1, 1, 1), mvWorld.getSpawnLocation());

        /* ****************************************** *
         *    Call some events and verify behavior
         * ****************************************** */
        // call both weather change events
        core.getWeatherListener().onWeatherChange(weatherChangeOffEvent);
        assertFalse(weatherChangeOffEvent.isCancelled());
        core.getWeatherListener().onWeatherChange(weatherChangeOnEvent);
        assertTrue(weatherChangeOnEvent.isCancelled());

        // call both thunder change events
        core.getWeatherListener().onThunderChange(thunderChangeOffEvent);
        assertFalse(thunderChangeOffEvent.isCancelled());
        core.getWeatherListener().onThunderChange(thunderChangeOnEvent);
        assertTrue(thunderChangeOnEvent.isCancelled());
    }

}
