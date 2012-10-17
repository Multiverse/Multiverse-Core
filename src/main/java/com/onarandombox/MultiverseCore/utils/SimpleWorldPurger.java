/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.WorldPurger;
import com.onarandombox.MultiverseCore.localization.MultiverseMessage;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Utility class that removes animals from worlds that don't belong there.
 */
public class SimpleWorldPurger implements WorldPurger {

    private MultiverseCore plugin;

    public SimpleWorldPurger(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purgeWorlds(List<MultiverseWorld> worlds) {
        if (worlds == null || worlds.isEmpty()) {
            return;
        }
        for (MultiverseWorld world : worlds) {
            this.purgeWorld(world);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purgeWorld(MultiverseWorld world) {
        if (world == null) {
            return;
        }
        ArrayList<String> allMobs = new ArrayList<String>(world.getAnimalList());
        allMobs.addAll(world.getMonsterList());
        purgeWorld(world, allMobs, !world.canAnimalsSpawn(), !world.canMonstersSpawn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldWeKillThisCreature(MultiverseWorld world, Entity e) {
        ArrayList<String> allMobs = new ArrayList<String>(world.getAnimalList());
        allMobs.addAll(world.getMonsterList());
        return this.shouldWeKillThisCreature(e, allMobs, !world.canAnimalsSpawn(), !world.canMonstersSpawn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purgeWorld(MultiverseWorld mvworld, List<String> thingsToKill,
            boolean negateAnimals, boolean negateMonsters, CommandSender sender) {
        if (mvworld == null) {
            return;
        }
        World world = mvworld.getCBWorld();
        if (world == null) {
            return;
        }
        int entitiesKilled = 0;
        boolean specifiedAll = thingsToKill.contains("ALL");
        boolean specifiedAnimals = thingsToKill.contains("ANIMALS") || specifiedAll;
        boolean specifiedMonsters = thingsToKill.contains("MONSTERS") || specifiedAll;
        for (Entity e : world.getEntities()) {
            if (killDecision(e, thingsToKill, negateAnimals, negateMonsters, specifiedAnimals, specifiedMonsters)) {
                e.remove();
                entitiesKilled++;
            }
        }
        if (sender != null)
            this.plugin.getMessaging().sendMessage(sender, MultiverseMessage.PURGER_ENTITIESKILLED, entitiesKilled, world.getName());
    }

    private boolean killDecision(Entity e, List<String> thingsToKill, boolean negateAnimals,
            boolean negateMonsters, boolean specifiedAnimals, boolean specifiedMonsters) {
        boolean negate = false;
        boolean specified = false;
        if (e instanceof Squid || e instanceof Animals) {
            // it's an animal
            if (specifiedAnimals && !negateAnimals) {
                this.plugin.log(Level.FINEST, "Removing an entity because I was told to remove all animals: " + e);
                return true;
            }
            if (specifiedAnimals)
                specified = true;
            negate = negateAnimals;
        } else if (e instanceof Monster || e instanceof Ghast || e instanceof Slime) {
            // it's a monster
            if (specifiedMonsters && !negateMonsters) {
                this.plugin.log(Level.FINEST, "Removing an entity because I was told to remove all monsters: " + e);
                return true;
            }
            if (specifiedMonsters)
                specified = true;
            negate = negateMonsters;
        }
        for (String s : thingsToKill) {
            EntityType type = EntityType.fromName(s);
            if (type != null && type.equals(e.getType())) {
                specified = true;
                if (!negate) {
                    this.plugin.log(Level.FINEST, "Removing an entity because it WAS specified and we are NOT negating: " + e);
                    return true;
                }
                break;
            }
        }
        if (!specified && negate) {
            this.plugin.log(Level.FINEST, "Removing an entity because it was NOT specified and we ARE negating: " + e);
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldWeKillThisCreature(Entity e, List<String> thingsToKill, boolean negateAnimals, boolean negateMonsters) {
        boolean specifiedAll = thingsToKill.contains("ALL");
        boolean specifiedAnimals = thingsToKill.contains("ANIMALS") || specifiedAll;
        boolean specifiedMonsters = thingsToKill.contains("MONSTERS") || specifiedAll;
        return this.killDecision(e, thingsToKill, negateAnimals, negateMonsters, specifiedAnimals, specifiedMonsters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purgeWorld(MultiverseWorld mvworld, List<String> thingsToKill, boolean negateAnimals, boolean negateMonsters) {
        purgeWorld(mvworld, thingsToKill, negateAnimals, negateMonsters, null);
    }
}
