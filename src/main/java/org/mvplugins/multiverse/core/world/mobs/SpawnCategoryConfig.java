package org.mvplugins.multiverse.core.world.mobs;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SpawnCategory;
import org.mvplugins.multiverse.core.config.node.serializer.DefaultSerializerProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpawnCategoryConfig {

    private final SpawnCategory spawnCategory;
    private boolean spawn;
    private int tickRate;
    private List<EntityType> exceptions;

    public SpawnCategoryConfig(SpawnCategory spawnCategory) {
        this.spawnCategory = spawnCategory;
        this.spawn = true;
        this.tickRate = -1;
        this.exceptions = new ArrayList<>();
    }

    public SpawnCategory getSpawnCategory() {
        return spawnCategory;
    }

    public boolean isSpawn() {
        return spawn;
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    public int getTickRate() {
        return tickRate;
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    public List<EntityType> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<EntityType> exceptions) {
        this.exceptions = exceptions;
    }

    public void applyConfigToWorld(World bukkitWorld) {
        if (!spawn) {
            if (exceptions.isEmpty()) {
                bukkitWorld.setTicksPerSpawns(spawnCategory, 0);
            } else {
                bukkitWorld.setTicksPerSpawns(spawnCategory, -1);
            }
        } else {
            bukkitWorld.setTicksPerSpawns(spawnCategory, tickRate);
        }
    }

    public boolean shouldAllowSpawn(Entity entity) {
        return shouldAllowSpawn(entity.getType());
    }

    public boolean shouldAllowSpawn(EntityType entityType) {
        return this.spawn != this.exceptions.contains(entityType);
    }

    @Override
    public String toString() {
        return "SpawnCategoryConfig{" +
                "spawnCategory=" + spawnCategory +
                ", spawn=" + spawn +
                ", tickRate=" + tickRate +
                ", exceptions=" + exceptions +
                '}';
    }

    public ConfigurationSection toSection() {
        MemoryConfiguration section = new MemoryConfiguration();
        section.set("spawn", DefaultSerializerProvider.getDefaultSerializer(Boolean.class)
                .serialize(spawn, Boolean.class));
        section.set("tick-rate", DefaultSerializerProvider.getDefaultSerializer(Integer.class)
                .serialize(tickRate, Integer.class));
        section.set("exceptions", exceptions.stream()
                .map(entityType -> DefaultSerializerProvider.getDefaultSerializer(EntityType.class)
                        .serialize(entityType, EntityType.class))
                .collect(Collectors.toList()));
        return section;
    }

    @SuppressWarnings("unchecked,rawtypes")
    public static SpawnCategoryConfig fromSection(SpawnCategory spawnCategory, ConfigurationSection section) {
        SpawnCategoryConfig spawnCategoryConfig = new SpawnCategoryConfig(spawnCategory);
        Try.run(() -> {
            if (section.get("spawn") != null) {
                spawnCategoryConfig.setSpawn(DefaultSerializerProvider.getDefaultSerializer(Boolean.class)
                        .deserialize(section.get("spawn"), Boolean.class));
            }

            if (section.get("tick-rate") != null) {
                spawnCategoryConfig.setTickRate(DefaultSerializerProvider.getDefaultSerializer(Integer.class)
                        .deserialize(section.get("tick-rate"), Integer.class));
            }

            List<EntityType> exceptions = new ArrayList<>();
            if (section.getList("exceptions") != null) {
                section.getList("exceptions").forEach(entityTypeName -> {
                    exceptions.add(EntityType.valueOf(entityTypeName.toString().toUpperCase()));
                });
            }
            spawnCategoryConfig.setExceptions(exceptions);
        }).onFailure(throwable -> {
            Logging.severe("Error parsing SpawnCategoryConfig. " + throwable.getLocalizedMessage());
        });
        return spawnCategoryConfig;
    }
}
