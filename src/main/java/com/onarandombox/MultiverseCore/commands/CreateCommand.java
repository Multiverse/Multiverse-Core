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
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandTools.WorldFlags;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandAlias("mv")
public class CreateCommand extends MultiverseCommand {

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.create")
    @Syntax("<name> <env> -s [seed] -g [generator[:id]] -t [worldtype] [-n] -a [true|false]")
    @CommandCompletion("@empty @environments") //TODO ACF: Add flags tab-complete
    @Description("Creates a new world and loads it.")
    public void onCreateCommand(@NotNull CommandSender sender,
                                @NotNull @Flags("trim") @Conditions("creatableWorldName") String worldName,
                                @NotNull World.Environment environment,
                                @Nullable @Optional String[] flagsArray) {

        WorldFlags flags = new WorldFlags(sender, this.plugin, flagsArray);

        Command.broadcastCommandMessage(sender, "Starting creation of world '" + worldName + "'...");
        Command.broadcastCommandMessage(sender, (this.plugin.getMVWorldManager().addWorld(
                worldName,
                environment,
                // TODO API: Should Allow WorldFlags object to be passed directly
                flags.getSeed(),
                flags.getWorldType(),
                flags.isGenerateStructures(),
                flags.getGenerator(),
                flags.isSpawnAdjust())
        )
                ? ChatColor.GREEN + "Complete!"
                : ChatColor.RED + "Failed! See console for errors.");
    }
}
