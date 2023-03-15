package com.onarandombox.MultiverseCore.placeholders;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

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

    /*
    Placeholder implementation, format: %multiverse-core_{world}_<placeholder>%
    world is optional
     */
    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        final MVWorldManager worldManager = plugin.getMVWorldManager();

        // Fail with offline players
        Player player;
        if (Bukkit.getOnlinePlayers().contains(offlinePlayer)) {
            player = (Player) offlinePlayer;
        } else {
            return null;
        }


        // Split string in to an Array wt the underscores
        String[] paramsArray = params.split("_");

        // Initialise "world" with players current world by default...
        MVWorld world = worldManager.getMVWorld(player.getWorld().getName());

        // ...But if the world has been defined, define it as that
        if (paramsArray.length > 1) {

            // Loop through all but the last item in the list and add that to an array then turn that array as world names can contain underscores
            ArrayList<String> userDefinedWorldNameList = new ArrayList<>();
            // Might not be the neatest code, but it's more readable
            for (int i = 0; i < paramsArray.length-1; i++) {
                userDefinedWorldNameList.add(paramsArray[i]);
            }

            // Concatenate the ArrayList to a string
            String userDefinedWorldName = String.join("_", userDefinedWorldNameList);

            world = worldManager.getMVWorld(userDefinedWorldName);
        }


        // Null check as not all worlds are from multiverse D:
        if (world == null) {
            // Advise user that the world defined does not exist
            if (paramsArray.length > 1) {
                warning("MVCorei18n.PLACEHOLDER_WARN_NON_EXISTENT"); //TODO i18n
            }
            // Tell user that the world they are in is not a Multiverse world
            else {
                warning("MVCorei18n.PLACEHOLDER_WARN_NON_MV"); //TODO i18n
            }
            return null;
        }

        // Switch to find what specific placeholder we want
        switch (paramsArray[paramsArray.length-1]) {
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
            case "flight" -> {
                return String.valueOf(world.getAllowFlight());
            }
            case "playerLimit" -> {
                return String.valueOf(world.getPlayerLimit());
            }
            case "animalSpawn" -> {
                return String.valueOf(world.canAnimalsSpawn());
            }
            case "monstersSpawn" -> {
                return String.valueOf(world.canMonstersSpawn());
            }
            case "pvp" -> {
                return String.valueOf(world.isPVPEnabled());
            }
            case "weather" -> {
                return String.valueOf(world.isWeatherEnabled());
            }
            case "hunger" -> {
                return String.valueOf(world.getHunger());
            }
            case "autoHeal" -> {
                return String.valueOf(world.getAutoHeal());
            }

            default -> {
                return null;
            }
        }
    }
}
