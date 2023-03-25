package com.onarandombox.MultiverseCore.placeholders;

import java.util.Optional;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.inject.EagerlyLoaded;
import jakarta.inject.Inject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

@Service
public class MultiverseCorePlaceholders extends PlaceholderExpansion implements EagerlyLoaded {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private final MVEconomist economist;

    @Inject
    public MultiverseCorePlaceholders(MultiverseCore plugin, MVWorldManager worldManager, MVEconomist economist) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.economist = economist;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "multiverse-core";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getAuthors();
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
        Optional<MVWorld> targetWorld;

        // If no world is defined, use the player's world
        if (paramsArray.length == 1) {
            if (!offlinePlayer.isOnline()) {
                return null;
            }
            targetWorld = Optional.ofNullable(worldManager.getMVWorld(((Player)offlinePlayer).getWorld()));
        } else {
            targetWorld = Optional.ofNullable(worldManager.getMVWorld(paramsArray[1]));
        }

        // Fail if world is null
        return targetWorld.map(world -> getWorldPlaceHolderValue(placeholder, world))
                .orElse(null);
    }

    private @Nullable String getWorldPlaceHolderValue(@NotNull String placeholder, @NotNull MVWorld world) {
        // Switch to find what specific placeholder we want
        switch (placeholder.toLowerCase()) {
            case "alias" -> {
                return world.getColoredWorldString();
            }
            case "animalspawn" -> {
                return String.valueOf(world.canAnimalsSpawn());
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
                return String.valueOf(world.canMonstersSpawn());
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
                return String.valueOf(world.isPVPEnabled());
            }
            case "seed" -> {
                return String.valueOf(world.getSeed());
            }
            case "time" -> {
                return world.getTime();
            }
            case "type" -> {
                return world.getWorldType().toString().toLowerCase();
            }
            case "weather" -> {
                return String.valueOf(world.isWeatherEnabled());
            }
            default -> {
                warning("Unknown placeholder: " + placeholder);
                return null;
            }
        }
    }
}
