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

public class WorldConfigNodes {
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

    public final ConfigNode<Boolean> ADJUST_SPAWN = node(ConfigNode.builder("adjust-spawn", Boolean.class)
            .defaultValue(false)
            .name("adjust-spawn")
            .build());

    public final ConfigNode<String> ALIAS = node(ConfigNode.builder("alias", String.class)
            .defaultValue("")
            .name("alias")
            .build());

    public final ConfigNode<Boolean> ALLOW_FLIGHT = node(ConfigNode.builder("allow-flight", Boolean.class)
            .defaultValue(false)
            .name("allow-flight")
            .build());

    public final ConfigNode<Boolean> ALLOW_WEATHER = node(ConfigNode.builder("allow-weather", Boolean.class)
            .defaultValue(true)
            .name("allow-weather")
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

    public final ConfigNode<Boolean> AUTO_HEAL = node(ConfigNode.builder("auto-heal", Boolean.class)
            .defaultValue(true)
            .name("auto-heal")
            .build());

    public final ConfigNode<Boolean> AUTO_LOAD = node(ConfigNode.builder("auto-load", Boolean.class)
            .defaultValue(true)
            .name("auto-load")
            .build());

    public final ConfigNode<Boolean> BED_RESPAWN = node(ConfigNode.builder("bed-respawn", Boolean.class)
            .defaultValue(true)
            .name("bed-respawn")
            .build());

    public final ConfigNode<Difficulty> DIFFICULTY = node(ConfigNode.builder("difficulty", Difficulty.class)
            .defaultValue(Difficulty.NORMAL)
            .name("difficulty")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setDifficulty(newValue));
            })
            .build());

    public final ConfigNode<Double> ENTRY_FEE_AMOUNT = node(ConfigNode.builder("entry-fee.amount", Double.class)
            .defaultValue(0.0)
            .name("entryfee-amount")
            .build());

    public final ConfigNode<Material> ENTRY_FEE_CURRENCY = node(ConfigNode.builder("entry-fee.currency", Material.class)
            .defaultValue(Material.AIR) // TODO: Convert from material ID
            .name("entryfee-currency")
            .build());

    public final ConfigNode<World.Environment> ENVIRONMENT = node(ConfigNode
            .builder("environment", World.Environment.class)
            .defaultValue(World.Environment.NORMAL)
            .name("environment")
            .build());

    public final ConfigNode<GameMode> GAMEMODE = node(ConfigNode.builder("gamemode", GameMode.class)
            .defaultValue(GameMode.SURVIVAL)
            .name("gamemode") // TODO: Set all gamemodes of players in world to this gamemode
            .build());

    public final ConfigNode<String> GENERATOR = node(ConfigNode.builder("generator", String.class)
            .defaultValue("@error") // this should be set on world creation
            .name("generator")
            .build());

    public final ConfigNode<Boolean> HIDDEN = node(ConfigNode.builder("hidden", Boolean.class)
            .defaultValue(false)
            .name("hidden")
            .build());

    public final ConfigNode<Boolean> HUNGER = node(ConfigNode.builder("hunger", Boolean.class)
            .defaultValue(true)
            .name("hunger")
            .build());

    public final ConfigNode<Boolean> KEEP_SPAWN_IN_MEMORY = node(ConfigNode
            .builder("keep-spawn-in-memory", Boolean.class)
            .defaultValue(true)
            .name("keep-spawn-in-memory")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setKeepSpawnInMemory(newValue));
            })
            .build());

    public final ConfigNode<Integer> PLAYER_LIMIT = node(ConfigNode.builder("player-limit", Integer.class)
            .defaultValue(-1)
            .name("player-limit")
            .build());

    public final ConfigNode<AllowedPortalType> PORTAL_FORM = node(ConfigNode
            .builder("portal-form", AllowedPortalType.class)
            .defaultValue(AllowedPortalType.ALL)
            .name("portal-form")
            .build());

    public final ConfigNode<Boolean> PVP = node(ConfigNode.builder("pvp", Boolean.class)
            .defaultValue(true)
            .name("pvp")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setPVP(newValue));
            })
            .build());

    public final ConfigNode<String> RESPAWN_WORLD = node(ConfigNode.builder("respawn-world", String.class)
            .defaultValue("")
            .name("respawn-world")
            .build());

    public final ConfigNode<Double> SCALE = node(ConfigNode.builder("scale", Double.class)
            .defaultValue(1.0)
            .name("scale")
            .build());

    public final ConfigNode<Long> SEED = node(ConfigNode.builder("seed", Long.class)
            .defaultValue(Long.MIN_VALUE)
            .name("seed")
            .build());

    public final ConfigNode<Location> SPAWN_LOCATION = node(ConfigNode.builder("spawn-location", Location.class)
            .defaultValue(new NullLocation())
            .name("spawn-location")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> {
                    world.setSpawnLocation(newValue.getBlockX(), newValue.getBlockY(), newValue.getBlockZ());
                    newValue.setWorld(world);
                });
            })
            .build());

    public final ConfigNode<Boolean> SPAWNING_ANIMALS = node(ConfigNode.builder("spawning.animals.spawn", Boolean.class)
            .defaultValue(true)
            .name("spawning-animals")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setSpawnFlags(world.getAllowMonsters(), newValue));
            })
            .build());

    public final ConfigNode<Integer> SPAWNING_ANIMALS_TICKS = node(ConfigNode
            .builder("spawning.animals.tick-rate", Integer.class)
            .defaultValue(-1)
            .name("spawning-animals-ticks")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setTicksPerAnimalSpawns(newValue));
            })
            .build());

    public final ConfigNode<List> SPAWNING_ANIMALS_EXCEPTIONS = node(ConfigNode
            .builder("spawning.animals.exceptions", List.class)
            .defaultValue(new ArrayList<>())
            .name("spawning-animals-exceptions")
            .build());

    public final ConfigNode<Boolean> SPAWNING_MONSTERS = node(ConfigNode
            .builder("spawning.monsters.spawn", Boolean.class)
            .defaultValue(true)
            .name("spawning-monsters")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setSpawnFlags(newValue, world.getAllowAnimals()));
            })
            .build());

    public final ConfigNode<Integer> SPAWNING_MONSTERS_TICKS = node(ConfigNode
            .builder("spawning.monsters.tick-rate", Integer.class)
            .defaultValue(-1)
            .name("spawning-monsters-ticks")
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setTicksPerMonsterSpawns(newValue));
            })
            .build());

    public final ConfigNode<List> SPAWNING_MONSTERS_EXCEPTIONS = node(ConfigNode
            .builder("spawning.monsters.exceptions", List.class)
            .defaultValue(new ArrayList<>())
            .name("spawning-monsters-exceptions")
            .build());

    public final ConfigNode<List> WORLD_BLACKLIST = node(ConfigNode.builder("world-blacklist", List.class)
            .defaultValue(new ArrayList<>())
            .name("world-blacklist")
            .build());

    // TODO: Migrate color and style into alias
}
