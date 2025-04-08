package org.mvplugins.multiverse.core.world.mobs;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SpawnCategory;
import org.mvplugins.multiverse.core.utils.StringFormatter;

import java.util.HashMap;
import java.util.Map;

public class MobsSpawnConfig {

    private final Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig;

    public MobsSpawnConfig() {
        this(new HashMap<>());
    }

    public MobsSpawnConfig(Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig) {
        this.spawnCategoriesConfig = spawnCategoriesConfig;
    }

    public SpawnCategoryConfig getSpawnCategoryConfig(SpawnCategory spawnCategory) {
        return spawnCategoriesConfig.computeIfAbsent(spawnCategory, SpawnCategoryConfig::new);
    }

    public boolean shouldAllowSpawn(Entity entity) {
        return getSpawnCategoryConfig(entity.getSpawnCategory()).shouldAllowSpawn(entity);
    }

    public void applyConfigToWorld(World bukkitWorld) {
        spawnCategoriesConfig.values()
                .forEach(spawnCategoryConfig -> spawnCategoryConfig.applyConfigToWorld(bukkitWorld));
    }

    @Override
    public String toString() {
        return "MobsSpawnConfig{" +
                "spawnCategoriesConfig=" + StringFormatter.join(spawnCategoriesConfig.values(), ", ") +
                '}';
    }

    public ConfigurationSection toSection() {
        MemoryConfiguration section = new MemoryConfiguration();
        spawnCategoriesConfig.forEach((spawnCategory, spawnCategoryConfig) -> {
            section.set(spawnCategory.toString().toLowerCase(), spawnCategoryConfig.toSection());
        });
        return section;
    }

    public static MobsSpawnConfig fromSection(ConfigurationSection section) {
        Map<SpawnCategory, SpawnCategoryConfig> spawnCategoriesConfig = new HashMap<>();
        section.getValues(false).forEach((key, value) -> {
            if (!(value instanceof ConfigurationSection sectionPart)) {
                Logging.warning("Invalid spawn category config for " + key + ": " + value);
                return;
            }
            SpawnCategory spawnCategory = SpawnCategory.valueOf(key.toUpperCase());
            spawnCategoriesConfig.put(spawnCategory, SpawnCategoryConfig.fromSection(spawnCategory, sectionPart));
        });
        return new MobsSpawnConfig(spawnCategoriesConfig);
    }
}
