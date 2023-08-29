package com.onarandombox.MultiverseCore.worldnew.config;

import com.onarandombox.MultiverseCore.configuration.node.ConfigNode;
import com.onarandombox.MultiverseCore.configuration.node.EnumNodeSerializer;
import com.onarandombox.MultiverseCore.configuration.node.Node;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import com.onarandombox.MultiverseCore.configuration.node.NodeSerializer;
import com.onarandombox.MultiverseCore.world.configuration.AllowedPortalType;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;

public class WorldConfigNodes {
    private static final NodeSerializer<?> ENUM_NODE_SERIALIZER = new EnumNodeSerializer<>();

    private final NodeGroup nodes = new NodeGroup();

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
            .build());

    public final ConfigNode<Boolean> AUTO_HEAL = node(ConfigNode.builder("auto-heal", Boolean.class)
            .defaultValue(true)
            .name("auto-heal")
            .build());

    public final ConfigNode<Boolean> AUTO_LOAD = node(ConfigNode.builder("auto-load", Boolean.class)
            .defaultValue(true)
            .name("auto-load")
            .build());

    public final ConfigNode<Difficulty> DIFFICULTY = node(ConfigNode.builder("difficulty", Difficulty.class)
            .defaultValue(Difficulty.NORMAL)
            .name("difficulty")
            .serializer((NodeSerializer<Difficulty>) ENUM_NODE_SERIALIZER)
            .build());

    public final ConfigNode<World.Environment> ENVIRONMENT = node(ConfigNode.builder("environment", World.Environment.class)
            .defaultValue(World.Environment.NORMAL)
            .name("environment")
            .serializer((NodeSerializer<World.Environment>) ENUM_NODE_SERIALIZER)
            .build());

    public final ConfigNode<GameMode> GAMEMODE = node(ConfigNode.builder("gamemode", GameMode.class)
            .defaultValue(GameMode.SURVIVAL)
            .name("gamemode")
            .serializer((NodeSerializer<GameMode>) ENUM_NODE_SERIALIZER)
            .build());

    public final ConfigNode<String> GENERATOR = node(ConfigNode.builder("generator", String.class)
            .defaultValue("")
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

    public final ConfigNode<Boolean> KEEP_SPAWN_IN_MEMORY = node(ConfigNode.builder("keep-spawn-in-memory", Boolean.class)
            .defaultValue(true)
            .name("keep-spawn-in-memory")
            .build());

    public final ConfigNode<Integer> PLAYER_LIMIT = node(ConfigNode.builder("player-limit", Integer.class)
            .defaultValue(-1)
            .name("player-limit")
            .build());

    public final ConfigNode<AllowedPortalType> PORTAL_FORM = node(ConfigNode.builder("portal-form", AllowedPortalType.class)
            .defaultValue(AllowedPortalType.ALL)
            .name("portal-form")
            .serializer((NodeSerializer<AllowedPortalType>) ENUM_NODE_SERIALIZER)
            .build());

    public final ConfigNode<Boolean> PVP = node(ConfigNode.builder("pvp", Boolean.class)
            .defaultValue(true)
            .name("pvp")
            .build());

    public final ConfigNode<String> RESPAWN_WORLD = node(ConfigNode.builder("respawn-world", String.class)
            .defaultValue("")
            .name("respawn-world")
            .build());

    public final ConfigNode<Double> SCALE = node(ConfigNode.builder("scale", Double.class)
            .defaultValue(1.0)
            .name("scale")
            .build());

    public final ConfigNode<String> SEED = node(ConfigNode.builder("seed", String.class)
            .defaultValue("")
            .name("seed")
            .build());

    //todo: color and style
    //todo: spawning
    //todo: entryfee
    //todo: spawnLocation
    //todo: worldBlacklist
}
