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
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class DeleteCommand extends MultiverseCommand {

    public DeleteCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @Syntax("<world>")
    @CommandCompletion("@MVWorlds|@unloadedWorlds")
    @Description("Deletes a world on your server PERMANENTLY.")
    public void onDeleteCommand(@NotNull CommandSender sender,

                                @Syntax("<world>")
                                @Description("Multiverse world you want to delete.")
                                @NotNull
                                @Single
                                @Flags("trim")
                                @Conditions("isWorldInConfig|validWorldFolder") String worldName) {

        this.plugin.getMVCommandManager().getQueueManager().addToQueue(
                sender,
                deleteRunnable(sender, worldName),
                String.format("Are you sure you want to delete world '%s'?", worldName)
        );
    }

    private Runnable deleteRunnable(@NotNull CommandSender sender,
                                    @NotNull String worldName) {

        return () -> {
            sender.sendMessage(String.format("Deleting world '%s'...", worldName));
            sender.sendMessage((this.plugin.getMVWorldManager().deleteWorld(worldName))
                    ? ChatColor.GREEN + "World '" + worldName + "' is deleted!"
                    : ChatColor.RED + "World '" + worldName + "' could not be deleted!");
        };
    }
}
