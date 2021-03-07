/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import com.onarandombox.MultiverseCore.displaytools.DisplaySettings;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CommandAlias("mv")
public class InfoCommand extends MultiverseCoreCommand {

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("info")
    @CommandPermission("multiverse.core.info")
    @Syntax("[world] [page]")
    @CommandCompletion("@MVWorlds @range:1-5")
    @Description("Display detailed information of the world.")
    public void onInfoCommand(@NotNull CommandSender sender,

                              @NotNull
                              @Syntax("[world]")
                              @Description("World you want to see info.")
                              @Flags("other,defaultself,fallbackself") MultiverseWorld world,

                              @Syntax("[page]")
                              @Description("Info page to display.")
                              @Default("1") int page) {

        new ContentDisplay.Builder<Collection<String>>()
                .sender(sender)
                .contents(buildWorldInfoContent(world))
                .displayHandler(DisplayHandlers.PAGE_LIST)
                .colorTool(ColorAlternator.with(ChatColor.YELLOW, ChatColor.AQUA)).setting(DisplaySettings.SHOW_PAGE, page)
                .display();
    }

    private List<String> buildWorldInfoContent(MultiverseWorld world) {
        List<String> contents = new ArrayList<>(38);

        // Page 1
        contents.add(parseHeader("General Info"));
        contents.add(String.format("World Name: %s%s", ChatColor.WHITE, world.getName()));
        contents.add(String.format("World Alias: %s%s", ChatColor.WHITE,  world.getColoredWorldString()));
        contents.add(String.format("Game Mode: %s%s", ChatColor.WHITE, world.getGameMode().toString()));
        contents.add(String.format("Difficulty: %s%s", ChatColor.WHITE, world.getDifficulty().toString()));
        contents.add(String.format("Spawn Location: %s%s", ChatColor.WHITE, this.plugin.getLocationManipulation().strCoords(world.getSpawnLocation())));
        contents.add(String.format("World Seed: %s%s", ChatColor.WHITE, world.getSeed()));
        String priceString = (world.getPrice() == 0)
                ? String.format("%sFREE!", ChatColor.GREEN)
                : plugin.getEconomist().formatPrice(-world.getPrice(), world.getCurrency());
        contents.add(String.format((world.getPrice() >= 0)
                ? "Price to enter this world: %s%s"
                : "Reward for entering this world: %s%s", ChatColor.WHITE, priceString));
        World respawnWorld = world.getRespawnToWorld();
        MultiverseWorld respawn = this.plugin.getMVWorldManager().getMVWorld(respawnWorld);
        String respawnWorldString = (respawn != null)
                ? respawn.getColoredWorldString()
                : ChatColor.RED + "!!INVALID!!";
        contents.add(String.format("Players will respawn in: %s%s", ChatColor.WHITE, respawnWorldString));
        contents.add(ContentDisplay.LINE_BREAK);

        // Page 2
        contents.add(parseHeader("More World Settings"));
        contents.add(String.format("World UID: %s%s", ChatColor.WHITE, world.getCBWorld().getUID()));
        contents.add(String.format("World Type: %s%s", ChatColor.WHITE, world.getWorldType().toString()));
        contents.add(String.format("World Scale: %s%s", ChatColor.WHITE, world.getScaling()));
        contents.add(String.format("Generator: %s%s", ChatColor.WHITE, world.getGenerator()));
        contents.add(String.format("Structures: %s%s", ChatColor.WHITE, world.getCBWorld().canGenerateStructures()));
        contents.add(String.format("Weather: %s%s", ChatColor.WHITE, world.isWeatherEnabled()));
        contents.add(String.format("Players will get hungry: %s%s", ChatColor.WHITE, world.getHunger()));
        contents.add(String.format("Keep spawn in memory: %s%s", ChatColor.WHITE, world.isKeepingSpawnInMemory()));
        contents.add(ContentDisplay.LINE_BREAK);

        // Page 3
        contents.add(parseHeader("PVP Settings"));
        contents.add(String.format("Multiverse Setting: %s%s", ChatColor.WHITE, world.isPVPEnabled()));
        contents.add(String.format("Bukkit Setting: %s%s", ChatColor.WHITE, world.getCBWorld().getPVP()));
        contents.add(ContentDisplay.LINE_BREAK);

        // Page 4
        contents.add(parseHeader("Monster Settings"));
        contents.add(String.format("Multiverse Setting: %s%s", ChatColor.WHITE, world.canMonstersSpawn()));
        contents.add(String.format("Bukkit Setting: %s%s", ChatColor.WHITE, world.getCBWorld().getAllowMonsters()));
        contents.add((world.canMonstersSpawn())
                ? String.format("Monsters that %scannot spawn: %s%s", ChatColor.RED, ChatColor.WHITE, toCommaSeparated(world.getMonsterList()))
                : String.format("Monsters that %scan spawn: %s%s", ChatColor.GREEN, ChatColor.WHITE, toCommaSeparated(world.getMonsterList())));
        contents.add(ContentDisplay.LINE_BREAK);

        // Page 5
        contents.add(parseHeader("Animal Settings"));
        contents.add(String.format("Multiverse Setting: %s%s", ChatColor.WHITE, world.canAnimalsSpawn()));
        contents.add(String.format("Bukkit Setting: %s%s", ChatColor.WHITE, world.getCBWorld().getAllowAnimals()));
        contents.add((world.canAnimalsSpawn())
                ? String.format("Animals that %scannot spawn: %s%s", ChatColor.RED, ChatColor.WHITE, toCommaSeparated(world.getAnimalList()))
                : String.format("Animals that %scan spawn: %s%s", ChatColor.GREEN, ChatColor.WHITE, toCommaSeparated(world.getAnimalList())));

        return contents;
    }

    private String parseHeader(String header) {
        return String.format("%s===[ %s %s]===", ChatColor.AQUA, header, ChatColor.AQUA);
    }

    private static String toCommaSeparated(List<String> list) {
        if (list == null || list.size() == 0) {
            return ChatColor.GRAY + ChatColor.ITALIC.toString() + "none";
        }
        if (list.size() == 1) {
            return list.get(0);
        }

        StringBuilder result = new StringBuilder(list.get(0));
        for (int i = 1; i < list.size() - 1; i++) {
            result.append(", ").append(list.get(i));
        }
        result.append(" and ").append(list.get(list.size() - 1));

        return result.toString();
    }
}
