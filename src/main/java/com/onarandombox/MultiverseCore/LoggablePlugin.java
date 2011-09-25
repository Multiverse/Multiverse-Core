/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import org.bukkit.Server;

import java.util.logging.Level;

public interface LoggablePlugin {
    public void log(Level level, String msg);

    public Server getServer();
}
