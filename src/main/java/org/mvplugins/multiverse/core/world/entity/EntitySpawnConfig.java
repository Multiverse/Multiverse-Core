package org.mvplugins.multiverse.core.world.entity;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class EntitySpawnConfig {

    private final CoreConfig config;
    private final Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig;

    EntitySpawnConfig(@NotNull CoreConfig config,
                      @NotNull Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig) {
        this.config = config;
        this.spawnCategoriesConfig = spawnCategoriesConfig;
    }

    public @NotNull SpawnCategoryConfig getSpawnCategoryConfig(@NotNull SpawnCategory spawnCategory) {
        return spawnCategoriesConfig.computeIfAbsent(spawnCategory,
                computeSpawnCategory -> new SpawnCategoryConfig(
                        config,
                        computeSpawnCategory,
                        new MemoryConfiguration()
                ));
    }

    @ApiStatus.AvailableSince("5.7")
    public boolean shouldAllowSpawn(@NotNull EntityType entityType) {
        // Ensure it defaults to true if SpawnCategoryMapper fails to find a spawn category for the entity type,
        // to avoid accidentally breaking mob spawns.
        return SpawnCategoryMapper.getSpawnCategory(entityType)
                .map(spawnCategory -> getSpawnCategoryConfig(spawnCategory).shouldAllowSpawn(entityType))
                .getOrElse(true);
    }

    public boolean shouldAllowSpawn(@NotNull Entity entity) {
        return shouldAllowSpawn(entity.getType());
    }

    public void applyConfigToWorld() {
        for (SpawnCategory category : SpawnCategory.values()) {
            getSpawnCategoryConfig(category).applyConfigToWorld();
        }
    }

    @Override
    public String toString() {
        return "MobsSpawnConfig{" +
                "spawnCategoriesConfig=" + StringFormatter.join(spawnCategoriesConfig.values(), ", ") +
                '}';
    }

    @ApiStatus.Internal
    public @NotNull ConfigurationSection toSection() {
        MemoryConfiguration section = new MemoryConfiguration();
        spawnCategoriesConfig.forEach((spawnCategory, spawnCategoryConfig) -> {
            section.set(String.valueOf(spawnCategory).toLowerCase(Locale.ENGLISH), spawnCategoryConfig.saveSection());
        });
        return section;
    }

    @ApiStatus.Internal
    public static @NotNull EntitySpawnConfig fromSection(
            @NotNull CoreConfig config,
            @NotNull ConfigurationSection section
    ) {
        Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig = new LinkedHashMap<>();
        Map<String, Object> existingCategories = section.getValues(false);
        for (SpawnCategory category : SpawnCategory.values()) {
            Object value = existingCategories.get(category.toString().toLowerCase(Locale.ENGLISH));
            if (!(value instanceof ConfigurationSection)) {
                if (value != null) {
                    Logging.warning("Invalid spawn category config for " + category + ": " + value);
                }
                value = section.createSection(category.toString());
            }
            ConfigurationSection sectionPart = (ConfigurationSection) value;
            spawnCategoriesConfig.put(category, new SpawnCategoryConfig(config, category, sectionPart));
        }
        return new EntitySpawnConfig(config, spawnCategoriesConfig);
    }

    @ApiStatus.Internal
    public void setWorldRef(MultiverseWorld world) {
        spawnCategoriesConfig.forEach((spawnCategory, spawnCategoryConfig) -> {
            spawnCategoryConfig.setWorldRef(world);
        });
    }
}
