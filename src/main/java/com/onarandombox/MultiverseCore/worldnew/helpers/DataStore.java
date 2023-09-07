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

/**
 * A data store for storing and restoring data from an object.
 *
 * @param <T>   The type of the object to store data from.
 */
@Service
public interface DataStore<T> {
    /**
     * Stores the data from the given object in this {@link DataStore} instance.
     *
     * @param object    The object to copy data from.
     * @return This {@link DataStore} instance.
     */
    DataStore<T> copyFrom(T object);

    /**
     * Copies the data from this {@link DataStore} instance to the given object.
     *
     * @param object    The object to paste data to.
     * @return This {@link DataStore} instance.
     */
    DataStore<T> pasteTo(T object);

    class GameRulesStore implements DataStore<MVWorld> {
        private Map<GameRule<?>, Object> gameRuleMap;

        /**
         * {@inheritDoc}
         */
        @Override
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

        /**
         * {@inheritDoc}
         */
        @Override
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

    class WorldConfigStore implements DataStore<MVWorld> {
        private Map<String, Object> configMap;

        /**
         * {@inheritDoc}
         */
        @Override
        public WorldConfigStore copyFrom(MVWorld world) {
            this.configMap = new HashMap<>();
            world.getConfigurablePropertyNames().forEach(name -> {
                world.getProperty(name).peek(value -> configMap.put(name, value)).onFailure(e -> {
                    Logging.warning("Failed to get property " + name + " from world " + world.getName() + ": " + e.getMessage());
                });
            });
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WorldConfigStore pasteTo(MVWorld world) {
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

    class WorldBorderStore implements DataStore<MVWorld> {
        private double borderCenterX;
        private double borderCenterZ;
        private double borderDamageAmount;
        private double borderDamageBuffer;
        private double borderSize;
        private int borderTimeRemaining;

        /**
         * {@inheritDoc}
         */
        @Override
        public WorldBorderStore copyFrom(MVWorld world) {
            world.getBukkitWorld().peek(bukkitWorld -> {
                borderCenterX = bukkitWorld.getWorldBorder().getCenter().getX();
                borderCenterZ = bukkitWorld.getWorldBorder().getCenter().getZ();
                borderDamageAmount = bukkitWorld.getWorldBorder().getDamageAmount();
                borderDamageBuffer = bukkitWorld.getWorldBorder().getDamageBuffer();
                borderSize = bukkitWorld.getWorldBorder().getSize();
                borderTimeRemaining = bukkitWorld.getWorldBorder().getWarningTime();
            });
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WorldBorderStore pasteTo(MVWorld world) {
            world.getBukkitWorld().peek(bukkitWorld -> {
                bukkitWorld.getWorldBorder().setCenter(borderCenterX, borderCenterZ);
                bukkitWorld.getWorldBorder().setDamageAmount(borderDamageAmount);
                bukkitWorld.getWorldBorder().setDamageBuffer(borderDamageBuffer);
                bukkitWorld.getWorldBorder().setSize(borderSize);
                bukkitWorld.getWorldBorder().setWarningTime(borderTimeRemaining);
            });
            return this;
        }
    }
}
