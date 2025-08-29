package org.mvplugins.multiverse.core.world.entity;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EntitySpawnConfig {

    private final CoreConfig config;
    private final Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig;

    EntitySpawnConfig(CoreConfig config, Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig) {
        this.config = config;
        this.spawnCategoriesConfig = spawnCategoriesConfig;
    }

    public SpawnCategoryConfig getSpawnCategoryConfig(SpawnCategory spawnCategory) {
        return spawnCategoriesConfig.computeIfAbsent(spawnCategory,
                computeSpawnCategory -> new SpawnCategoryConfig(
                        config,
                        computeSpawnCategory,
                        new MemoryConfiguration()
                ));
    }

    public boolean shouldAllowSpawn(Entity entity) {
        return getSpawnCategoryConfig(entity.getSpawnCategory()).shouldAllowSpawn(entity);
    }

    public void applyConfigToWorld() {
        spawnCategoriesConfig.values()
                .forEach(SpawnCategoryConfig::applyConfigToWorld);
    }

    @Override
    public String toString() {
        return "MobsSpawnConfig{" +
                "spawnCategoriesConfig=" + StringFormatter.join(spawnCategoriesConfig.values(), ", ") +
                '}';
    }

    @ApiStatus.Internal
    public ConfigurationSection toSection() {
        MemoryConfiguration section = new MemoryConfiguration();
        spawnCategoriesConfig.forEach((spawnCategory, spawnCategoryConfig) -> {
            section.set(spawnCategory.toString().toLowerCase(), spawnCategoryConfig.saveSection());
        });
        return section;
    }

    @ApiStatus.Internal
    public static EntitySpawnConfig fromSection(CoreConfig config, ConfigurationSection section) {
        Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig = new LinkedHashMap<>();
        section.getValues(false).forEach((key, value) -> {
            if (!(value instanceof ConfigurationSection sectionPart)) {
                Logging.warning("Invalid spawn category config for " + key + ": " + value);
                return;
            }
            SpawnCategory spawnCategory = SpawnCategory.valueOf(key.toUpperCase());
            spawnCategoriesConfig.put(spawnCategory, new SpawnCategoryConfig(config, spawnCategory, sectionPart));
        });
        return new EntitySpawnConfig(config, spawnCategoriesConfig);
    }

    @ApiStatus.Internal
    public void setWorldRef(MultiverseWorld world) {
        spawnCategoriesConfig.forEach((spawnCategory, spawnCategoryConfig) -> {
            spawnCategoryConfig.setWorldRef(world);
        });
    }
}
