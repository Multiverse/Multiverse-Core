package com.onarandombox.MultiverseCore.api;

import java.util.List;

import org.bukkit.command.CommandSender;

/**
 * Used to remove animals from worlds that don't belong there.
 */
public interface WorldPurger {

    /**
     * Synchronizes the given world with it's settings.
     *
     * @param worlds A list of {@link MultiverseWorld}
     */
    void purgeWorlds(List<MultiverseWorld> worlds);

    /**
     * Convenience method for {@link #purgeWorld(CommandSender, MultiverseWorld, List, boolean, boolean)} that takes the settings from the world-config.
     *
     * @param world The {@link MultiverseWorld}.
     */
    void purgeWorld(MultiverseWorld world);

    /**
     * Clear all animals/monsters that do not belong to a world according to the config.
     *
     * @param mvworld The {@link MultiverseWorld}.
     * @param thingsToKill A {@link List} of animals/monsters to be killed.
     * @param negateAnimals Whether the monsters in the list should be negated.
     * @param negateMonsters Whether the animals in the list should be negated.
     */
    void purgeWorld(MultiverseWorld mvworld, List<String> thingsToKill, boolean negateAnimals,
            boolean negateMonsters);

    /**
     * Clear all animals/monsters that do not belong to a world according to the config.
     *
     * @param mvworld The {@link MultiverseWorld}.
     * @param thingsToKill A {@link List} of animals/monsters to be killed.
     * @param negateAnimals Whether the monsters in the list should be negated.
     * @param negateMonsters Whether the animals in the list should be negated.
     * @param sender The {@link CommandSender} that initiated the action. He will/should be notified.
     */
    void purgeWorld(MultiverseWorld mvworld, List<String> thingsToKill, boolean negateAnimals,
            boolean negateMonsters, CommandSender sender);

}
