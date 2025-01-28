package org.mvplugins.multiverse.core.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.configuration.functions.NodeSerializer;
import org.mvplugins.multiverse.core.event.MVWorldPropertyChangeEvent;
import org.mvplugins.multiverse.core.configuration.node.ConfigNode;
import org.mvplugins.multiverse.core.configuration.node.ListConfigNode;
import org.mvplugins.multiverse.core.configuration.node.NodeGroup;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.utils.MaterialConverter;
import org.mvplugins.multiverse.core.world.helpers.EnforcementHandler;
import org.mvplugins.multiverse.core.world.location.NullLocation;
import org.mvplugins.multiverse.core.world.location.SpawnLocation;

/**
 * Represents nodes in a world configuration.
 */
final class WorldConfigNodes {
    private static final double CONFIG_VERSION = 1.0;

    private final NodeGroup nodes = new NodeGroup();
    private WorldManager worldManager;
    private EnforcementHandler enforcementHandler;
    private MultiverseWorld world = null;

    WorldConfigNodes(@NotNull MultiverseCore multiverseCore) {
        this.worldManager = multiverseCore.getServiceLocator().getService(WorldManager.class);
        this.enforcementHandler = multiverseCore.getServiceLocator().getService(EnforcementHandler.class);
    }

    MultiverseWorld getWorld() {
        return world;
    }

    void setWorld(MultiverseWorld world) {
        this.world = world;
    }

    NodeGroup getNodes() {
        return nodes;
    }

    private <T> ConfigNode<T> node(ConfigNode.Builder<T, ?> nodeBuilder) {
        nodeBuilder.onSetValue((oldValue, newValue) -> {
            if (Objects.equals(oldValue, newValue)) return;
            if (world == null) return;
            MVWorldPropertyChangeEvent<?> mvWorldPropertyChangeEvent = new MVWorldPropertyChangeEvent<>(
                    world, Option.of(nodeBuilder.name()).getOrElse(nodeBuilder.path()), oldValue, newValue);
            Bukkit.getPluginManager().callEvent(mvWorldPropertyChangeEvent);
            Logging.finer("MVWorldPropertyChangeEvent fired for world '%s' with name '%s' and value '%s'",
                    world.getName(), nodeBuilder.path(), newValue);
        });

        ConfigNode<T> node = nodeBuilder.build();
        nodes.add(node);
        return node;
    }

    // BEGIN CHECKSTYLE-SUPPRESSION: Javadoc
    // BEGIN CHECKSTYLE-SUPPRESSION: VisibilityModifier

    final ConfigNode<Boolean> adjustSpawn = node(ConfigNode.builder("adjust-spawn", Boolean.class)
            .defaultValue(false));

    final ConfigNode<String> alias = node(ConfigNode.builder("alias", String.class)
            .defaultValue(""));

    final ConfigNode<Boolean> allowFlight = node(ConfigNode.builder("allow-flight", Boolean.class)
            .defaultValue(false)
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                enforcementHandler.handleAllFlightEnforcement(loadedWorld);
            }));

    final ConfigNode<Boolean> allowWeather = node(ConfigNode.builder("allow-weather", Boolean.class)
            .defaultValue(true)
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                loadedWorld.getBukkitWorld().peek(world -> {
                    if (!world.isClearWeather() && !newValue) {
                        world.setThundering(false);
                        world.setStorm(false);
                    }
                });
            }));

    final ConfigNode<Boolean> anchorRespawn = node(ConfigNode.builder("anchor-respawn", Boolean.class)
            .defaultValue(true));

    final ConfigNode<Boolean> autoHeal = node(ConfigNode.builder("auto-heal", Boolean.class)
            .defaultValue(true));

    final ConfigNode<Boolean> autoLoad = node(ConfigNode.builder("auto-load", Boolean.class)
            .defaultValue(true));

    final ConfigNode<Boolean> bedRespawn = node(ConfigNode.builder("bed-respawn", Boolean.class)
            .defaultValue(true));

    final ConfigNode<String> biome = node(ConfigNode.builder("biome", String.class)
            .defaultValue("@error")
            .name(null));

    final ConfigNode<Difficulty> difficulty = node(ConfigNode.builder("difficulty", Difficulty.class)
            .defaultValue(Difficulty.NORMAL)
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                loadedWorld.getBukkitWorld().peek(bukkitWorld -> bukkitWorld.setDifficulty(newValue));
            }));

    final ConfigNode<Boolean> entryFeeEnabled = node(ConfigNode.builder("entry-fee.enabled", Boolean.class)
            .defaultValue(false)
            .name("entryfee-enabled"));

    final ConfigNode<Double> entryFeeAmount = node(ConfigNode.builder("entry-fee.amount", Double.class)
            .defaultValue(0.0)
            .name("entryfee-amount"));

    final ConfigNode<Material> entryFeeCurrency = node(ConfigNode.builder("entry-fee.currency", Material.class)
            .defaultValue(MVEconomist.VAULT_ECONOMY_MATERIAL)
            .name("entryfee-currency")
            .serializer(new NodeSerializer<>() {
                @Override
                public Material deserialize(Object object, Class<Material> type) {
                    return Option.of(object)
                            .map(String::valueOf)
                            .map(materialStr -> {
                                if (materialStr.equalsIgnoreCase(MVEconomist.VAULT_ECONOMY_CODE)) {
                                    return MVEconomist.VAULT_ECONOMY_MATERIAL;
                                }
                                return MaterialConverter.stringToMaterial(materialStr);
                            })
                            .getOrElse(MVEconomist.VAULT_ECONOMY_MATERIAL);
                }

                @Override
                public Object serialize(Material object, Class<Material> type) {
                    return Option.of(object)
                            .map(material -> material == MVEconomist.VAULT_ECONOMY_MATERIAL
                                    ? MVEconomist.VAULT_ECONOMY_CODE
                                    : material.name())
                            .getOrElse(MVEconomist.VAULT_ECONOMY_CODE);
                }
            }));

    final ConfigNode<World.Environment> environment = node(ConfigNode
            .builder("environment", World.Environment.class)
            .defaultValue(World.Environment.NORMAL)
            .name(null));

    final ConfigNode<GameMode> gamemode = node(ConfigNode.builder("gamemode", GameMode.class)
            .defaultValue(GameMode.SURVIVAL)
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                enforcementHandler.handleAllGameModeEnforcement(loadedWorld);
            }));

    final ConfigNode<String> generator = node(ConfigNode.builder("generator", String.class)
            // this should be set on world creation, if @error is shown in config, something went wrong
            .defaultValue("@error")
            .name(null));

    final ConfigNode<Boolean> hidden = node(ConfigNode.builder("hidden", Boolean.class)
            .defaultValue(false));

    final ConfigNode<Boolean> hunger = node(ConfigNode.builder("hunger", Boolean.class)
            .defaultValue(true));

    final ConfigNode<Boolean> keepSpawnInMemory = node(ConfigNode
            .builder("keep-spawn-in-memory", Boolean.class)
            .defaultValue(true)
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                loadedWorld.getBukkitWorld().peek(bukkitWorld -> bukkitWorld.setKeepSpawnInMemory(newValue));
            }));

    final ConfigNode<Integer> playerLimit = node(ConfigNode.builder("player-limit", Integer.class)
            .defaultValue(-1));

    final ConfigNode<AllowedPortalType> portalForm = node(ConfigNode
            .builder("portal-form", AllowedPortalType.class)
            .defaultValue(AllowedPortalType.ALL));

    final ConfigNode<Boolean> pvp = node(ConfigNode.builder("pvp", Boolean.class)
            .defaultValue(true)
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                loadedWorld.getBukkitWorld().peek(bukkitWorld -> bukkitWorld.setPVP(newValue));
            }));

    final ConfigNode<String> respawnWorld = node(ConfigNode.builder("respawn-world", String.class)
            .defaultValue("")
            .suggester(input -> {
                if (worldManager == null) return Collections.emptyList();
                return worldManager.getWorlds().stream().map(MultiverseWorld::getName).toList();
            }));

    final ConfigNode<Double> scale = node(ConfigNode.builder("scale", Double.class)
            .defaultValue(() -> {
                if (world == null) return 1.0;
                return switch (world.getEnvironment()) {
                    case NETHER -> 8.0;
                    case THE_END -> 16.0;
                    default -> 1.0;
                };
            }));

    final ConfigNode<Long> seed = node(ConfigNode.builder("seed", Long.class)
            .defaultValue(Long.MIN_VALUE)
            .name(null));

    final ConfigNode<SpawnLocation> spawnLocation = node(ConfigNode.builder("spawn-location", SpawnLocation.class)
            .defaultValue(NullLocation.get())
            .name(null)
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                if (newValue == null || newValue instanceof NullLocation) return;
                loadedWorld.getBukkitWorld().peek(bukkitWorld -> {
                    bukkitWorld.setSpawnLocation(newValue.getBlockX(), newValue.getBlockY(), newValue.getBlockZ());
                    newValue.setWorld(bukkitWorld);
                });
            }));

    final ConfigNode<Boolean> spawningAnimals = node(ConfigNode.builder("spawning.animals.spawn", Boolean.class)
            .defaultValue(true)
            .name("spawning-animals")
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                loadedWorld.getBukkitWorld().peek(bukkitWorld ->
                        bukkitWorld.setSpawnFlags(bukkitWorld.getAllowMonsters(), newValue));
            }));

    final ConfigNode<Integer> spawningAnimalsTicks = node(ConfigNode
            .builder("spawning.animals.tick-rate", Integer.class)
            .defaultValue(-1)
            .name("spawning-animals-ticks")
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                loadedWorld.getBukkitWorld().peek(bukkitWorld -> bukkitWorld.setTicksPerAnimalSpawns(newValue));
            }));

    final ConfigNode<List<String>> spawningAnimalsExceptions = node(ListConfigNode
            .listBuilder("spawning.animals.exceptions", String.class)
            .defaultValue(new ArrayList<>())
            .name("spawning-animals-exceptions"));

    final ConfigNode<Boolean> spawningMonsters = node(ConfigNode
            .builder("spawning.monsters.spawn", Boolean.class)
            .defaultValue(true)
            .name("spawning-monsters")
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                loadedWorld.getBukkitWorld().peek(bukkitWorld ->
                        bukkitWorld.setSpawnFlags(newValue, bukkitWorld.getAllowAnimals()));
            }));

    final ConfigNode<Integer> spawningMonstersTicks = node(ConfigNode
            .builder("spawning.monsters.tick-rate", Integer.class)
            .defaultValue(-1)
            .name("spawning-monsters-ticks")
            .onSetValue((oldValue, newValue) -> {
                if (!(world instanceof LoadedMultiverseWorld loadedWorld)) return;
                loadedWorld.getBukkitWorld().peek(bukkitWorld -> bukkitWorld.setTicksPerMonsterSpawns(newValue));
            }));

    final ConfigNode<List<String>> spawningMonstersExceptions = node(ListConfigNode
            .listBuilder("spawning.monsters.exceptions", String.class)
            .defaultValue(new ArrayList<>())
            .name("spawning-monsters-exceptions"));

    final ConfigNode<List<String>> worldBlacklist = node(ListConfigNode.listBuilder("world-blacklist", String.class));

    final ConfigNode<Double> version = node(ConfigNode.builder("version", Double.class)
            .defaultValue(CONFIG_VERSION)
            .name(null));

    // END CHECKSTYLE-SUPPRESSION: Javadoc
    // END CHECKSTYLE-SUPPRESSION: VisibilityModifier
}
