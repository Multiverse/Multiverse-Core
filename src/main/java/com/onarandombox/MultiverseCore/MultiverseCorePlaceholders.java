package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiverseCorePlaceholders extends PlaceholderExpansion {

    private final MultiverseCore plugin;


    public MultiverseCorePlaceholders(MultiverseCore plugin) {
        this.plugin = plugin;
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

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {

        // Fail with offline players
        Player player;
        if (Bukkit.getOnlinePlayers().contains(offlinePlayer)) {
            player = (Player) offlinePlayer;
        } else {
            return null;
        }

        final MVWorldManager worldManager = plugin.getMVWorldManager();
        final MVWorld world = worldManager.getMVWorld(player.getWorld().getName());

        // Null check as not all worlds are from multiverse D;
        if (world == null) {
            return null;
        }


        switch (params) {
            case "alias" -> {
                return world.getColoredWorldString();
            }
            case "name" -> {
                return world.getName();
            }
            case "generator" -> {
                return world.getGenerator();
            }
            case "type" -> {
                return world.getEnvironment().toString().toLowerCase();
            }
            case "price" -> {
                return String.valueOf(world.getPrice());
            }
            case "currency" -> {
                return String.valueOf(world.getCurrency());
            }
            case "difficulty" -> {
                return world.getDifficulty().toString();
            }
            case "seed" -> {
                return String.valueOf(world.getSeed());
            }
            case "time" -> {
                return world.getTime();
            }
            case "gamemode" -> {
                return world.getGameMode().toString().toLowerCase();
            }
            case "property_flight" -> {
                return String.valueOf(world.getAllowFlight());
            }
            case "property_playerLimit" -> {
                return String.valueOf(world.getPlayerLimit());
            }
            case "property_animalSpawn" -> {
                return String.valueOf(world.canAnimalsSpawn());
            }
            case "property_monstersSpawn" -> {
                return String.valueOf(world.canMonstersSpawn());
            }
            case "property_pvp" -> {
                return String.valueOf(world.isPVPEnabled());
            }
            case "property_weather" -> {
                return String.valueOf(world.isWeatherEnabled());
            }
            case "property_hunger" -> {
                return String.valueOf(world.getHunger());
            }
            case "property_autoHeal" -> {
                return String.valueOf(world.getAutoHeal());
            }

            default -> {
                return null;
            }
        }
    }
}
