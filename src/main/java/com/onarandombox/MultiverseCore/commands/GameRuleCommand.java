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
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
@Subcommand("gamerule")
public class GameRuleCommand extends MultiverseCommand {

    public GameRuleCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.gamerule.list")
    @Syntax("[world]")
    @CommandCompletion("@MVWorlds")
    @Description("See the list gamerules values for a given world.")
    public void onGameRulesCommand(@NotNull CommandSender sender,
                                   @NotNull @Flags("other,defaultself") MultiverseWorld world) {

        World CBWorld = world.getCBWorld();
        StringBuilder gameRules = new StringBuilder();

        for (String gameRule : CBWorld.getGameRules()) {
            if (gameRules.length() != 0) {
                gameRules.append(ChatColor.WHITE).append(", ");
            }
            gameRules.append(ChatColor.AQUA)
                    .append(gameRule)
                    .append(ChatColor.WHITE)
                    .append(": ")
                    .append(ChatColor.GREEN)
                    .append(CBWorld.getGameRuleValue(GameRule.getByName(gameRule)));
        }

        sender.sendMessage("=== Gamerules for " + ChatColor.AQUA + world.getName() + ChatColor.WHITE + " ===");
        sender.sendMessage(gameRules.toString());
    }

    @Subcommand("set")
    @CommandPermission("multiverse.core.gamerule.set")
    @Syntax("<rule> <value> [world]")
    @CommandCompletion("@gameRules @empty @MVWorlds")
    @Description("Allows a player to set a gamerule for a given world.")
    public <T> void onGameRuleChangeCommand(@NotNull CommandSender sender,
                                            @NotNull Player player,
                                            @NotNull GameRuleProperty<T> property,
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
