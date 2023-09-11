package com.onarandombox.MultiverseCore.worldnew.config;

import com.onarandombox.MultiverseCore.configuration.node.ConfigNode;
import com.onarandombox.MultiverseCore.configuration.node.Node;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import com.onarandombox.MultiverseCore.world.configuration.AllowedPortalType;
import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents nodes in a world configuration.
 */
public class WorldConfigNodes {
    static final double CONFIG_VERSION = 1.0;

    private final NodeGroup nodes = new NodeGroup();
    LoadedMultiverseWorld world = null;

    WorldConfigNodes() {
    }

    public NodeGroup getNodes() {
        return nodes;
    }

    private <N extends Node> N node(N node) {
        nodes.add(node);
        return node;
    }

    final ConfigNode<Boolean> ADJUST_SPAWN = node(ConfigNode.builder("adjust-spawn", Boolean.class)
            .defaultValue(false)
            .build());

    final ConfigNode<String> ALIAS = node(ConfigNode.builder("alias", String.class)
            .defaultValue("")
            .build());

    final ConfigNode<Boolean> ALLOW_FLIGHT = node(ConfigNode.builder("allow-flight", Boolean.class)
            .defaultValue(false)
            .build());

    final ConfigNode<Boolean> ALLOW_WEATHER = node(ConfigNode.builder("allow-weather", Boolean.class)
            .defaultValue(true)
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> {
                    if (!world.isClearWeather() && !newValue) {
                        world.setThundering(false);
                        world.setStorm(false);
                    }
                });
            })
            .build());

    final ConfigNode<Boolean> AUTO_HEAL = node(ConfigNode.builder("auto-heal", Boolean.class)
            .defaultValue(true)
            .build());

    final ConfigNode<Boolean> AUTO_LOAD = node(ConfigNode.builder("auto-load", Boolean.class)
            .defaultValue(true)
            .build());

    final ConfigNode<Boolean> BED_RESPAWN = node(ConfigNode.builder("bed-respawn", Boolean.class)
            .defaultValue(true)
            .build());

    final ConfigNode<Difficulty> DIFFICULTY = node(ConfigNode.builder("difficulty", Difficulty.class)
            .defaultValue(Difficulty.NORMAL)
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setDifficulty(newValue));
            })
            .build());

    final ConfigNode<Double> ENTRY_FEE_AMOUNT = node(ConfigNode.builder("entry-fee.amount", Double.class)
            .defaultValue(0.0)
            .name("entryfee-amount")
            .build());

    final ConfigNode<Material> ENTRY_FEE_CURRENCY = node(ConfigNode.builder("entry-fee.currency", Material.class)
            .defaultValue(Material.AIR) // TODO: Convert from material ID
            .name("entryfee-currency")
            .build());

    final ConfigNode<World.Environment> ENVIRONMENT = node(ConfigNode
            .builder("environment", World.Environment.class)
            .defaultValue(World.Environment.NORMAL)
            .name(null)
            .build());

    final ConfigNode<GameMode> GAMEMODE = node(ConfigNode.builder("gamemode", GameMode.class)
            .defaultValue(GameMode.SURVIVAL)
            // TODO: Set all gamemodes of players in world to this gamemode
            .build());

    final ConfigNode<String> GENERATOR = node(ConfigNode.builder("generator", String.class)
            .defaultValue("@error") // this should be set on world creation
            .name(null)
            .build());

    final ConfigNode<Boolean> HIDDEN = node(ConfigNode.builder("hidden", Boolean.class)
            .defaultValue(false)
            .build());

    final ConfigNode<Boolean> HUNGER = node(ConfigNode.builder("hunger", Boolean.class)
            .defaultValue(true)
            .build());

    final ConfigNode<Boolean> KEEP_SPAWN_IN_MEMORY = node(ConfigNode
            .builder("keep-spawn-in-memory", Boolean.class)
            .defaultValue(true)
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setKeepSpawnInMemory(newValue));
            })
            .build());

    final ConfigNode<Integer> PLAYER_LIMIT = node(ConfigNode.builder("player-limit", Integer.class)
            .defaultValue(-1)
            .name("player-limit")
            .build());

    final ConfigNode<AllowedPortalType> PORTAL_FORM = node(ConfigNode
            .builder("portal-form", AllowedPortalType.class)
            .defaultValue(AllowedPortalType.ALL)
            .build());

    final ConfigNode<Boolean> PVP = node(ConfigNode.builder("pvp", Boolean.class)
            .defaultValue(true)
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setPVP(newValue));
            })
            .build());

    final ConfigNode<String> RESPAWN_WORLD = node(ConfigNode.builder("respawn-world", String.class)
            .defaultValue("")
            .build());

    final ConfigNode<Double> SCALE = node(ConfigNode.builder("scale", Double.class)
            .defaultValue(1.0)
            .build());

    final ConfigNode<Long> SEED = node(ConfigNode.builder("seed", Long.class)
            .defaultValue(Long.MIN_VALUE)
            .name(null)
            .build());

    final ConfigNode<Location> SPAWN_LOCATION = node(ConfigNode.builder("spawn-location", Location.class)
            .defaultValue(new NullLocation())
            .name(null)
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                if (newValue == null || newValue instanceof NullLocation) return;
                world.getBukkitWorld().peek(bukkitWorld -> {
                    bukkitWorld.setSpawnLocation(newValue.getBlockX(), newValue.getBlockY(), newValue.getBlockZ());
                    newValue.setWorld(bukkitWorld);
                });
            })
            .build());

    final ConfigNode<Boolean> SPAWNING_ANIMALS = node(ConfigNode.builder("spawning.animals.spawn", Boolean.class)
            .defaultValue(true)
            .name("spawning-animals")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setSpawnFlags(world.getAllowMonsters(), newValue));
            })
            .build());

    final ConfigNode<Integer> SPAWNING_ANIMALS_TICKS = node(ConfigNode
            .builder("spawning.animals.tick-rate", Integer.class)
            .defaultValue(-1)
            .name("spawning-animals-ticks")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setTicksPerAnimalSpawns(newValue));
            })
            .build());

    final ConfigNode<List> SPAWNING_ANIMALS_EXCEPTIONS = node(ConfigNode
            .builder("spawning.animals.exceptions", List.class)
            .defaultValue(new ArrayList<>())
            .name("spawning-animals-exceptions")
            .build());

    final ConfigNode<Boolean> SPAWNING_MONSTERS = node(ConfigNode
            .builder("spawning.monsters.spawn", Boolean.class)
            .defaultValue(true)
            .name("spawning-monsters")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setSpawnFlags(newValue, world.getAllowAnimals()));
            })
            .build());

    final ConfigNode<Integer> SPAWNING_MONSTERS_TICKS = node(ConfigNode
            .builder("spawning.monsters.tick-rate", Integer.class)
            .defaultValue(-1)
            .name("spawning-monsters-ticks")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setTicksPerMonsterSpawns(newValue));
            })
            .build());

    final ConfigNode<List> SPAWNING_MONSTERS_EXCEPTIONS = node(ConfigNode
            .builder("spawning.monsters.exceptions", List.class)
            .defaultValue(new ArrayList<>())
            .name("spawning-monsters-exceptions")
            .build());

    final ConfigNode<List> WORLD_BLACKLIST = node(ConfigNode.builder("world-blacklist", List.class)
            .defaultValue(new ArrayList<>())
            .build());

    final ConfigNode<Double> VERSION = node(ConfigNode.builder("version", Double.class)
            .defaultValue(CONFIG_VERSION)
            .name(null)
            .build());
}
