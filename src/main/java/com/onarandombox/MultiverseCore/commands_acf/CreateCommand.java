package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commands_helper.WorldFlags;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CreateCommand extends MultiverseCommand {

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.info")
    @Syntax("<name> <env> -s [seed] -g [generator[:id]] -t [worldtype] [-n] -a [true|false]")
    @CommandCompletion("")
    @Description("Creates a new world and loads it.")
    public void onCreateCommand(@NotNull CommandSender sender,
                                @NotNull @Conditions("isMVWorld:false|worldFolderExist:false") String worldName,
                                @NotNull World.Environment environment,
                                @NotNull WorldFlags flags) {

        Command.broadcastCommandMessage(sender, "Starting creation of world '" + worldName + "'...");

        // TODO: Should Allow WorldFlags object to be passed directly
        String resultMessage = (!this.plugin.getMVWorldManager().addWorld(
                worldName,
                environment,
                flags.getSeed(),
                flags.getWorldType(),
                flags.isGenerateStructures(),
                flags.getGenerator(),
                flags.isSpawnAdjust())
        )
                ? ChatColor.RED + "Failed to create world! See console for errors."
                : ChatColor.GREEN + "Complete!";

        Command.broadcastCommandMessage(sender, resultMessage);
    }
}
