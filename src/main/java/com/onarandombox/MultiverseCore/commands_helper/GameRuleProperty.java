package com.onarandombox.MultiverseCore.commands_helper;

import org.bukkit.GameRule;

public class GameRuleProperty<T> {

    private final GameRule<T> gameRule;
    private final T value;

    public GameRuleProperty(GameRule<T> gameRule, T value) {
        this.gameRule = gameRule;
        this.value = value;
    }

    public GameRule<T> getGameRule() {
        return gameRule;
    }

    public T getValue() {
        return value;
    }
}
