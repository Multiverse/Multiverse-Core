package org.mvplugins.multiverse.core;

import io.vavr.control.Option;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

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
        String[] paramsArray = params.split("_", 2);

        // No placeholder defined
        if (paramsArray.length < 1) {
            warning("No placeholder defined");
            return null;
        }

        final var placeholder = paramsArray[0];
        Option<LoadedMultiverseWorld> targetWorld;

        // If no world is defined, use the player's world
        if (paramsArray.length == 1) {
            if (!offlinePlayer.isOnline()) {
                return null;
            }
            targetWorld = worldManager.getLoadedWorld(((Player)offlinePlayer).getWorld());
        } else {
            targetWorld = worldManager.getLoadedWorld(paramsArray[1]);
        }

        // Fail if world is null
        return targetWorld.map(world -> getWorldPlaceHolderValue(placeholder, world)).getOrNull();
    }

    private @Nullable String getWorldPlaceHolderValue(@NotNull String placeholder, @NotNull LoadedMultiverseWorld world) {
        // Switch to find what specific placeholder we want
        switch (placeholder.toLowerCase()) {
            case "alias" -> {
                return world.getAlias();
            }
            case "animalspawn" -> {
                return String.valueOf(world.getSpawningAnimals());
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
                return String.valueOf(world.getAllowFlight());
            }
            case "gamemode" -> {
                return world.getGameMode().toString().toLowerCase();
            }
            case "generator" -> {
                return world.getGenerator();
            }
            case "hunger" -> {
                return String.valueOf(world.getHunger());
            }
            case "monstersspawn" -> {
                return String.valueOf(world.getSpawningMonsters());
            }
            case "name" -> {
                return world.getName();
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
            case "type" -> {
                return world.getBukkitWorld().map(World::getWorldType).map(Enum::name).getOrElse("null");
            }
            case "weather" -> {
                return String.valueOf(world.getAllowWeather());
            }
            default -> {
                warning("Unknown placeholder: " + placeholder);
                return null;
            }
        }
    }
}
