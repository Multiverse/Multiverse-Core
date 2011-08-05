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
