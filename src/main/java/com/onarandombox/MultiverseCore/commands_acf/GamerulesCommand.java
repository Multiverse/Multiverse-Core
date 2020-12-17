package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class GamerulesCommand extends MultiverseCommand {

    public GamerulesCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("info")
    @CommandPermission("multiverse.core.gamerule.list")
    @Syntax("[world]")
    @CommandCompletion("@MVWorlds")
    @Description("See the list gamerules values for a given world.")
    public void onGamerulesCommand(@NotNull CommandSender sender,
                                   @NotNull @Flags("other|defaultself") MultiverseWorld world) {

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
}
