package org.mvplugins.multiverse.core.world.entity;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SpawnCategory;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.utils.ReflectHelper;

import java.lang.reflect.Field;
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

    static {
        buildSpawnCategoryMap();
    }

    private static Map<SpawnCategory, List<EntityType>> spawnCategoryMap;

    private static void buildSpawnCategoryMap() {
        spawnCategoryMap = new HashMap<>();

        Class<?> entityTypeClass = ReflectHelper.getClass("net.minecraft.world.entity.EntityType");
        if (entityTypeClass == null) {
            Logging.warning("Failed to find EntityType class. SpawnCategoryMapper will not work.");
            return;
        }
        Method getCategoryMethod = ReflectHelper.getMethod(entityTypeClass, "getCategory");
        if (getCategoryMethod == null) {
            Logging.warning("Failed to find getCategory method. SpawnCategoryMapper will not work.");
            return;
        }
        Class<?> craftSpawnCategoryClass = ReflectHelper.getClass("org.bukkit.craftbukkit.util.CraftSpawnCategory");
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
        Field[] entityTypeFields = entityTypeClass.getFields();
        for (Field entityTypeField : entityTypeFields) {
            String entityName = entityTypeField.getName();
            EntityType entityType = Try.of(() -> EntityType.valueOf(entityName)).getOrNull();
            if (entityType == null) {
                continue;
            }
            Object nmsEntityType = ReflectHelper.getFieldValue(null, entityTypeField, entityTypeClass);
            Object nsmMobCategory = ReflectHelper.invokeMethod(nmsEntityType, getCategoryMethod);
            if (nsmMobCategory == null) {
                continue;
            }
            Object bukkitSpawnCategory = ReflectHelper.invokeMethod(null, toBukkitMethod, nsmMobCategory);
            if (!(bukkitSpawnCategory instanceof SpawnCategory spawnCategory)) {
                continue;
            }
            spawnCategoryMap.computeIfAbsent(spawnCategory, ignore -> new ArrayList<>())
                    .add(entityType);
        }
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
