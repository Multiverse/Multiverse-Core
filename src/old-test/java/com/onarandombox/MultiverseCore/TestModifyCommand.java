package com.onarandombox.MultiverseCore;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

        /* This block is preserved for the transition to MV5, just in case
        // create world
        assertTrue(core.getMVWorldManager().addWorld("world", Environment.NORMAL, null, null, null, null));
         */
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void testSetHidden() {
        Command cmd = mock(Command.class);
        when(cmd.getName()).thenReturn("mv");

        /* This block is preserved for the transition to MV5, just in case
        MVWorld world = core.getMVWorldManager().getMVWorld("world");
        assertNotNull(world);

        assertFalse(world.isHidden()); // ensure it's not hidden now
        assertTrue(core.onCommand(mockCommandSender, cmd, "", // run the command
                new String[] { "modify", "set", "hidden", "true", "world" }));
        assertTrue(world.isHidden()); // test if it worked
         */
    }
}
