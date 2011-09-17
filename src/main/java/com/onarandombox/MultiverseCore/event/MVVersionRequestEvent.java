/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.event;

import org.bukkit.event.Event;

public class MVVersionRequestEvent extends Event {

    private String pasteBinBuffer;
    public MVVersionRequestEvent(String pasteBinBuffer) {
        super("MVVersion");
        this.pasteBinBuffer = pasteBinBuffer;
    }

    public String getPasteBinBuffer() {
        return this.pasteBinBuffer;
    }
    public void setPasteBinBuffer(String buffer) {
        this.pasteBinBuffer = buffer;
    }
}
