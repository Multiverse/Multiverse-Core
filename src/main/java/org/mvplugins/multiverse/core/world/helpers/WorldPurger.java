package org.mvplugins.multiverse.core.world.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Golem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Used to remove animals from worlds that don't belong there.
 *
 * @deprecated this class needs a refactor
 * @since 5.0
 */
@Deprecated
@Service
public final class WorldPurger {

    /**
     * Synchronizes the given worlds with their settings.
     *
     * @param worlds A list of {@link LoadedMultiverseWorld}
     */
    public void purgeWorlds(@Nullable List<LoadedMultiverseWorld> worlds) {
        if (worlds == null || worlds.isEmpty()) {
            return;
        }
        for (LoadedMultiverseWorld world : worlds) {
            this.purgeWorld(world);
        }
    }

    /**
     * Convenience method for {@link #purgeWorld(LoadedMultiverseWorld, java.util.List, boolean, boolean)} that takes
     * the settings from the world-config.
     *
     * @param world The {@link LoadedMultiverseWorld}.
     */
    public void purgeWorld(@Nullable LoadedMultiverseWorld world) {
        if (world == null) {
            return;
        }
        ArrayList<String> allMobs = new ArrayList<>(world.getSpawningAnimalsExceptions());
        allMobs.addAll(world.getSpawningMonstersExceptions());
        purgeWorld(world, allMobs, !world.getSpawningAnimals(), !world.getSpawningMonsters());
    }

    /**
     * Clear all animals/monsters that do not belong to a world according to the config.
     *
     * @param world The {@link LoadedMultiverseWorld}.
     * @param thingsToKill A {@link List} of animals/monsters to be killed.
     * @param negateAnimals Whether the monsters in the list should be negated.
     * @param negateMonsters Whether the animals in the list should be negated.
     */
    public void purgeWorld(
            LoadedMultiverseWorld world,
            List<String> thingsToKill,
            boolean negateAnimals,
            boolean negateMonsters) {
        purgeWorld(world, thingsToKill, negateAnimals, negateMonsters, null);
    }

    /**
     * Clear all animals/monsters that do not belong to a world according to the config.
     *
     * @param world           The {@link LoadedMultiverseWorld}.
     * @param thingsToKill      A {@link List} of animals/monsters to be killed.
     * @param negateAnimals     Whether the monsters in the list should be negated.
     * @param negateMonsters    Whether the animals in the list should be negated.
     * @param sender The {@link CommandSender} that initiated the action. He will/should be notified.
     */
    public void purgeWorld(
            @Nullable LoadedMultiverseWorld world,
            @NotNull List<String> thingsToKill,
            boolean negateAnimals,
            boolean negateMonsters,
            CommandSender sender) {
        if (world == null) {
            return;
        }
        World bukkitWorld = world.getBukkitWorld().getOrNull();
        if (bukkitWorld == null) {
            return;
        }
        int projectilesKilled = 0;
        int entitiesKilled = 0;
        boolean specifiedAll = thingsToKill.contains("ALL");
        boolean specifiedAnimals = thingsToKill.contains("ANIMALS") || specifiedAll;
        boolean specifiedMonsters = thingsToKill.contains("MONSTERS") || specifiedAll;
        List<Entity> worldEntities = bukkitWorld.getEntities();
        List<LivingEntity> livingEntities = new ArrayList<LivingEntity>(worldEntities.size());
        List<Projectile> projectiles = new ArrayList<Projectile>(worldEntities.size());
        for (final Entity e : worldEntities) {
            if (e instanceof Projectile p) {
                if (p.getShooter() != null) {
                    projectiles.add((Projectile) e);
                }
            } else if (e instanceof LivingEntity) {
                livingEntities.add((LivingEntity) e);
            }
        }
        for (final LivingEntity e : livingEntities) {
            if (killDecision(e, thingsToKill, negateAnimals, negateMonsters, specifiedAnimals, specifiedMonsters)) {
                final Iterator<Projectile> it = projectiles.iterator();
                while (it.hasNext()) {
                    final Projectile p = it.next();
                    if (Objects.equals(p.getShooter(), e)) {
                        p.remove();
                        it.remove();
                        projectilesKilled++;
                    }
                }
                e.remove();
                entitiesKilled++;
            }
        }
        if (sender != null) {
            sender.sendMessage(entitiesKilled + " entities purged from the world '" + world.getName() + "' along with "
                    + projectilesKilled + " projectiles that belonged to them.");
        }
    }

    /**
     * Determines whether the specified creature should be killed and automatically reads the params from a world object.
     *
     * @param world     The world.
     * @param entity    The creature.
     * @return {@code true} if the creature should be killed, otherwise {@code false}.
     */
    public boolean shouldWeKillThisCreature(@NotNull MultiverseWorld world, @NotNull Entity entity) {
        ArrayList<String> allMobs = new ArrayList<>(world.getSpawningAnimalsExceptions());
        allMobs.addAll(world.getSpawningMonstersExceptions());
        return this.shouldWeKillThisCreature(entity, allMobs, !world.getSpawningAnimals(), !world.getSpawningMonsters());
    }

    /**
     * Determines whether the specified creature should be killed.
     *
     * @param entity                 The creature.
     * @param thingsToKill      A {@link List} of animals/monsters to be killed.
     * @param negateAnimals     Whether the monsters in the list should be negated.
     * @param negateMonsters    Whether the animals in the list should be negated.
     * @return {@code true} if the creature should be killed, otherwise {@code false}.
     */
    public boolean shouldWeKillThisCreature(
            Entity entity,
            List<String> thingsToKill,
            boolean negateAnimals,
            boolean negateMonsters) {
        boolean specifiedAll = thingsToKill.contains("ALL");
        boolean specifiedAnimals = thingsToKill.contains("ANIMALS") || specifiedAll;
        boolean specifiedMonsters = thingsToKill.contains("MONSTERS") || specifiedAll;
        return this.killDecision(
                entity,
                thingsToKill,
                negateAnimals,
                negateMonsters,
                specifiedAnimals,
                specifiedMonsters);
    }

    private boolean killDecision(
            Entity entity,
            List<String> thingsToKill,
            boolean negateAnimals,
            boolean negateMonsters,
            boolean specifiedAnimals,
            boolean specifiedMonsters) {
        boolean negate = false;
        boolean specified = false;
        if (entity instanceof Golem
                || entity instanceof Squid
                || entity instanceof Animals
                || entity instanceof Ambient) {
            // it's an animal
            if (specifiedAnimals && !negateAnimals) {
                Logging.finest("Removing an entity because I was told to remove all animals in world %s: %s",
                        entity.getWorld().getName(), entity);
                return true;
            }
            if (specifiedAnimals) {
                specified = true;
            }
            negate = negateAnimals;
        } else if (entity instanceof Monster
                || entity instanceof Ghast
                || entity instanceof Slime
                || entity instanceof Phantom) {
            // it's a monster
            if (specifiedMonsters && !negateMonsters) {
                Logging.finest("Removing an entity because I was told to remove all monsters in world %s: %s",
                        entity.getWorld().getName(), entity);
                return true;
            }
            if (specifiedMonsters) {
                specified = true;
            }
            negate = negateMonsters;
        }
        for (String s : thingsToKill) {
            EntityType type = EntityType.fromName(s);
            if (type != null && type.equals(entity.getType())) {
                specified = true;
                if (!negate) {
                    Logging.finest(
                            "Removing an entity because it WAS specified and we are NOT negating in world %s: %s",
                            entity.getWorld().getName(), entity);
                    return true;
                }
                break;
            }
        }
        if (!specified && negate) {
            Logging.finest("Removing an entity because it was NOT specified and we ARE negating in world %s: %s",
                    entity.getWorld().getName(), entity);
            return true;
        }

        return false;
    }
}
