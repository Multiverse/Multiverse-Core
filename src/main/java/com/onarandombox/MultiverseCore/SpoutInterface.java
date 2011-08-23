package com.onarandombox.MultiverseCore;

import org.getspout.spoutapi.SpoutManager;

public class SpoutInterface {
    private SpoutManager spoutManager;
    public SpoutInterface() {
        this.spoutManager = SpoutManager.getInstance();
    }
    
    public SpoutManager getManager() {
        return this.spoutManager;
    }
}
