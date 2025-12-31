package org.mvplugins.multiverse.core;

import com.google.common.collect.Lists;
import io.vavr.control.Option;
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

import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.utils.MinecraftTimeFormatter;
import org.mvplugins.multiverse.core.utils.REPatterns;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.List;
import java.util.Locale;

@Service
final class PlaceholderExpansionHook extends PlaceholderExpansion {

    private final MultiverseCore plugin;
    private final CoreConfig coreConfig;
    private final WorldManager worldManager;
    private final MVEconomist economist;

    @Inject
    public PlaceholderExpansionHook(@NotNull MultiverseCore plugin,
                                    @NotNull CoreConfig coreConfig,
                                    @NotNull WorldManager worldManager,
                                    @NotNull MVEconomist economist) {
        this.plugin = plugin;
        this.coreConfig = coreConfig;
        this.worldManager = worldManager;
        this.economist = economist;
    }

    @PostConstruct
    @Override
    public boolean register() {
        return super.register();
    }

    @Override
    public void warning(String msg) {
        if (coreConfig.getWarnInvalidPapiFormat()) {
            super.warning(msg);
        }
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

        final var placeholder = paramsArray.remove(0).toLowerCase(Locale.ENGLISH);

        return parseWorldName(offlinePlayer, paramsArray)
                .flatMap(worldName -> worldManager.getWorld(worldName)
                    .onEmpty(() -> warning("Multiverse World not found: " + worldName)))
                .flatMap(world -> world.asLoadedWorld()
                        .flatMap(loadedWorld -> getLoadedWorldPlaceHolderValue(placeholder, paramsArray, loadedWorld))
                        .orElse(() -> getWorldPlaceHolderValue(placeholder, paramsArray, world)))
                .getOrElse(() -> coreConfig.getInvalidPapiFormatReturnsBlank() ? "" : null);
    }

    private @NotNull Option<String> parseWorldName(OfflinePlayer offlinePlayer, List<String> paramsArray) {
        // No world defined, get from player
        if (paramsArray.isEmpty()) {
            if (offlinePlayer instanceof Player player) {
                return Option.of(player.getWorld().getName());
            } else {
                warning("You must specify a world name for non-player placeholders");
                return null;
            }
        }

        // Try get from params
        String paramWorldName = paramsArray.get(paramsArray.size() - 1);
        if (worldManager.isWorld(paramWorldName)) {
            paramsArray.remove(paramsArray.size() - 1);
            return Option.of(paramWorldName);
        }

        // Param not a world, fallback to player
        if (offlinePlayer instanceof Player player) {
            return Option.of(player.getWorld().getName());
        }
        warning("Multiverse World not found: " + paramWorldName);
        return Option.none();
    }

    private @NotNull Option<String> getWorldPlaceHolderValue(@NotNull String placeholder,
                                                             @NotNull List<String> placeholderParams,
                                                             @NotNull MultiverseWorld world) {
        return Option.of(switch (placeholder) {
            case "alias" -> world.getAliasOrName();
            case "animalspawn" -> String.valueOf(world.getEntitySpawnConfig()
                    .getSpawnCategoryConfig(SpawnCategory.ANIMAL)
                    .isSpawn());
            case "autoheal" -> String.valueOf(world.getAutoHeal());
            case "blacklist" -> String.join(", ", world.getWorldBlacklist());
            case "currency" -> String.valueOf(world.getCurrency());
            case "difficulty" -> world.getDifficulty().toString();
            case "entryfee" -> economist.formatPrice(world.getPrice(), world.getCurrency());
            case "environment" -> world.getEnvironment().toString().toLowerCase();
            case "flight" -> String.valueOf(world.isAllowFlight());
            case "gamemode" -> world.getGameMode().toString().toLowerCase();
            case "generator" -> world.getGenerator();
            case "hunger" -> String.valueOf(world.isHunger());
            case "monstersspawn" -> String.valueOf(world.getEntitySpawnConfig()
                    .getSpawnCategoryConfig(SpawnCategory.MONSTER)
                    .isSpawn());
            case "name" -> world.getName();
            case "playerlimit" -> String.valueOf(world.getPlayerLimit());
            case "price" -> String.valueOf(world.getPrice());
            case "pvp" -> String.valueOf(world.getPvp());
            case "seed" -> String.valueOf(world.getSeed());
            case "weather" -> String.valueOf(world.isAllowWeather());
            case "playercount", "time", "type" -> {
                warning("Placeholder '" + placeholder + "' is only available for loaded worlds.");
                yield null;
            }
            default -> {
                warning("Unknown Placeholder: " + placeholder);
                yield null;
            }
        });
    }

    private @NotNull Option<String> getLoadedWorldPlaceHolderValue(@NotNull String placeholder,
                                                                   @NotNull List<String> placeholderParams,
                                                                   @NotNull LoadedMultiverseWorld world) {
        return Option.of(switch (placeholder) {
            case "playercount" -> String.valueOf(world.getBukkitWorld()
                    .map(World::getPlayers)
                    .map(List::size)
                    .getOrElse(-1));
            case "time" -> {
                String timeFormat = !placeholderParams.isEmpty() ? placeholderParams.get(0) : "";
                long time = world.getBukkitWorld().map(World::getTime).getOrElse(0L);
                yield switch (timeFormat) {
                    case "" -> String.valueOf(time);
                    case "12h" -> MinecraftTimeFormatter.format12h(time);
                    case "24h" -> MinecraftTimeFormatter.format24h(time);
                    default -> MinecraftTimeFormatter.formatTime(time, timeFormat);
                };
            }
            case "type" -> world.getBukkitWorld().map(World::getWorldType).map(Enum::name).getOrElse("null");
            default -> null;
        });
    }
}
