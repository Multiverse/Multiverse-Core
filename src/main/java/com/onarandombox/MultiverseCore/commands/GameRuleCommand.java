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
import com.onarandombox.MultiverseCore.commandtools.contexts.GameRuleProperty;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.ContentFilter;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import com.onarandombox.MultiverseCore.displaytools.DisplaySetting;
import com.onarandombox.MultiverseCore.displaytools.DisplaySettings;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@CommandAlias("mv")
@Subcommand("gamerule")
public class GameRuleCommand extends MultiverseCoreCommand {

    public GameRuleCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.gamerule.list")
    @Syntax("[world] [filter]")
    @CommandCompletion("@MVWorlds")
    @Description("See the list gamerules values for a given world.")
    public void onGameRulesCommand(@NotNull CommandSender sender,

                                   @NotNull
                                   @Syntax("[world]")
                                   @Description("World you want to see game rule info.")
                                   @Flags("other,defaultself, fallbackself") MultiverseWorld world,

                                   @NotNull ContentFilter filter) {

        new ContentDisplay.Builder<Map<String, Object>>()
                .sender(sender)
                .header("=== Gamerules for %s%s%s ===", ChatColor.AQUA, world.getName(), ChatColor.WHITE)
                .contents(getGameRuleMap(world))
                .displayHandler(DisplayHandlers.INLINE_MAP)
                .colorTool(ColorAlternator.with(ChatColor.GREEN, ChatColor.GOLD))
                .setting(DisplaySettings.OPERATOR, ": ")
                .filter(filter)
                .display();
    }

    private Map<String, Object> getGameRuleMap(MultiverseWorld world) {
        return new HashMap<String, Object>() {{
            Arrays.stream(GameRule.values())
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

        sender.sendMessage((world.getCBWorld().setGameRule(property.getGameRule(), property.getValue()))

                ? String.format("%sSuccess! %sGamerule %s%s%s was set to %s%s%s.",
                ChatColor.GREEN, ChatColor.WHITE, ChatColor.AQUA, property.getGameRule().getName(),
                ChatColor.WHITE, ChatColor.GREEN, property.getValue(), ChatColor.WHITE)

                : String.format("%sFailure! Gamerule %s%s%s could not be set to %s%s%s.",
                ChatColor.RED, ChatColor.AQUA, property.getGameRule().getName(),
                ChatColor.RED, ChatColor.DARK_RED, property.getValue(), ChatColor.RED));
    }
}
