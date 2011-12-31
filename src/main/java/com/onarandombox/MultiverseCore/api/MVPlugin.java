/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.api;

import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * This interface is implemented by every official Multiverse-plugin.
 */
public interface MVPlugin extends LoggablePlugin {
    /**
     * Adds This plugin's version information to the buffer and returns the new string.
     *
     * @param buffer The string that contains Core and all other MV plugins' versions.
     *
     * @return A modified buffer that contains this MVPlugin's version information.
     */
    String dumpVersionInfo(String buffer);

    /**
     * Gets the reference to MultiverseCore.
     *
     * @return A valid {@link MultiverseCore}.
     */
    MultiverseCore getCore();

    /**
     * Sets the reference to MultiverseCore.
     *
     * @param core A valid {@link MultiverseCore}.
     */
    void setCore(MultiverseCore core);

    /**
     * Allows Multiverse or a plugin to query another Multiverse plugin to see what version its protocol is. This
     * number
     * should change when something will break the code.
     *
     * @return The Integer protocol version.
     */
    int getProtocolVersion();
}
