package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import org.bukkit.Server;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ MultiverseCore.class, PluginDescriptionFile.class, JavaPluginLoader.class })
@PowerMockIgnore("javax.script.*")
public class TestModifyCommand {
    TestInstanceCreator creator;
    Server mockServer;
    MultiverseCore core;
    CommandSender mockCommandSender;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        mockServer = creator.getServer();
        mockCommandSender = creator.getCommandSender();
        core = creator.getCore();

        // create world
        assertTrue(core.getMVWorldManager().addWorld("world", Environment.NORMAL, null, null, null, null));
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void testSetHidden() {
        MultiverseWorld world = core.getMVWorldManager().getMVWorld("world");
        assertNotNull(world);

        // ensure it's not hidden now
        assertFalse(world.isHidden());

        // run the command
        assertTrue(creator.dispatch(mockCommandSender, "mv modify set hidden true world"));

        // test if it worked
        assertTrue(world.isHidden());
    }
}
