/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools.contexts;

import org.bukkit.GameRule;
import org.jetbrains.annotations.NotNull;

/**
 * Single key value pair for a game rule.
 *
 * @param <T> Type of GameRule property.
 */
public class GameRuleProperty<T> {

    private final GameRule<T> gameRule;
    private final T value;

    public GameRuleProperty(@NotNull GameRule<T> gameRule,
                            @NotNull T value) {

        this.gameRule = gameRule;
        this.value = value;
    }

    @NotNull
    public GameRule<T> getGameRule() {
        return gameRule;
    }

    @NotNull
    public T getValue() {
        return value;
    }
}
