package org.mvplugins.multiverse.core.world.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.configuration.node.ConfigNode;
import org.mvplugins.multiverse.core.configuration.node.ListConfigNode;
import org.mvplugins.multiverse.core.configuration.node.Node;
import org.mvplugins.multiverse.core.configuration.node.NodeGroup;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.EnforcementHandler;

/**
 * Represents nodes in a world configuration.
 */
public class WorldConfigNodes {
    private static final double CONFIG_VERSION = 1.0;

    private final NodeGroup nodes = new NodeGroup();
    private WorldManager worldManager;
    private EnforcementHandler enforcementHandler;
    private LoadedMultiverseWorld world = null;

    WorldConfigNodes(@NotNull MultiverseCore multiverseCore) {
        this.worldManager = multiverseCore.getServiceLocator().getService(WorldManager.class);
        this.enforcementHandler = multiverseCore.getServiceLocator().getService(EnforcementHandler.class);
    }

    LoadedMultiverseWorld getWorld() {
        return world;
    }

    void setWorld(LoadedMultiverseWorld world) {
        this.world = world;
    }

    public NodeGroup getNodes() {
        return nodes;
    }

    private <N extends Node> N node(N node) {
        nodes.add(node);
        return node;
    }

    // BEGIN CHECKSTYLE-SUPPRESSION: Javadoc
    // BEGIN CHECKSTYLE-SUPPRESSION: MemberName
    // BEGIN CHECKSTYLE-SUPPRESSION: Abbreviation
    // BEGIN CHECKSTYLE-SUPPRESSION: VisibilityModifier

    final ConfigNode<Boolean> ADJUST_SPAWN = node(ConfigNode.builder("adjust-spawn", Boolean.class)
            .defaultValue(false)
            .build());

    final ConfigNode<String> ALIAS = node(ConfigNode.builder("alias", String.class)
            .defaultValue("")
            .build());

    final ConfigNode<Boolean> ALLOW_FLIGHT = node(ConfigNode.builder("allow-flight", Boolean.class)
            .defaultValue(false)
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                enforcementHandler.handleAllFlightEnforcement(world);
            })
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

    final ConfigNode<Boolean> ANCHOR_RESPAWN = node(ConfigNode.builder("anchor-respawn", Boolean.class)
            .defaultValue(true)
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

    final ConfigNode<Biome> BIOME = node(ConfigNode.builder("biome", Biome.class)
            .defaultValue(Biome.CUSTOM)
            .name(null)
            .serializer(new BiomeSerializer())
            .build());

    final ConfigNode<Difficulty> DIFFICULTY = node(ConfigNode.builder("difficulty", Difficulty.class)
            .defaultValue(Difficulty.NORMAL)
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                world.getBukkitWorld().peek(world -> world.setDifficulty(newValue));
            })
            .build());

    final ConfigNode<Boolean> ENTRY_FEE_ENABLED = node(ConfigNode.builder("entry-fee.enabled", Boolean.class)
            .defaultValue(false)
            .name("entryfee-enabled")
            .build());

    final ConfigNode<Double> ENTRY_FEE_AMOUNT = node(ConfigNode.builder("entry-fee.amount", Double.class)
            .defaultValue(0.0)
            .name("entryfee-amount")
            .build());

    final ConfigNode<Material> ENTRY_FEE_CURRENCY = node(ConfigNode.builder("entry-fee.currency", Material.class)
            .defaultValue(MVEconomist.VAULT_ECONOMY_MATERIAL)
            .name("entryfee-currency")
            .serializer(new CurrencySerializer())
            .build());

    final ConfigNode<World.Environment> ENVIRONMENT = node(ConfigNode
            .builder("environment", World.Environment.class)
            .defaultValue(World.Environment.NORMAL)
            .name(null)
            .build());

    final ConfigNode<GameMode> GAMEMODE = node(ConfigNode.builder("gamemode", GameMode.class)
            .defaultValue(GameMode.SURVIVAL)
            .onSetValue((oldValue, newValue) -> {
                if (world == null) return;
                enforcementHandler.handleAllGameModeEnforcement(world);
            })
            .build());

    final ConfigNode<String> GENERATOR = node(ConfigNode.builder("generator", String.class)
            // this should be set on world creation, if @error is shown in config, something went wrong
            .defaultValue("@error")
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
            .suggester(input -> {
                if (worldManager == null) return Collections.emptyList();
                //todo: maybe suggest alias? based on resolve-alias-name config
                return worldManager.getWorlds().stream().map(MultiverseWorld::getName).toList();
            })
            .build());

    final ConfigNode<Double> SCALE = node(ConfigNode.builder("scale", Double.class)
            .defaultValue(() -> {
                if (world == null) return 1.0;
                return switch (world.getEnvironment()) {
                    case NETHER -> 8.0;
                    case THE_END -> 16.0;
                    default -> 1.0;
                };
            })
            .build());

    final ConfigNode<Long> SEED = node(ConfigNode.builder("seed", Long.class)
            .defaultValue(Long.MIN_VALUE)
            .name(null)
            .build());

    final ConfigNode<SpawnLocation> SPAWN_LOCATION = node(ConfigNode.builder("spawn-location", SpawnLocation.class)
            .defaultValue(NullLocation.get())
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

    final ListConfigNode<String> SPAWNING_ANIMALS_EXCEPTIONS = node(ListConfigNode
            .listBuilder("spawning.animals.exceptions", String.class)
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

    final ListConfigNode<String> SPAWNING_MONSTERS_EXCEPTIONS = node(ListConfigNode
            .listBuilder("spawning.monsters.exceptions", String.class)
            .defaultValue(new ArrayList<>())
            .name("spawning-monsters-exceptions")
            .build());

    final ListConfigNode<String> WORLD_BLACKLIST = node(ListConfigNode.listBuilder("world-blacklist", String.class)
            .build());

    final ConfigNode<Double> VERSION = node(ConfigNode.builder("version", Double.class)
            .defaultValue(CONFIG_VERSION)
            .name(null)
            .build());

    // END CHECKSTYLE-SUPPRESSION: Javadoc
    // END CHECKSTYLE-SUPPRESSION: MemberName
    // END CHECKSTYLE-SUPPRESSION: Abbreviation
    // END CHECKSTYLE-SUPPRESSION: VisibilityModifier
}
