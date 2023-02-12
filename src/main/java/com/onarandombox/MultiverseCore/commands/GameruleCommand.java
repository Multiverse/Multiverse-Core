package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class GameruleCommand extends MultiverseCoreCommand {
    public GameruleCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("gamerule")
    @CommandPermission("multiverse.core.gamerule")
    @CommandCompletion("@gamerules True|False|@range:1-10 @mvworlds|*")
    @Syntax("<Gamerule> <Gamerule value> [World or *]")
    @Description("Changes a gamerule in 1 world")
    public void onGameruleCommand(CommandIssuer issuer,

                                @Syntax("<Gamerule>")
                                @Description("Gamerule to set")
                                String gamerule,

                                @Syntax("<Value>")
                                @Description("Value of gamerule")
                                String value,

                                @Optional
                                @Syntax("[World or *]")
                                @Description("World to apply gamerule to, current world by default")
                                String worldOrAll


    ) {
        MVWorld gameruleChangingInWorld = null;

        final GameRule gameRuleObject = GameRule.getByName(gamerule);

        // All worlds
        if (worldOrAll.equals("*")) {

            ArrayList<Boolean> successfulGameruleAdding = new ArrayList<Boolean>();
            for(World world : Bukkit.getWorlds()) {



                // Set gamerules and add false to list if it fails
                if (!world.setGameRule(gameRuleObject, value)) {
                    issuer.sendMessage(ChatColor.RED + "Failed to set gamerule " + gamerule + " to " + value + " in " + world.getName() + ". It should be a " + gameRuleObject.getType());
                    successfulGameruleAdding.add(false);
                } else {
                    successfulGameruleAdding.add(true);
                }
            }

            // If all successful tell user that the task successfully completed
            if (!successfulGameruleAdding.contains(false)) {
                issuer.sendMessage(ChatColor.GREEN + "Successfully set " + gamerule + " to " + value + " in all worlds.");
            }
        }
        // Single world
        else {
            gameruleChangingInWorld = worldManager.getMVWorld(worldOrAll);

            // Change the game rule, send an error if it fails telling the user what data type is expected
            if (gameruleChangingInWorld.getCBWorld().setGameRule(gameRuleObject, value)) {
                issuer.sendMessage(ChatColor.GREEN + "Successfully set " + gamerule + " to " + value + " in " + gameruleChangingInWorld.getName());
            } else {
                issuer.sendMessage(ChatColor.RED + "Failed to set " + gamerule + " to " + value + ". Expected a " + gameRuleObject.getType());
            }
        }
    }
}
