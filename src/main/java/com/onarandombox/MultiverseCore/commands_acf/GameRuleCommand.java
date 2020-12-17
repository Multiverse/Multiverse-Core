package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class GameRuleCommand extends MultiverseCommand {

    public GameRuleCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("gamerule")
    @CommandPermission("multiverse.core.gamerule.set")
    @Syntax("<rule> <value> [world]")
    @CommandCompletion("@gameRules")
    @Description("Allows a player to set a gamerule for a given world.")
    public void onGameRuleCommand(@NotNull CommandSender sender,
                                      @NotNull GameRule gameRule,
                                      //TODO: Need to validate value.
                                      @NotNull String value,
                                      @NotNull World world) {

        //TODO: Set actual gameRule.
        sender.sendMessage(gameRule.getName());
        sender.sendMessage(gameRule.getType().getName());
        sender.sendMessage(value);
        sender.sendMessage(world.getName());
    }
}
