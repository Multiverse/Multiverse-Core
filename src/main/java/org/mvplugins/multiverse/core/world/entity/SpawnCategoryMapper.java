package org.mvplugins.multiverse.core.world.entity;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SpawnCategory;
import org.mvplugins.multiverse.core.utils.ReflectHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses reflection into nms to map entity types to spawn categories.
 */
final class SpawnCategoryMapper {

    private static final Map<EntityType, SpawnCategory> entityTypeToSpawnCategoryMap;
    private static final Map<SpawnCategory, List<EntityType>> spawnCategoryMap;

    static {
        entityTypeToSpawnCategoryMap = new HashMap<>();
        spawnCategoryMap = new HashMap<>();
        buildSpawnCategoryMap();
    }

    private static void buildSpawnCategoryMap() {
        Class<?> entityTypeClass = ReflectHelper.tryGetClass("net.minecraft.world.entity.EntityType").getOrNull();
        if (entityTypeClass == null) {
            Logging.warning("Failed to find EntityType class. SpawnCategoryMapper will not work.");
            return;
        }
        Method getCategoryMethod = ReflectHelper.tryGetMethod(entityTypeClass, "getCategory").getOrNull();
        if (getCategoryMethod == null) {
            Logging.warning("Failed to find getCategory method. SpawnCategoryMapper will not work.");
            return;
        }
        Class<?> craftSpawnCategoryClass = ReflectHelper.tryGetClass("org.bukkit.craftbukkit.util.CraftSpawnCategory").getOrNull();
        if (craftSpawnCategoryClass == null) {
            Logging.warning("Failed to find CraftSpawnCategory class. SpawnCategoryMapper will not work.");
            return;
        }
        Method toBukkitMethod = Arrays.stream(craftSpawnCategoryClass.getMethods())
                .filter(method -> method.getName().equals("toBukkit"))
                .findFirst()
                .orElse(null);
        if (toBukkitMethod == null) {
            Logging.warning("Failed to find toBukkit method. SpawnCategoryMapper will not work.");
            return;
        }
        Arrays.stream(entityTypeClass.getFields()).forEach(entityTypeField -> Try.of(() -> EntityType.valueOf(entityTypeField.getName()))
                .peek(entityType -> ReflectHelper.tryGetStaticFieldValue(entityTypeField, entityTypeClass)
                        .flatMap(nmsEntityType -> ReflectHelper.tryInvokeMethod(nmsEntityType, getCategoryMethod))
                        .flatMap(nsmMobCategory -> ReflectHelper.tryInvokeStaticMethod(toBukkitMethod, nsmMobCategory))
                        .filter(bukkitSpawnCategory -> bukkitSpawnCategory instanceof SpawnCategory)
                        .map(bukkitSpawnCategory -> (SpawnCategory) bukkitSpawnCategory)
                        .peek(bukkitSpawnCategory -> entityTypeToSpawnCategoryMap.put(entityType, bukkitSpawnCategory))
                        .peek(bukkitSpawnCategory -> spawnCategoryMap
                                .computeIfAbsent(bukkitSpawnCategory, ignore -> new ArrayList<>())
                                .add(entityType))));
    }

    /**
     * Gets the spawn category that the given entity type is in.
     *
     * @param entityType The entity type
     * @return The spawn category that the given entity type is in, or null if it cannot be determined
     */
    static SpawnCategory getSpawnCategory(EntityType entityType) {
        return entityTypeToSpawnCategoryMap.get(entityType);
    }

    /**
     * Gets the entity types for a spawn category
     *
     * @param spawnCategory The spawn category
     * @return The entity types associated with the spawn category
     */
    static List<EntityType> getEntityTypes(SpawnCategory spawnCategory) {
        if (spawnCategoryMap == null) {
            return Collections.emptyList();
        }
        return spawnCategoryMap.get(spawnCategory);
    }
}
