package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
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
    @CommandCompletion("@gamerules True|False|@range:1-10 @mvworlds|*")
    @Syntax("<Gamerule> <Gamerule value> [World or *]")
    @Description("Changes a gamerule in one or more worlds")
    public void onGameruleCommand(BukkitCommandIssuer issuer,

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

        final GameRule gameRuleObject = GameRule.getByName(gamerule);

        // Single world
        if (!worldOrAll.equals("*")) {

            final MVWorld worldToChangeGameruleIn = worldManager.getMVWorld(worldOrAll);

            // Confirm inputted world exists, no clue why IntelliJ thinks it cannot be null...
            if (worldToChangeGameruleIn.equals(null)) {
                issuer.sendMessage(ChatColor.RED + "World: " + worldOrAll + " does not exist");
            }

            // Change the game rule, send an error if it fails telling the user what data type is expected
            if (worldToChangeGameruleIn.getCBWorld().setGameRule(gameRuleObject, value)) {
                issuer.sendMessage(ChatColor.GREEN + "Successfully set " + gamerule + " to " + value + " in " + worldToChangeGameruleIn.getName());
            } else {
                issuer.sendMessage(ChatColor.RED + "Failed to set " + gamerule + " to " + value + ". Expected a " + gameRuleObject.getType());
            }

        // All worlds
        } else {
            boolean success = true;

            for(MVWorld world : worldManager.getMVWorlds()) {

                // Set gamerules and add false to list if it fails
                if (!world.getCBWorld().setGameRule(gameRuleObject, value)) {
                    issuer.sendMessage(ChatColor.RED + "Failed to set gamerule " + gamerule + " to " + value + " in " + world.getName() + ". It should be a " + gameRuleObject.getType());
                    success = false;
                }
            }
            // Tell user if it was successful
            if (success) {
                issuer.sendMessage(ChatColor.GREEN + "Successfully set " + gamerule + " to " + value + " in all worlds.");
            }
        }
    }
}
