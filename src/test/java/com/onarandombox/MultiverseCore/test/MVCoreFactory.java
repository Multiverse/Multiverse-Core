/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.plugin.PluginDescriptionFile;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;

import static org.mockito.Mockito.doReturn;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class MVCoreFactory {
    public static final File pluginDirectory = new File("bin/test/server/plugins/coretest");
    public static final File serverDirectory = new File("bin/test/server");

    public MultiverseCore getNewCore() {

        MultiverseCore core = PowerMockito.spy(new MultiverseCore());

        // Let's let all MV files go to bin/test

        doReturn(pluginDirectory).when(core).getDataFolder();

        // Return a fake PDF file.
        PluginDescriptionFile pdf = new PluginDescriptionFile("Multiverse-Core", "2.1-Test", "com.onarandombox.MultiverseCore.MultiverseCore");
        doReturn(pdf).when(core).getDescription();
        doReturn(true).when(core).isEnabled();
        return core;
    }
}
