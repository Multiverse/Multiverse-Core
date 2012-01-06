/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Locale;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.localization.LazyLocaleMessageProvider;
import com.onarandombox.MultiverseCore.localization.MultiverseMessage;
import com.onarandombox.MultiverseCore.localization.SimpleMessageProvider;
import com.onarandombox.MultiverseCore.test.utils.TestInstanceCreator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MultiverseCore.class })
public class TestLocalization {
    TestInstanceCreator creator;
    Server mockServer;
    CommandSender mockCommandSender;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        mockServer = creator.getServer();
        mockCommandSender = creator.getCommandSender();
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void testEnumDefaults() throws Exception {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Make sure Core is a MultiverseCore
        assertTrue(plugin instanceof MultiverseCore);

        MultiverseCore core = (MultiverseCore) plugin;

        // This should be the same core as creator.getCore()
        assertEquals(core, creator.getCore());

        // Make sure there is neither the file nor the resource
        assertNull(core.getResource("localization/en.yml"));
        assertFalse(new File(TestInstanceCreator.pluginDirectory, "en.yml").exists());

        // And now test it
        String actual = core.getMessageProvider().getMessage(MultiverseMessage.TEST_STRING);
        String expected = "a test-string from the enum";

        assertEquals(expected, actual);
    }

    @Test
    public void testResourceDefaults() throws Exception {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Make sure Core is a MultiverseCore
        assertTrue(plugin instanceof MultiverseCore);

        MultiverseCore core = (MultiverseCore) plugin;

        // This should be the same core as creator.getCore()
        assertEquals(core, creator.getCore());

        // Make sure there is no file, only the resource
        assertFalse(new File(TestInstanceCreator.pluginDirectory, "en.yml").exists());
        assertTrue(new File("src/main/resources/localization/en.yml").exists());
        doAnswer(new Answer<InputStream>() {
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                try {
                    return new FileInputStream("src/main/resources/" + (String) invocation.getArguments()[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).when(core).getResource(anyString());
        assertNotNull(core.getResource("localization/en.yml"));

        // this only works for a LazyLocaleMessageProvider
        assertTrue(core.getMessageProvider() instanceof LazyLocaleMessageProvider);

        LazyLocaleMessageProvider messageProvider = (LazyLocaleMessageProvider) core.getMessageProvider();

        // We have to reload it
        messageProvider.loadLocale(Locale.ENGLISH);

        // And now test it
        String actual = core.getMessageProvider().getMessage(MultiverseMessage.TEST_STRING);
        String expected = "a test-string from the resource";

        assertEquals(expected, actual);
    }

    @Test
    public void testUserFile() throws Exception {
        // Pull a core instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Multiverse-Core");

        // Make sure Core is not null
        assertNotNull(plugin);

        // Make sure Core is enabled
        assertTrue(plugin.isEnabled());

        // Make sure Core is a MultiverseCore
        assertTrue(plugin instanceof MultiverseCore);

        MultiverseCore core = (MultiverseCore) plugin;

        // This should be the same core as creator.getCore()
        assertEquals(core, creator.getCore());

        // Create the file
        File file = new File(TestInstanceCreator.pluginDirectory, "en.yml");
        BufferedWriter bwriter = new BufferedWriter(new FileWriter(file));
        String expected = "a test-string from the user-file";
        bwriter.write("TEST_STRING: " + expected);
        bwriter.close();

        // Make sure there is the file and the resource
        assertTrue(file.exists());
        assertTrue(new File("src/main/resources/localization/en.yml").exists());
        doAnswer(new Answer<InputStream>() {
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                try {
                    return new FileInputStream("src/main/resources/" + (String) invocation.getArguments()[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).when(core).getResource(anyString());
        assertNotNull(core.getResource("localization/en.yml"));

        // this only works for a LazyLocaleMessageProvider
        assertTrue(core.getMessageProvider() instanceof LazyLocaleMessageProvider);

        LazyLocaleMessageProvider messageProvider = (LazyLocaleMessageProvider) core.getMessageProvider();

        // We have to reload it
        messageProvider.loadLocale(Locale.ENGLISH);

        // And now test it
        String actual = core.getMessageProvider().getMessage(MultiverseMessage.TEST_STRING);

        assertEquals(expected, actual);

        // Clean up afterwards:
        assertTrue(file.delete());
    }

    @Test
    public void testFormat() {
        String testString = "%sthisisasimpletest&moretesting&&thatsit.";
        String result = SimpleMessageProvider.format(testString, "arg");
        assertEquals("argthisisasimpletest\u00A7moretesting&thatsit.", result);
    }
}
