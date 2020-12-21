package com.onarandombox.MultiverseCore.commands_acf;

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
import com.onarandombox.MultiverseCore.commands_helper.ColourAlternator;
import com.onarandombox.MultiverseCore.commands_helper.PageDisplay;
import com.onarandombox.MultiverseCore.utils.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("mv")
public class InfoCommand extends MultiverseCommand {

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("info")
    @CommandPermission("multiverse.core.info")
    @Syntax("[world] [page]")
    @CommandCompletion("@MVWorlds @range:1-3")
    @Description("")
    public void onInfoCommand(@NotNull CommandSender sender,
                              @NotNull @Flags("other,defaultself,fallbackself") MultiverseWorld world,
                              @Default("1") int page) {

        PageDisplay pageDisplay = new PageDisplay(
                sender,
                buildWorldInfoContent(world),
                page,
                10,
                new ColourAlternator(ChatColor.YELLOW, ChatColor.AQUA)
        );

        pageDisplay.showPageAsync(this.plugin);
    }

    private List<String> buildWorldInfoContent(MultiverseWorld world) {
        List<String> contents = new ArrayList<>(38);

        // Page 1
        contents.add(parseHeader("General Info"));
        contents.add(String.format("World Name: %s%s", ChatColor.WHITE, world.getName()));
        contents.add(String.format("World Alias: %s%s", ChatColor.WHITE,  world.getColoredWorldString()));
        contents.add(String.format("Game Mode: %s%s", ChatColor.WHITE, world.getGameMode().toString()));
        contents.add(String.format("Difficulty: %s%s", ChatColor.WHITE, world.getDifficulty().toString()));

        Location spawn = world.getSpawnLocation();
        contents.add(String.format("Spawn Location: %s%s", ChatColor.WHITE, this.plugin.getLocationManipulation().strCoords(spawn)));
        contents.add(String.format("World Scale: %s%s", ChatColor.WHITE, world.getScaling()));
        contents.add(String.format("World Seed: %s%s", ChatColor.WHITE, world.getSeed()));

        String priceString = (world.getPrice() == 0)
                ? ChatColor.GREEN + "FREE!"
                : plugin.getEconomist().formatPrice(-world.getPrice(), world.getCurrency());

        contents.add(String.format((world.getPrice() >= 0)
                        ? "Price to enter this world: %s%s"
                        : "Reward for entering this world: %s%s", ChatColor.WHITE, priceString));

        World respawnWorld = world.getRespawnToWorld();
        if (respawnWorld != null) {
            MultiverseWorld respawn = this.plugin.getMVWorldManager().getMVWorld(respawnWorld);
            String respawnWorldString = (respawn != null)
                    ? respawn.getColoredWorldString()
                    : ChatColor.RED + respawnWorld.getName() + " !!INVALID!!";

            contents.add(String.format("Players will respawn in: %s%s", ChatColor.WHITE, respawnWorldString));
        }
        contents.add("%lf%");

        // Page 2
        contents.add(parseHeader("More World Settings"));
        contents.add(String.format("World UID: %s%s", ChatColor.WHITE, world.getCBWorld().getUID()));
        contents.add(String.format("World Type: %s%s", ChatColor.WHITE, world.getWorldType().toString()));
        contents.add(String.format("Generator: %s%s", ChatColor.WHITE, world.getGenerator()));
        contents.add(String.format("Structures: %s%s", ChatColor.WHITE, world.getCBWorld().canGenerateStructures()));
        contents.add(String.format("Weather: %s%s", ChatColor.WHITE, world.isWeatherEnabled()));
        contents.add(String.format("Players will get hungry: %s%s", ChatColor.WHITE, world.getHunger()));
        contents.add(String.format("Keep spawn in memory: %s%s", ChatColor.WHITE, world.isKeepingSpawnInMemory()));
        contents.add("%lf%");

        // Page 3
        contents.add(parseHeader("PVP Settings"));
        contents.add(String.format("Multiverse Setting: %s%s", ChatColor.WHITE, world.isPVPEnabled()));
        contents.add(String.format("Bukkit Setting: %s%s", ChatColor.WHITE, world.getCBWorld().getPVP()));
        contents.add("%lf%");

        // Page 4
        contents.add(parseHeader("Monster Settings"));
        contents.add(String.format("Multiverse Setting: %s%s", ChatColor.WHITE, world.canMonstersSpawn()));
        contents.add(String.format("Bukkit Setting: %s%s", ChatColor.WHITE, world.getCBWorld().getAllowMonsters()));

        if (!world.getMonsterList().isEmpty()){
            contents.add(String.format((world.canMonstersSpawn())
                    ? "Monsters that" + ChatColor.RED + " CAN NOT " + ChatColor.GREEN + "spawn: %s%s"
                    : "Monsters that" + ChatColor.GREEN + " CAN SPAWN: %s%s",
                    ChatColor.WHITE, toCommaSeparated(world.getMonsterList())));
        }
        contents.add("%lf%");

        // Page 5
        contents.add(parseHeader("Animal Settings"));
        contents.add(String.format("Multiverse Setting: %s%s", ChatColor.WHITE, world.canAnimalsSpawn()));
        contents.add(String.format("Bukkit Setting: %s%s", ChatColor.WHITE, world.getCBWorld().getAllowAnimals()));

        if (!world.getAnimalList().isEmpty()){
            contents.add(String.format((world.canMonstersSpawn())
                            ? "Animals that" + ChatColor.RED + " CAN NOT " + ChatColor.GREEN + "spawn: %s%s"
                            : "Animals that" + ChatColor.GREEN + " CAN SPAWN: %s%s",
                    ChatColor.WHITE, toCommaSeparated(world.getAnimalList())));
        }

        return contents;
    }

    private String parseHeader(String header) {
        return String.format("%s--- %s %s%s %s---", ChatColor.AQUA, header, ChatColor.DARK_PURPLE, "%page%", ChatColor.AQUA);
    }

    private static String toCommaSeparated(List<String> list) {
        if (list == null || list.size() == 0) {
            return "";
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
