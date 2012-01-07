/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.api;

import org.bukkit.Server;

import java.util.logging.Level;

/** A simple API to require plugins to have a log method. */
public interface LoggablePlugin {
    /**
     * Logs a message at the specified level.
     *
     * @param level The Log-{@link Level}.
     * @param msg   The message to log.
     */
    void log(Level level, String msg);

    /**
     * Gets the server instance that this plugin is attached to.
     *
     * @return A {@link Server} instance.
     */
    Server getServer();
}
