/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

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
