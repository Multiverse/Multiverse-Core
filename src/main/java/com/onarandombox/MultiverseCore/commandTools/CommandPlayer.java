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
