package com.onarandombox.MultiverseCore.worldnew.helpers;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.worldnew.MVWorld;
import com.onarandombox.MultiverseCore.worldnew.OfflineWorld;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jvnet.hk2.annotations.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public interface DataStore<T> {
    DataStore<T> copyFrom(T object);
    DataStore<T> pasteTo(T object);

    class GameRulesStore implements DataStore<MVWorld> {
        public static GameRulesStore createAndCopyFrom(MVWorld world) {
            return new GameRulesStore().copyFrom(world);
        }

        private Map<GameRule<?>, Object> gameRuleMap;

        public GameRulesStore copyFrom(MVWorld world) {
            this.gameRuleMap = new HashMap<>();
            world.getBukkitWorld().peek(bukkitWorld -> {
                Arrays.stream(GameRule.values()).forEach(gameRule -> {
                    Object value = bukkitWorld.getGameRuleValue(gameRule);
                    gameRuleMap.put(gameRule, value);
                });
            });
            return this;
        }

        public GameRulesStore pasteTo(MVWorld world) {
            if (gameRuleMap == null) {
                return this;
            }
            world.getBukkitWorld().peek(bukkitWorld -> {
                gameRuleMap.forEach((gameRule, value) -> {
                    if (!setGameRuleValue(bukkitWorld, gameRule, value)) {
                        Logging.warning("Failed to set game rule " + gameRule.getName() + " to " + value);
                    }
                });
            });
            return this;
        }

        private <T> boolean setGameRuleValue(World world, GameRule<T> gameRule, Object value) {
            try {
                return world.setGameRule(gameRule, (T) value);
            } catch (Exception e) {
                Logging.fine(e.getMessage());
                return false;
            }
        }
    }

    class WorldConfigStore implements DataStore<OfflineWorld> {
        public static WorldConfigStore createAndCopyFrom(OfflineWorld world) {
            return new WorldConfigStore().copyFrom(world);
        }

        private Map<String, Object> configMap;

        @Override
        public WorldConfigStore copyFrom(OfflineWorld world) {
            this.configMap = new HashMap<>();
            world.getConfigurablePropertyNames().forEach(name -> {
                world.getProperty(name).peek(value -> configMap.put(name, value)).onFailure(e -> {
                    Logging.warning("Failed to get property " + name + " from world " + world.getName() + ": " + e.getMessage());
                });
            });
            return this;
        }

        @Override
        public WorldConfigStore pasteTo(OfflineWorld world) {
            if (configMap == null) {
                return this;
            }
            configMap.forEach((name, value) -> {
                world.setProperty(name, value).onFailure(e -> {
                    Logging.warning("Failed to set property %s to %s for world %s: %s",
                            name, value, world.getName(), e.getMessage());
                });
            });
            return this;
        }
    }
}
