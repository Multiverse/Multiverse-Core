package org.mvplugins.multiverse.core.world.helpers;

import java.util.HashMap;
import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.configuration.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

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

    /**
     * A {@link DataStore} for storing and restoring game rules for a multiverse world.
     */
    class GameRulesStore implements DataStore<LoadedMultiverseWorld> {
        private Map<GameRule<?>, Object> gameRuleMap;

        /**
         * {@inheritDoc}
         */
        @Override
        public GameRulesStore copyFrom(LoadedMultiverseWorld world) {
            this.gameRuleMap = new HashMap<>();
            world.getBukkitWorld().peek(bukkitWorld -> {
                for (String gameRule : bukkitWorld.getGameRules()) {
                    GameRule<?> gameRuleEnum = GameRule.getByName(gameRule);
                    if (gameRuleEnum == null) {
                        continue;
                    }
                    gameRuleMap.put(gameRuleEnum, bukkitWorld.getGameRuleValue(gameRuleEnum));
                }
            });
            Logging.finer("Copied " + gameRuleMap.size() + " game rules from world " + world.getName());
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public GameRulesStore pasteTo(LoadedMultiverseWorld world) {
            if (gameRuleMap == null) {
                return this;
            }
            world.getBukkitWorld().peek(bukkitWorld -> {
                for (String gameRule : bukkitWorld.getGameRules()) {
                    GameRule<?> gameRuleEnum = GameRule.getByName(gameRule);
                    if (gameRuleEnum == null) {
                        continue;
                    }
                    setGameRuleValue(bukkitWorld, gameRuleEnum, gameRuleMap.get(gameRuleEnum)).onFailure(e -> {
                        Logging.warning("Failed to set game rule " + gameRuleEnum.getName() + " to " + gameRuleMap.get(gameRuleEnum));
                        e.printStackTrace();
                    });
                }
            });
            return this;
        }

        private <T> Try<Void> setGameRuleValue(World world, GameRule<T> gameRule, Object value) {
            return Try.run(() -> world.setGameRule(gameRule, (T) value));
        }
    }

    /**
     * A {@link DataStore} for storing and restoring world properties for a multiverse world.
     */
    class WorldConfigStore implements DataStore<LoadedMultiverseWorld> {
        private Map<String, Object> configMap;

        /**
         * {@inheritDoc}
         */
        @Override
        public WorldConfigStore copyFrom(LoadedMultiverseWorld world) {
            this.configMap = new HashMap<>();
            StringPropertyHandle worldPropertyHandler = world.getStringPropertyHandle();
            worldPropertyHandler.getAllPropertyNames().forEach(name -> worldPropertyHandler.getProperty(name)
                    .peek(value -> configMap.put(name, value)).onFailure(e -> {
                        Logging.warning("Failed to get property " + name + " from world "
                                + world.getName() + ": " + e.getMessage());
                    }));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WorldConfigStore pasteTo(LoadedMultiverseWorld world) {
            if (configMap == null) {
                return this;
            }
            StringPropertyHandle worldPropertyHandler = world.getStringPropertyHandle();
            configMap.forEach((name, value) -> worldPropertyHandler.setProperty(name, value).onFailure(e -> {
                Logging.warning("Failed to set property %s to %s for world %s: %s",
                        name, value, world.getName(), e.getMessage());
            }));
            return this;
        }
    }

    /**
     * A {@link DataStore} for storing and restoring world border properties for a multiverse world.
     */
    class WorldBorderStore implements DataStore<LoadedMultiverseWorld> {
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
        public WorldBorderStore copyFrom(LoadedMultiverseWorld world) {
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
        public WorldBorderStore pasteTo(LoadedMultiverseWorld world) {
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
