package org.mvplugins.multiverse.core.world.helpers;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helps to find the overworld world from a nether or end world and vice versa.
 */
@Service
public final class DimensionFinder {

    private final CoreConfig config;
    private final WorldManager worldManager;

    @Inject
    DimensionFinder(@NotNull CoreConfig config, @NotNull WorldManager worldManager) {
        this.config = config;
        this.worldManager = worldManager;
    }

    /**
     * Gets whether a world is the overworld, i.e. the environment is NORMAL.
     *
     * @param world target world to check
     * @return true if the world is an overworld
     */
    public boolean isOverworld(MultiverseWorld world) {
        return world.getEnvironment() == Environment.NORMAL;
    }

    /**
     * Gets whether a world is the nether, i.e. the environment is NETHER.
     *
     * @param world target world to check
     * @return true if the world is a nether
     */
    public boolean isNether(MultiverseWorld world) {
        return world.getEnvironment() == Environment.NETHER;
    }

    /**
     * Gets whether a world is the end, i.e. the environment is THE_END
     *
     * @param world target world to check
     * @return true if the world is an end
     */
    public boolean isEnd(MultiverseWorld world) {
        return world.getEnvironment() == Environment.THE_END;
    }

    /**
     * Gets the Multiverse overworld world from a nether or end world
     *
     * @param world target world
     * @return The overworld multiverse world if exist
     */
    public Option<MultiverseWorld> getOverworldWorld(MultiverseWorld world) {
        if (isOverworld(world)) {
            return Option.of(world);
        }
        if (isNether(world)) {
            return getOverworldNameFromNether(world.getName()).flatMap(worldManager::getWorld);
        }
        if (isEnd(world)) {
            return getOverworldNameFromEnd(world.getName()).flatMap(worldManager::getWorld);
        }
        return Option.none();
    }

    /**
     * Gets the Multiverse nether world from an overworld or end world
     *
     * @param world target world
     * @return The nether multiverse world if exist
     */
    public Option<MultiverseWorld> getNetherWorld(MultiverseWorld world) {
        if (isOverworld(world)) {
            return worldManager.getWorld(getNetherNameFromOverworld(world.getName()));
        }
        if (isNether(world)) {
            return Option.of(world);
        }
        if (isEnd(world)) {
            return getOverworldNameFromEnd(world.getName())
                    .map(this::getNetherNameFromOverworld)
                    .flatMap(worldManager::getWorld);
        }
        return Option.none();
    }

    /**
     * Gets the Multiverse end world from an overworld or nether world
     *
     * @param world target world
     * @return The end multiverse world if exist
     */
    public Option<MultiverseWorld> getEndWorld(MultiverseWorld world) {
        if (isOverworld(world)) {
            return worldManager.getWorld(getEndNameFromOverworld(world.getName()));
        }
        if (isNether(world)) {
            return getOverworldNameFromNether(world.getName())
                    .map(this::getEndNameFromOverworld)
                    .flatMap(worldManager::getWorld);
        }
        if (isEnd(world)) {
            return Option.of(world);
        }
        return Option.none();
    }

    private Option<String> getOverworldNameFromNether(String worldName) {
        return config.getNetherWorldNameFormat().getOverworldFromName(worldName);
    }

    private Option<String> getOverworldNameFromEnd(String worldName) {
        return config.getEndWorldNameFormat().getOverworldFromName(worldName);
    }

    private @NotNull String getNetherNameFromOverworld(String worldName) {
        return config.getNetherWorldNameFormat().replaceOverworld(worldName);
    }

    private @NotNull String getEndNameFromOverworld(String worldName) {
        return config.getEndWorldNameFormat().replaceOverworld(worldName);
    }

    /**
     * Represents a dimension name format, used to extract the overworld world name from a nether or end world name.
     */
    public static final class DimensionFormat {

        public static final String OVERWORLD_PLACEHOLDER = "%overworld%";

        private final String format;
        private final Pattern pattern;

        public DimensionFormat(@NotNull String format) {
            this.format = format;
            if (!format.contains(OVERWORLD_PLACEHOLDER)) {
                throw new IllegalArgumentException("DimensionRegex must contain overworld placeholder: " + OVERWORLD_PLACEHOLDER);
            }
            this.pattern = Pattern.compile("^" + format.replace(OVERWORLD_PLACEHOLDER, "(?<worldname>.+)") + "$");
        }

        /**
         * Extract the overworld world name from a nether or end world name
         *
         * @param worldName The nether or end world name
         * @return The overworld world name
         */
        public Option<String> getOverworldFromName(String worldName) {
            Matcher matcher = pattern.matcher(worldName);
            if (!matcher.matches()) {
                Logging.finer("%s does not match %s", worldName, format);
                return Option.none();
            }
            return Option.of(matcher.group("worldname"));
        }

        /**
         * Parse the dimension name with the overworld world name
         *
         * @param worldName The overworld name
         * @return The parsed dimension name
         */
        public String replaceOverworld(String worldName) {
            return format.replace(OVERWORLD_PLACEHOLDER, worldName);
        }

        /**
         * The format used to format the dimension's name
         *
         * @return the format
         */
        public @NotNull String getFormat() {
            return format;
        }
    }
}
