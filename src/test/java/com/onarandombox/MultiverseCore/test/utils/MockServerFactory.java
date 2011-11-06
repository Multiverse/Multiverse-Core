/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test.utils;

import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class MockServerFactory {
    public Server getMockServer() {
        Server server = mock(Server.class);
        when(server.getName()).thenReturn("FernCraft");
        Logger logger = Logger.getLogger("Multiverse-Core-Test");
        when(server.getLogger()).thenReturn(logger);

        return server;
    }
}
