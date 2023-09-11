package org.mvplugins.multiverse.core.api;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jvnet.hk2.annotations.Contract;

/**
 * Used to remove animals from worlds that don't belong there.
 */
@Contract
public interface WorldPurger {
    /**
     * Synchronizes the given worlds with their settings.
     *
     * @param worlds A list of {@link MVWorld}
     */
    void purgeWorlds(List<MVWorld> worlds);

    /**
     * Convenience method for {@link #purgeWorld(MVWorld, java.util.List, boolean, boolean)} that takes the settings from the world-config.
     *
     * @param world The {@link MVWorld}.
     */
    void purgeWorld(MVWorld world);

    /**
     * Clear all animals/monsters that do not belong to a world according to the config.
     *
     * @param mvworld The {@link MVWorld}.
     * @param thingsToKill A {@link List} of animals/monsters to be killed.
     * @param negateAnimals Whether the monsters in the list should be negated.
     * @param negateMonsters Whether the animals in the list should be negated.
     */
    void purgeWorld(MVWorld mvworld, List<String> thingsToKill, boolean negateAnimals,
                    boolean negateMonsters);

    /**
     * Clear all animals/monsters that do not belong to a world according to the config.
     *
     * @param mvworld The {@link MVWorld}.
     * @param thingsToKill A {@link List} of animals/monsters to be killed.
     * @param negateAnimals Whether the monsters in the list should be negated.
     * @param negateMonsters Whether the animals in the list should be negated.
     * @param sender The {@link CommandSender} that initiated the action. He will/should be notified.
     */
    void purgeWorld(MVWorld mvworld, List<String> thingsToKill, boolean negateAnimals,
                    boolean negateMonsters, CommandSender sender);

    /**
     * Determines whether the specified creature should be killed.
     *
     * @param e The creature.
     * @param thingsToKill A {@link List} of animals/monsters to be killed.
     * @param negateAnimals Whether the monsters in the list should be negated.
     * @param negateMonsters Whether the animals in the list should be negated.
     * @return {@code true} if the creature should be killed, otherwise {@code false}.
     */
    boolean shouldWeKillThisCreature(Entity e, List<String> thingsToKill, boolean negateAnimals, boolean negateMonsters);

    /**
     * Determines whether the specified creature should be killed and automatically reads the params from a world object.
     *
     * @param w The world.
     * @param e The creature.
     * @return {@code true} if the creature should be killed, otherwise {@code false}.
     */
    boolean shouldWeKillThisCreature(MVWorld w, Entity e);
}
