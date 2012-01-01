/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.event;

import org.bukkit.event.Event;

/**
 * Called when somebody requests version information about Multiverse.
 * @deprecated Use {@link MVVersionEvent} instead.
 */
@Deprecated
public class MVVersionRequestEvent extends Event {

    private String pasteBinBuffer;

    public MVVersionRequestEvent(String pasteBinBuffer) {
        super("MVVersion");
        this.pasteBinBuffer = pasteBinBuffer;
    }

    /**
     * Gets the pasteBinBuffer.
     * @return The pasteBinBuffer.
     * @deprecated Use {@link MVVersionEvent} instead.
     */
    @Deprecated
    public String getPasteBinBuffer() {
        return this.pasteBinBuffer;
    }

    /**
     * Sets the pasteBinBuffer.
     * @param buffer The new pasteBinBuffer.
     * @deprecated Use {@link MVVersionEvent} instead.
     */
    @Deprecated
    public void setPasteBinBuffer(String buffer) {
        this.pasteBinBuffer = buffer;
    }
}
