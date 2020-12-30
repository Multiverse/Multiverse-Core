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
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.GameRuleProperty;
import com.onarandombox.MultiverseCore.commandTools.display.ColourAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import com.onarandombox.MultiverseCore.commandTools.display.kvpair.KeyValueDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@CommandAlias("mv")
@Subcommand("gamerule")
public class GameRuleCommand extends MultiverseCommand {

    public GameRuleCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.gamerule.list")
    @Syntax("[world] [filter]")
    @CommandCompletion("@MVWorlds")
    @Description("See the list gamerules values for a given world.")
    public void onGameRulesCommand(@NotNull CommandSender sender,

                                   @Syntax("[world]")
                                   @Description("World you want to see game rule info.")
                                   @NotNull @Flags("other,defaultself, fallbackself") MultiverseWorld world,

                                   @NotNull ContentFilter filter) {

        KeyValueDisplay display = new KeyValueDisplay(
                this.plugin,
                sender,
                "=== Gamerules for " + ChatColor.AQUA + world.getName() + ChatColor.WHITE + " ===",
                getGameRuleMap(world),
                filter,
                new ColourAlternator(ChatColor.GREEN, ChatColor.GOLD),
                ": "
        );

        display.showContentAsync();
    }

    private ContentCreator<Map<String, Object>> getGameRuleMap(MultiverseWorld world) {
        return () -> new HashMap<String, Object>() {{
            Arrays.stream(GameRule.values())
                    .unordered()
                    .forEach(gr -> {
                        Object value = world.getCBWorld().getGameRuleValue(gr);
                        if (value != null) {
                            put(gr.getName(), value.toString());
                        }
                    });
        }};
    }

    @Subcommand("set")
    @CommandPermission("multiverse.core.gamerule.set")
    @Syntax("<rule> <value> [world]")
    @CommandCompletion("@gameRules @empty @MVWorlds")
    @Description("Allows a player to set a gamerule for a given world.")
    public <T> void onGameRuleChangeCommand(@NotNull CommandSender sender,
                                            @NotNull Player player,

                                            @Syntax("<rule> <value>")
                                            @Description("Game rule property and value you want to change to.")
                                            @NotNull GameRuleProperty<T> property,

                                            @Syntax("[world]")
                                            @Description("World you want to set this game rule.")
                                            @NotNull @Flags("other,defaultself") MultiverseWorld world) {

        if (world.getCBWorld().setGameRule(property.getGameRule(), property.getValue())) {
            sender.sendMessage(ChatColor.GREEN + "Success!" + ChatColor.WHITE + " Gamerule " + ChatColor.AQUA
                    + property.getGameRule().getName() + ChatColor.WHITE + " was set to " + ChatColor.GREEN
                    + property.getValue() + ChatColor.WHITE + ".");
        }
        else {
            sender.sendMessage(ChatColor.RED + "Failure!" + ChatColor.WHITE + " Gamerule "
                    + ChatColor.AQUA + property.getGameRule().getName() + ChatColor.WHITE
                    + " could not be set to " + ChatColor.RED + property.getValue() + ChatColor.WHITE + ", ");
        }
    }
}
