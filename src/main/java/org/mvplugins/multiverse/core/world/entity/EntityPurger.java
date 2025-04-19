package org.mvplugins.multiverse.core.world.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.SpawnCategory;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public final class EntityPurger {

    public int purgeEntities(LoadedMultiverseWorld world) {
        AtomicInteger purgeCount = new AtomicInteger(0);
        world.getBukkitWorld().peek(bukkitWorld -> {
            for (Entity entity : bukkitWorld.getEntities()) {
                if (!world.getEntitySpawnConfig().shouldAllowSpawn(entity)) {
                    entity.remove();
                    purgeCount.incrementAndGet();
                }
            }
        });
        return purgeCount.get();
    }

    public int purgeEntities(LoadedMultiverseWorld world, SpawnCategory spawnCategory) {
        AtomicInteger purgeCount = new AtomicInteger(0);
        world.getBukkitWorld().peek(bukkitWorld -> {
            for (Entity entity : bukkitWorld.getEntities()) {
                if (entity.getSpawnCategory() == spawnCategory) {
                    entity.remove();
                    purgeCount.incrementAndGet();
                }
            }
        });
        return purgeCount.get();
    }

    public int purgeEntities(LoadedMultiverseWorld world, SpawnCategory... spawnCategories) {
        Set<SpawnCategory> spawnCategoriesSet = Set.of(spawnCategories);
        AtomicInteger purgeCount = new AtomicInteger(0);
        world.getBukkitWorld().peek(bukkitWorld -> {
            for (Entity entity : bukkitWorld.getEntities()) {
                if (spawnCategoriesSet.contains(entity.getSpawnCategory())) {
                    entity.remove();
                    purgeCount.incrementAndGet();
                }
            }
        });
        return purgeCount.get();
    }

    public int purgeAllEntities(LoadedMultiverseWorld world) {
        AtomicInteger purgeCount = new AtomicInteger(0);
        world.getBukkitWorld().peek(bukkitWorld -> {
            for (Entity entity : bukkitWorld.getEntities()) {
                entity.remove();
                purgeCount.incrementAndGet();
            }
        });
        return purgeCount.get();
    }
}
