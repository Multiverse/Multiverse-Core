package org.mvplugins.multiverse.core;

import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.utils.MinecraftTimeFormatter;
import org.mvplugins.multiverse.core.utils.REPatterns;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.List;
import java.util.Locale;

@Service
final class PlaceholderExpansionHook extends PlaceholderExpansion {

    private final MultiverseCore plugin;
    private final WorldManager worldManager;
    private final MVEconomist economist;

    @Inject
    public PlaceholderExpansionHook(MultiverseCore plugin, WorldManager worldManager, MVEconomist economist) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.economist = economist;
    }

    @PostConstruct
    @Override
    public boolean register() {
        return super.register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "multiverse-core";
    }

    @Override
    public @NotNull String getAuthor() {
        return StringFormatter.joinAnd(plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Placeholder implementation, format: %multiverse-core_&lt;placeholder&gt;_[world]% world is optional.
     *
     * @param offlinePlayer Player to get the placeholder for
     * @param params        Placeholder to get
     * @return Placeholder value
     */
    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        // Split string in to an Array with underscores
        List<String> paramsArray = Lists.newArrayList(REPatterns.UNDERSCORE.split(params));

        // No placeholder defined
        if (paramsArray.isEmpty()) {
            warning("No placeholder key defined");
            return null;
        }

        final var placeholder = paramsArray.remove(0);

        String worldName = parseWorldName(offlinePlayer, paramsArray);
        if (worldName == null) return null;

        return worldManager.getLoadedWorld(worldName)
                .onEmpty(() -> warning("Multiverse World not found: " + worldName))
                .map(world -> getWorldPlaceHolderValue(placeholder, paramsArray, world))
                .getOrNull();
    }

    private @Nullable String parseWorldName(OfflinePlayer offlinePlayer, List<String> paramsArray) {
        // No world defined, get from player
        if (paramsArray.isEmpty()) {
            if (offlinePlayer instanceof Player player) {
                return player.getWorld().getName();
            } else {
                warning("You must specify a world name for non-player placeholders");
                return null;
            }
        }

        // Try get from params
        String paramWorldName = paramsArray.get(paramsArray.size() - 1);
        if (worldManager.isLoadedWorld(paramWorldName)) {
            paramsArray.remove(paramsArray.size() - 1);
            return paramWorldName;
        }

        // Param not a world, fallback to player
        if (offlinePlayer instanceof Player player) {
            return player.getWorld().getName();
        }
        warning("Multiverse World not found: " + paramWorldName);
        return null;
    }

    private @Nullable String getWorldPlaceHolderValue(@NotNull String placeholder,
                                                      @NotNull List<String> placeholderParams,
                                                      @NotNull LoadedMultiverseWorld world) {
        // Switch to find what specific placeholder we want
        switch (placeholder.toLowerCase(Locale.ENGLISH)) {
            case "alias" -> {
                return world.getAliasOrName();
            }
            case "animalspawn" -> {
                return String.valueOf(world.getEntitySpawnConfig().getSpawnCategoryConfig(SpawnCategory.ANIMAL).isSpawn());
            }
            case "autoheal" -> {
                return String.valueOf(world.getAutoHeal());
            }
            case "blacklist" -> {
                return String.join(", ", world.getWorldBlacklist());
            }
            case "currency" -> {
                return String.valueOf(world.getCurrency());
            }
            case "difficulty" -> {
                return world.getDifficulty().toString();
            }
            case "entryfee" -> {
                return economist.formatPrice(world.getPrice(), world.getCurrency());
            }
            case "environment" -> {
                return world.getEnvironment().toString().toLowerCase();
            }
            case "flight" -> {
                return String.valueOf(world.isAllowFlight());
            }
            case "gamemode" -> {
                return world.getGameMode().toString().toLowerCase();
            }
            case "generator" -> {
                return world.getGenerator();
            }
            case "hunger" -> {
                return String.valueOf(world.isHunger());
            }
            case "monstersspawn" -> {
                return String.valueOf(world.getEntitySpawnConfig().getSpawnCategoryConfig(SpawnCategory.MONSTER).isSpawn());
            }
            case "name" -> {
                return world.getName();
            }
            case "playercount" -> {
                return String.valueOf(world.getBukkitWorld()
                        .map(World::getPlayers)
                        .map(List::size)
                        .getOrElse(-1));
            }
            case "playerlimit" -> {
                return String.valueOf(world.getPlayerLimit());
            }
            case "price" -> {
                return String.valueOf(world.getPrice());
            }
            case "pvp" -> {
                return String.valueOf(world.getPvp());
            }
            case "seed" -> {
                return String.valueOf(world.getSeed());
            }
            case "time" -> {
                String timeFormat = !placeholderParams.isEmpty() ? placeholderParams.get(0) : "";
                long time = world.getBukkitWorld().map(World::getTime).getOrElse(0L);
                switch (timeFormat) {
                    case "" -> {
                        return String.valueOf(time);
                    }
                    case "12h" -> {
                        return MinecraftTimeFormatter.format12h(time);
                    }
                    case "24h" -> {
                        return MinecraftTimeFormatter.format24h(time);
                    }
                    default -> {
                        return MinecraftTimeFormatter.formatTime(time, timeFormat);
                    }
                }
            }
            case "type" -> {
                return world.getBukkitWorld().map(World::getWorldType).map(Enum::name).getOrElse("null");
            }
            case "weather" -> {
                return String.valueOf(world.isAllowWeather());
            }
            default -> {
                warning("Unknown placeholder: " + placeholder);
                return null;
            }
        }
    }
}
