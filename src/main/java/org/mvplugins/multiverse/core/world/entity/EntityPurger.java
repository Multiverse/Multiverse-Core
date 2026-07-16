package org.mvplugins.multiverse.core.world.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

import java.util.Set;
import java.util.function.Predicate;

@Service
public final class EntityPurger {

    public int purgeEntities(LoadedMultiverseWorld world) {
        return purgeEntitiesWithCondition(world, entity -> !world.getEntitySpawnConfig().shouldAllowSpawn(entity));
    }

    public int purgeEntities(LoadedMultiverseWorld world, SpawnCategory spawnCategory) {
        return purgeEntitiesWithCondition(world, entity -> entity.getSpawnCategory().equals(spawnCategory));
    }

    public int purgeEntities(LoadedMultiverseWorld world, SpawnCategory... spawnCategories) {
        Set<SpawnCategory> spawnCategoriesSet = Set.of(spawnCategories);
        return purgeEntitiesWithCondition(world, entity -> spawnCategoriesSet.contains(entity.getSpawnCategory()));
    }

    public int purgeAllEntities(LoadedMultiverseWorld world) {
        return purgeEntitiesWithCondition(world, entity -> true);
    }

    private int purgeEntitiesWithCondition(LoadedMultiverseWorld world, Predicate<Entity> condition) {
        return Math.toIntExact(world.getBukkitWorld()
                .map(bukkitWorld -> bukkitWorld.getEntities().stream()
                        .filter(entity -> !(entity instanceof Player))
                        .filter(condition)
                        .peek(Entity::remove)
                        .count())
                .getOrElse(0L));
    }
}
