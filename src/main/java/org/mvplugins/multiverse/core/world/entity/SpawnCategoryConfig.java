package org.mvplugins.multiverse.core.world.entity;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SpawnCategory;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.config.handle.MemoryConfigurationHandle;
import org.mvplugins.multiverse.core.config.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.config.node.ConfigNode;
import org.mvplugins.multiverse.core.config.node.ListConfigNode;
import org.mvplugins.multiverse.core.config.node.Node;
import org.mvplugins.multiverse.core.config.node.NodeGroup;
import org.mvplugins.multiverse.core.config.node.serializer.NodeSerializer;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

import java.util.ArrayList;
import java.util.List;

public final class SpawnCategoryConfig {

    private final CoreConfig config;
    private final SpawnCategory spawnCategory;
    private final MemoryConfigurationHandle handle;
    private final StringPropertyHandle stringPropertyHandle;
    private final Nodes nodes;

    private MultiverseWorld world;

    SpawnCategoryConfig(CoreConfig config, SpawnCategory spawnCategory, ConfigurationSection section) {
        this.config = config;
        this.spawnCategory = spawnCategory;
        this.nodes = new Nodes();
        this.handle = MemoryConfigurationHandle.builder(section, nodes.nodes)
                .build();
        this.stringPropertyHandle = new StringPropertyHandle(handle);
        this.handle.load();
    }

    ConfigurationSection saveSection() {
        handle.save().onFailure(throwable -> {
            Logging.warning("Failed to save SpawnCategoryConfig. " + throwable.getLocalizedMessage());
        });
        return handle.getConfig();
    }

    void setWorldRef(MultiverseWorld world) {
        this.world = world;
    }

    void applyConfigToWorld() {
        if (spawnCategory == SpawnCategory.MISC) {
            // Cannot control misc spawn with setTicksPerSpawns
            return;
        }
        if (!(world instanceof LoadedMultiverseWorld loadedWorld)) {
            return;
        }
        loadedWorld.getBukkitWorld().peek(bukkitWorld -> {
            applyTickPerSpawns(bukkitWorld);
            applySpawnLimit(bukkitWorld);
        });
    }

    private void applyTickPerSpawns(World bukkitWorld) {
        if (!config.getApplyEntitySpawnLimit()) {
            Logging.finer("World %s %s skipping setTicksPerSpawns due to core config", world.getName(), spawnCategory);
            return;
        }
        if (!isSpawn() && getExceptions().isEmpty()) {
            Logging.finer("World %s %s setTicksPerSpawns: 0", world.getName(), spawnCategory);
            bukkitWorld.setTicksPerSpawns(spawnCategory, 0);
            return;
        }
        if (getTickRate() == -2) {
            Logging.finer("World %s %s skipping setTicksPerSpawns as tick-rate is UNSET", world.getName(), spawnCategory);
            return;
        }
        Logging.finer("World %s %s setTicksPerSpawns: %d", world.getName(), spawnCategory, getTickRate());
        bukkitWorld.setTicksPerSpawns(spawnCategory, getTickRate());
    }

    private void applySpawnLimit(World bukkitWorld) {
        if (!config.getApplyEntitySpawnLimit()) {
            Logging.finer("Skipping World %s %s setSpawnLimit due to core config", world.getName(), spawnCategory);
            return;
        }
        if (getSpawnLimit() == -2) {
            Logging.finer("World %s %s skipping setSpawnLimit as spawn-limit is UNSET", world.getName(), spawnCategory);
            return;
        }
        Logging.finer("World %s %s setSpawnLimit: %d", world.getName(), spawnCategory, getSpawnLimit());
        bukkitWorld.setSpawnLimit(spawnCategory, getSpawnLimit());
    }

    public StringPropertyHandle getStringPropertyHandle() {
        return stringPropertyHandle;
    }

    public SpawnCategory getSpawnCategory() {
        return spawnCategory;
    }

    public boolean isSpawn() {
        return handle.get(nodes.spawn);
    }

    public Try<Void> setSpawn(boolean spawn) {
        return handle.set(nodes.spawn, spawn);
    }

    public int getTickRate() {
        return handle.get(nodes.tickRate);
    }

    public Try<Void> setTickRate(int tickRate) {
        return handle.set(nodes.tickRate, tickRate);
    }

    public int getSpawnLimit() {
        return handle.get(nodes.spawnLimit);
    }

    public Try<Void> setSpawnLimit(int spawnLimit) {
        return handle.set(nodes.spawnLimit, spawnLimit);
    }

    public List<EntityType> getExceptions() {
        return handle.get(nodes.exceptions);
    }

    public Try<Void> setExceptions(List<EntityType> exceptions) {
        return handle.set(nodes.exceptions, exceptions);
    }

    public boolean shouldAllowSpawn(Entity entity) {
        return shouldAllowSpawn(entity.getType());
    }

    public boolean shouldAllowSpawn(EntityType entityType) {
        return isSpawn() != getExceptions().contains(entityType);
    }

    @Override
    public String toString() {
        return "SpawnCategoryConfig{" +
                "spawnCategory=" + spawnCategory +
                ", spawn=" + isSpawn() +
                ", tickRate=" + getTickRate() +
                ", exceptions=" + getExceptions() +
                '}';
    }

    private final class Nodes {
        private final NodeGroup nodes = new NodeGroup();

        private <N extends Node> N node(N node) {
            nodes.add(node);
            return node;
        }

        final ConfigNode<Boolean> spawn = node(ConfigNode.builder("spawn", Boolean.class)
                .defaultValue(true)
                .onLoadAndChange((oldValue, newValue) -> applyConfigToWorld())
                .build());

        final ConfigNode<Integer> tickRate = node(ConfigNode.builder("tick-rate", Integer.class)
                .defaultValue(-2)
                .suggester(input -> List.of("@unset", "@bukkit", "10", "100", "400", "1000"))
                .serializer(SpawnValueSerializer.INSTANCE)
                .onLoadAndChange((oldValue, newValue) -> applyConfigToWorld())
                .onChange((sender, oldValue, newValue) -> {
                    if (!config.getApplyEntitySpawnRate()) {
                        sender.sendMessage(ChatColor.RED + "Warning: Changing tick rates has no effect because " +
                                "'apply-entity-spawn-rate' is disabled in the core config.");
                    } else if (newValue == -2) {
                        sender.sendMessage(ChatColor.YELLOW + "Note: Setting tick-rate to '@unset' may not reset to " +
                                "the server default until the world is reloaded or server is restarted.");
                    }
                })
                .build());

        final ConfigNode<Integer> spawnLimit = node(ConfigNode.builder("spawn-limit", Integer.class)
                .defaultValue(-2)
                .suggester(input -> List.of("@unset", "@bukkit", "10", "100", "400", "1000"))
                .serializer(SpawnValueSerializer.INSTANCE)
                .onLoadAndChange((oldValue, newValue) -> applyConfigToWorld())
                .onChange((sender, oldValue, newValue) -> {
                    if (!config.getApplyEntitySpawnLimit()) {
                        sender.sendMessage(ChatColor.RED + "Warning: Changing spawn limits has no effect because " +
                                "'apply-entity-spawn-limit' is disabled in the core config.");
                    } else if (newValue == -2) {
                        sender.sendMessage(ChatColor.YELLOW + "Note: Setting spawn-limit to '@unset' may not reset to " +
                                "the server default until the world is reloaded or server is restarted.");
                    }
                })
                .build());

        final ListConfigNode<EntityType> exceptions = node(ListConfigNode.listBuilder("exceptions", EntityType.class)
                .defaultValue(ArrayList::new)
                .itemSuggester(input -> SpawnCategoryMapper.getEntityTypes(spawnCategory).stream()
                        .map(EntityType::name)
                        .toList())
                .onLoadAndChange((oldValue, newValue) -> applyConfigToWorld())
                .build());
    }

    private static class SpawnValueSerializer implements NodeSerializer<Integer> {
        private static final SpawnValueSerializer INSTANCE = new SpawnValueSerializer();

        @Override
        public Integer deserialize(Object object, Class<Integer> type) {
            String str = String.valueOf(object);
            if (str.equalsIgnoreCase("@unset")) {
                return -2;
            }
            if (str.equalsIgnoreCase("@bukkit")) {
                return -1;
            }
            return Integer.parseInt(str);
        }

        @Override
        public Object serialize(Integer object, Class<Integer> type) {
            return switch (object) {
                case -2 -> "@unset";
                case -1 -> "@bukkit";
                default -> object;
            };
        }
    }
}
