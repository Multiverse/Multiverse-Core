package com.onarandombox.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;


import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class PurgeWorlds {

    MultiverseCore plugin;

    public PurgeWorlds(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Synchronizes the given world with it's settings
     */
    public void purgeWorlds(CommandSender sender, List<MVWorld> worlds) {
        if (worlds == null || worlds.isEmpty()) {
            return;
        }
        for (MVWorld world : worlds) {
            this.purgeWorld(sender, world);
        }
    }

    /**
     * Convince method for clearing all the animals that do not belong according to the config.
     *
     * @param sender
     * @param world
     */
    public void purgeWorld(CommandSender sender, MVWorld world) {
        if (world == null) {
            return;
        }
        ArrayList<String> allMobs = new ArrayList<String>(world.getAnimalList());
        allMobs.addAll(world.getMonsterList());
        purgeWorld(sender, world, allMobs, !world.allowAnimalSpawning(), !world.allowMonsterSpawning());
    }

    public void purgeWorld(CommandSender sender, MVWorld mvworld, List<String> thingsToKill, boolean negateAnimals, boolean negateMonsters) {
        if (mvworld == null) {
            return;
        }
        World world = this.plugin.getServer().getWorld(mvworld.getName());
        if (world == null) {
            return;
        }
        int entitiesKilled = 0;
        for (Entity e : world.getEntities()) {

            // Check against Monsters
            if (killMonster(mvworld, e, thingsToKill, negateMonsters)) {
                entitiesKilled++;
                continue;
            }
            // Check against Animals
            if (this.killCreature(mvworld, e, thingsToKill, negateAnimals)) {
                entitiesKilled++;
                continue;
            }

        }
        if (sender != null) {
            sender.sendMessage(entitiesKilled + " entities purged from the world '" + world.getName() + "'");
        }
    }

    private boolean killCreature(MVWorld mvworld, Entity e, List<String> creaturesToKill, boolean negate) {
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
     * Will kill the monster if it's in the list UNLESS the NEGATE boolean is set, then it will kill it if it's NOT
     *
     * @param mvworld
     * @param e
     * @param creaturesToKill
     * @param negate
     * @return
     */
    private boolean killMonster(MVWorld mvworld, Entity e, List<String> creaturesToKill, boolean negate) {
        String entityName = e.toString().replaceAll("Craft", "").toUpperCase();
        if (e instanceof Slime || e instanceof Monster || e instanceof Ghast) {
            if (creaturesToKill.contains(entityName) || creaturesToKill.contains("ALL") || creaturesToKill.contains("MONSTERS")) {
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

}
