package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.commandtools.context.GameRuleValue;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class GameruleCommand extends MultiverseCoreCommand {
    public GameruleCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("gamerule")
    @CommandPermission("multiverse.core.gamerule")
    @CommandCompletion("@gamerules true|false|@range:1-10 @mvworlds:multiple|*")
    @Syntax("<Gamerule> <Gamerule value> [World or *]")
    @Description("Changes a gamerule in one or more worlds")
    public void onGameruleCommand(BukkitCommandIssuer issuer,

                                  @Syntax("<Gamerule>")
                                  @Description("Gamerule to set")
                                  GameRule gamerule,

                                  @Syntax("<Value>")
                                  @Description("Value of gamerule")
                                  GameRuleValue gameRuleValue,

                                  @Flags("resolve=issuerAware")
                                  @Syntax("[World or *]")
                                  @Description("World to apply gamerule to, current world by default")
                                  MVWorld[] worlds
    ) {
        Object value = gameRuleValue.getValue();
        boolean success = true;
        for(MVWorld world : worlds) {
            // Set gamerules and add false to list if it fails
            if (!world.getCBWorld().setGameRule(gamerule, value)) {
                issuer.sendMessage(ChatColor.RED + "Failed to set gamerule " + gamerule.getName() + " to " + value + " in " + world.getName() + ". It should be a " + gamerule.getType());
                success = false;
            }
        }
        // Tell user if it was successful
        if (success) {
            if (worlds.length == 1) {
                issuer.sendMessage(ChatColor.GREEN + "Successfully set " + gamerule.getName() + " to " + value + " in " + worlds[0].getName());
            }
            else if (worlds.length > 1) {
                issuer.sendMessage(ChatColor.GREEN + "Successfully set " + gamerule.getName() + " to " + value + " in " + worlds.length + " worlds.");
            }
        }
    }
}
