package com.onarandombox.MultiverseCore.commandtools.contexts;

import org.bukkit.entity.Player;

public class RequiredPlayer {

    private final Player player;

    public RequiredPlayer(Player player) {
        this.player = player;
    }

    public Player get() {
        return player;
    }
}
