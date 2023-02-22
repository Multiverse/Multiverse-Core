package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import org.bukkit.Server;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        Command cmd = mock(Command.class);
        when(cmd.getName()).thenReturn("mv");

        MVWorld world = core.getMVWorldManager().getMVWorld("world");
        assertNotNull(world);

        assertFalse(world.isHidden()); // ensure it's not hidden now
        assertTrue(core.onCommand(mockCommandSender, cmd, "", // run the command
                new String[] { "modify", "set", "hidden", "true", "world" }));
        assertTrue(world.isHidden()); // test if it worked
    }
}
