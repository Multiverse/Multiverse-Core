/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PurgeWorlds {

    private MultiverseCore plugin;

    public PurgeWorlds(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Synchronizes the given world with it's settings.
     *
     * @param sender The {@link CommandSender} who is requesting the world be purged.
     * @param worlds A list of {@link MultiverseWorld}
     */
    public void purgeWorlds(CommandSender sender, List<MultiverseWorld> worlds) {
        if (worlds == null || worlds.isEmpty()) {
            return;
        }
        for (MultiverseWorld world : worlds) {
            this.purgeWorld(sender, world);
        }
    }

    /**
     * Convince method for clearing all the animals that do not belong according to the config.
     *
     * @param sender
     * @param world
     */
    public void purgeWorld(CommandSender sender, MultiverseWorld world) {
        if (world == null) {
            return;
        }
        ArrayList<String> allMobs = new ArrayList<String>(world.getAnimalList());
        allMobs.addAll(world.getMonsterList());
        purgeWorld(sender, world, allMobs, !world.canAnimalsSpawn(), !world.canMonstersSpawn());
    }

    public void purgeWorld(CommandSender sender, MultiverseWorld mvworld, List<String> thingsToKill, boolean negateAnimals, boolean negateMonsters) {
        if (mvworld == null) {
            return;
        }
        World world = this.plugin.getServer().getWorld(mvworld.getName());
        if (world == null) {
            return;
        }
        int entitiesKilled = 0;
        for (Entity e : world.getEntities()) {
            this.plugin.log(Level.FINEST, "Entity list (aval for purge) from WORLD < " + mvworld.getName() + " >: " + e.toString());

            // Check against Monsters
            if (killMonster(mvworld, e, thingsToKill, negateMonsters)) {
                entitiesKilled++;
                continue;
            }
            // Check against Animals
            if (this.killCreature(mvworld, e, thingsToKill, negateAnimals)) {
                entitiesKilled++;
            }

        }
        if (sender != null) {
            sender.sendMessage(entitiesKilled + " entities purged from the world '" + world.getName() + "'");
        }
    }

    private boolean killCreature(MultiverseWorld mvworld, Entity e, List<String> creaturesToKill, boolean negate) {
        String entityName = e.toString().replaceAll("Craft", "").toUpperCase();
        if (e instanceof Squid || e instanceof Animals) {
            if (creaturesToKill.contains(entityName) || creaturesToKill.contains("ALL") || creaturesToKill.contains("ANIMALS")) {
                if (!negate) {
                    e.remove();
                    return true;
                }
            } else {
                if (negate) {
                    e.remove();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Will kill the monster if it's in the list UNLESS the NEGATE boolean is set, then it will kill it if it's NOT.
     *
     * @param mvworld
     * @param e
     * @param creaturesToKill
     * @param negate
     * @return
     */
    private boolean killMonster(MultiverseWorld mvworld, Entity e, List<String> creaturesToKill, boolean negate) {
        String entityName = "";
        //TODO: Fixme once either Rigby puts his awesome thing in OR Enderdragon gets a toString, OR both.
        if (e instanceof EnderDragon) {
            entityName = "ENDERDRAGON";
        } else {
            entityName = e.toString().replaceAll("Craft", "").toUpperCase();
        }
        if (e instanceof Slime || e instanceof Monster || e instanceof Ghast || e instanceof EnderDragon) {
            this.plugin.log(Level.FINER, "Looking at a monster: " + e);
            if (creaturesToKill.contains(entityName) || creaturesToKill.contains("ALL") || creaturesToKill.contains("MONSTERS")) {
                if (!negate) {
                    this.plugin.log(Level.FINEST, "Removing a monster: " + e);
                    e.remove();
                    return true;
                }
            } else {
                if (negate) {
                    this.plugin.log(Level.FINEST, "Removing a monster: " + e);
                    e.remove();
                    return true;
                }
            }
        }
        return false;
    }

}
