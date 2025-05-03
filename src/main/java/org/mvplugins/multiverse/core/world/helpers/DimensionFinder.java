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

@Service
public final class DimensionFinder {

    private final CoreConfig config;
    private final WorldManager worldManager;

    @Inject
    DimensionFinder(@NotNull CoreConfig config, @NotNull WorldManager worldManager) {
        this.config = config;
        this.worldManager = worldManager;
    }

    public boolean isOverworld(MultiverseWorld world) {
        return world.getEnvironment() == Environment.NORMAL;
    }

    public boolean isNether(MultiverseWorld world) {
        return world.getEnvironment() == Environment.NETHER;
    }

    public boolean isEnd(MultiverseWorld world) {
        return world.getEnvironment() == Environment.THE_END;
    }

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

        public Option<String> getOverworldFromName(String worldName) {
            Matcher matcher = pattern.matcher(worldName);
            if (!matcher.matches()) {
                Logging.finer("%s does not match %s", worldName, format);
                return Option.none();
            }
            return Option.of(matcher.group("worldname"));
        }

        public String replaceOverworld(String worldName) {
            return format.replace(OVERWORLD_PLACEHOLDER, worldName);
        }

        public @NotNull String getFormat() {
            return format;
        }
    }
}
