/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import org.bukkit.entity.Player;

public class CommandPlayer {
    private final Player player;

    public CommandPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "CommandPlayer{" +
                "player=" + player +
                '}';
    }
}
