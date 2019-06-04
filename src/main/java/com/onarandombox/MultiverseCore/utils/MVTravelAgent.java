/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import org.bukkit.entity.Player;

/**
 * The Multiverse TravelAgent.
 */
public class MVTravelAgent {
    protected MVDestination destination;
    protected MultiverseCore core;
    protected Player player;

    public MVTravelAgent(MultiverseCore multiverseCore, MVDestination d, Player p) {
        this.destination = d;
        this.core = multiverseCore;
        this.player = p;
    }
}
